import { Request, Response, NextFunction } from 'express';
import { AuthService } from './auth.service';

export class AuthController {
  static register = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const result = await AuthService.register(req.body);
      res.status(201).json({
        status: 'success',
        data: result,
      });
    } catch (error) {
      next(error);
    }
  };

  static login = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const result = await AuthService.login(req.body);
      res.status(200).json({
        status: 'success',
        data: result,
      });
    } catch (error) {
      next(error);
    }
  };

  static googleAuth = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const result = await AuthService.loginWithGoogle(req.body);
      res.status(200).json({
        status: 'success',
        data: result,
      });
    } catch (error) {
      next(error);
    }
  };

  static refresh = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      const result = await AuthService.refreshTokens(req.body.refreshToken);
      res.status(200).json({
        status: 'success',
        data: result,
      });
    } catch (error) {
      next(error);
    }
  };

  static logout = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
    try {
      await AuthService.logout(req.body.refreshToken);
      res.status(200).json({
        status: 'success',
        message: 'Logged out successfully',
      });
    } catch (error) {
      next(error);
    }
  };
}
