import { prisma } from '../../config/prisma';
import { AppError } from '../../errors/AppError';
import { UpdateProfileInput } from './user.schema';

export class UserService {
  private static sanitizeUser(user: any) {
    const { password, ...userWithoutPassword } = user;
    return userWithoutPassword;
  }

  static async getProfile(userId: string) {
    const user = await prisma.user.findUnique({
      where: { id: userId },
    });

    if (!user) {
      throw AppError.notFound('User profile not found', 'NOT_FOUND');
    }

    return this.sanitizeUser(user);
  }

  static async updateProfile(userId: string, input: UpdateProfileInput) {
    if (input.username) {
      const existingUser = await prisma.user.findFirst({
        where: {
          username: input.username,
          NOT: { id: userId },
        },
      });

      if (existingUser) {
        throw AppError.conflict('Username is already taken', 'USER_EXISTS');
      }
    }

    const updatedUser = await prisma.user.update({
      where: { id: userId },
      data: {
        ...(input.fullName !== undefined && { fullName: input.fullName }),
        ...(input.username !== undefined && { username: input.username }),
        ...(input.bio !== undefined && { bio: input.bio }),
        ...(input.avatarUrl !== undefined && { avatarUrl: input.avatarUrl }),
      },
    });

    return this.sanitizeUser(updatedUser);
  }
}
