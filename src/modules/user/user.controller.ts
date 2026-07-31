import { Response, NextFunction } from 'express';
import { AuthenticatedRequest } from '../../middlewares/authMiddleware';
import { UserService } from './user.service';

export class UserController {
  static getProfile = async (req: AuthenticatedRequest, res: Response, next: NextFunction): Promise<void> => {
    try {
      const userId = req.user!.userId;
      const user = await UserService.getProfile(userId);
      res.status(200).json({
        status: 'success',
        data: { user },
      });
    } catch (error) {
      next(error);
    }
  };

  static updateProfile = async (req: AuthenticatedRequest, res: Response, next: NextFunction): Promise<void> => {
    try {
      const userId = req.user!.userId;
      const user = await UserService.updateProfile(userId, req.body);
      res.status(200).json({
        status: 'success',
        data: { user },
      });
    } catch (error) {
      next(error);
    }
  };
}
