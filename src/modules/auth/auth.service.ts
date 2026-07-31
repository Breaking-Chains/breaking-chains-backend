import bcrypt from 'bcryptjs';
import { OAuth2Client } from 'google-auth-library';
import { prisma } from '../../config/prisma';
import { env } from '../../config/env';
import { AppError } from '../../errors/AppError';
import {
  signAccessToken,
  signRefreshToken,
  verifyRefreshToken,
} from '../../utils/jwt';
import {
  RegisterInput,
  LoginInput,
  GoogleAuthInput,
} from './auth.schema';
import { AuthProvider } from '@prisma/client';

const googleClient = new OAuth2Client(env.GOOGLE_CLIENT_ID);

export class AuthService {
  /**
   * Helper to format User object removing sensitive fields
   */
  private static sanitizeUser(user: any) {
    const { password, ...userWithoutPassword } = user;
    return userWithoutPassword;
  }

  /**
   * Helper to generate Access + Refresh Tokens and save RefreshToken to DB
   */
  private static async generateTokensAndSave(userId: string, email: string) {
    const accessToken = signAccessToken({ userId, email });
    const refreshToken = signRefreshToken({ userId });

    // Set expiration 7 days from now
    const expiresAt = new Date();
    expiresAt.setDate(expiresAt.getDate() + 7);

    await prisma.refreshToken.create({
      data: {
        token: refreshToken,
        userId,
        expiresAt,
      },
    });

    return { accessToken, refreshToken };
  }

  /**
   * Register User with Email and Password
   */
  static async register(input: RegisterInput) {
    const existingUser = await prisma.user.findFirst({
      where: {
        OR: [{ email: input.email }, { username: input.username }],
      },
    });

    if (existingUser) {
      if (existingUser.email === input.email) {
        throw AppError.conflict('An account with this email already exists', 'USER_EXISTS');
      }
      throw AppError.conflict('Username is already taken', 'USER_EXISTS');
    }

    const hashedPassword = await bcrypt.hash(input.password, 10);

    const user = await prisma.user.create({
      data: {
        email: input.email.toLowerCase(),
        password: hashedPassword,
        fullName: input.fullName,
        username: input.username,
        authProvider: AuthProvider.LOCAL,
      },
    });

    const tokens = await this.generateTokensAndSave(user.id, user.email);

    return {
      user: this.sanitizeUser(user),
      tokens,
    };
  }

  /**
   * Login User with Email and Password
   */
  static async login(input: LoginInput) {
    const user = await prisma.user.findUnique({
      where: { email: input.email.toLowerCase() },
    });

    if (!user || !user.password) {
      throw AppError.unauthorized('Invalid email or password', 'INVALID_CREDENTIALS');
    }

    const isPasswordValid = await bcrypt.compare(input.password, user.password);
    if (!isPasswordValid) {
      throw AppError.unauthorized('Invalid email or password', 'INVALID_CREDENTIALS');
    }

    const tokens = await this.generateTokensAndSave(user.id, user.email);

    return {
      user: this.sanitizeUser(user),
      tokens,
    };
  }

  /**
   * Authenticate / Register with Google OAuth ID Token
   */
  static async loginWithGoogle(input: GoogleAuthInput) {
    let payload;
    try {
      const ticket = await googleClient.verifyIdToken({
        idToken: input.idToken,
        audience: env.GOOGLE_CLIENT_ID ? [env.GOOGLE_CLIENT_ID] : undefined,
      });
      payload = ticket.getPayload();
    } catch (error) {
      throw AppError.unauthorized('Invalid or expired Google ID token', 'UNAUTHORIZED');
    }

    if (!payload || !payload.email) {
      throw AppError.unauthorized('Failed to extract profile information from Google ID Token', 'UNAUTHORIZED');
    }

    const { email, name, picture, sub: googleId } = payload;
    const normalizedEmail = email.toLowerCase();

    let user = await prisma.user.findFirst({
      where: {
        OR: [{ googleId }, { email: normalizedEmail }],
      },
    });

    if (user) {
      // Link Google Account if logged in via local before
      if (!user.googleId) {
        user = await prisma.user.update({
          where: { id: user.id },
          data: {
            googleId,
            avatarUrl: user.avatarUrl || picture,
          },
        });
      }
    } else {
      // Generate a unique fallback username based on name/email
      let baseUsername = (name || normalizedEmail.split('@')[0])
        .toLowerCase()
        .replace(/[^a-z0-9_]/g, '');
      if (baseUsername.length < 3) baseUsername = `user_${baseUsername}`;

      let uniqueUsername = baseUsername;
      let counter = 1;
      while (await prisma.user.findUnique({ where: { username: uniqueUsername } })) {
        uniqueUsername = `${baseUsername}_${counter}`;
        counter++;
      }

      user = await prisma.user.create({
        data: {
          email: normalizedEmail,
          fullName: name || 'Google User',
          username: uniqueUsername,
          avatarUrl: picture,
          authProvider: AuthProvider.GOOGLE,
          googleId,
        },
      });
    }

    const tokens = await this.generateTokensAndSave(user.id, user.email);

    return {
      user: this.sanitizeUser(user),
      tokens,
    };
  }

  /**
   * Refresh Tokens with Token Rotation
   */
  static async refreshTokens(refreshToken: string) {
    const payload = verifyRefreshToken(refreshToken);

    const storedToken = await prisma.refreshToken.findUnique({
      where: { token: refreshToken },
      include: { user: true },
    });

    if (!storedToken || storedToken.expiresAt < new Date()) {
      // Cleanup invalid token if exists
      if (storedToken) {
        await prisma.refreshToken.delete({ where: { id: storedToken.id } });
      }
      throw AppError.unauthorized('Refresh token is invalid or expired', 'INVALID_REFRESH_TOKEN');
    }

    // Revoke used refresh token
    await prisma.refreshToken.delete({ where: { id: storedToken.id } });

    // Issue new token pair
    const newTokens = await this.generateTokensAndSave(payload.userId, storedToken.user.email);

    return newTokens;
  }

  /**
   * Logout User (Revoke Refresh Token)
   */
  static async logout(refreshToken: string) {
    try {
      await prisma.refreshToken.delete({
        where: { token: refreshToken },
      });
    } catch {
      // Ignore if token was already revoked/deleted
    }
  }
}
