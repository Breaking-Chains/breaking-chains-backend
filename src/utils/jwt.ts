import jwt, { Secret } from 'jsonwebtoken';
import { env } from '../config/env';
import { AppError } from '../errors/AppError';

export interface JwtPayload {
  userId: string;
  email: string;
}

export interface RefreshJwtPayload {
  userId: string;
}

export const signAccessToken = (payload: JwtPayload): string => {
  return jwt.sign(payload, env.JWT_ACCESS_SECRET as Secret, {
    expiresIn: env.JWT_ACCESS_EXPIRES_IN as any,
  });
};

export const signRefreshToken = (payload: RefreshJwtPayload): string => {
  return jwt.sign(payload, env.JWT_REFRESH_SECRET as Secret, {
    expiresIn: env.JWT_REFRESH_EXPIRES_IN as any,
  });
};

export const verifyAccessToken = (token: string): JwtPayload => {
  try {
    return jwt.verify(token, env.JWT_ACCESS_SECRET as Secret) as JwtPayload;
  } catch (err: any) {
    if (err.name === 'TokenExpiredError') {
      throw AppError.unauthorized('Access token has expired', 'TOKEN_EXPIRED');
    }
    throw AppError.unauthorized('Invalid access token', 'UNAUTHORIZED');
  }
};

export const verifyRefreshToken = (token: string): RefreshJwtPayload => {
  try {
    return jwt.verify(token, env.JWT_REFRESH_SECRET as Secret) as RefreshJwtPayload;
  } catch (err: any) {
    throw AppError.unauthorized('Invalid or expired refresh token', 'INVALID_REFRESH_TOKEN');
  }
};
