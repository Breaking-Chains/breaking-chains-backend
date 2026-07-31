import { Router } from 'express';
import { AuthController } from './auth.controller';
import { validateRequest } from '../../middlewares/validateRequest';
import { authenticateJwt } from '../../middlewares/authMiddleware';
import {
  registerSchema,
  loginSchema,
  googleAuthSchema,
  refreshTokenSchema,
} from './auth.schema';

const router = Router();

router.post('/register', validateRequest(registerSchema), AuthController.register);
router.post('/login', validateRequest(loginSchema), AuthController.login);
router.post('/google', validateRequest(googleAuthSchema), AuthController.googleAuth);
router.post('/refresh', validateRequest(refreshTokenSchema), AuthController.refresh);
router.post('/logout', authenticateJwt as any, validateRequest(refreshTokenSchema), AuthController.logout);

export default router;
