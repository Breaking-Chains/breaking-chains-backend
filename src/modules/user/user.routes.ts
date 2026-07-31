import { Router } from 'express';
import { UserController } from './user.controller';
import { authenticateJwt } from '../../middlewares/authMiddleware';
import { validateRequest } from '../../middlewares/validateRequest';
import { updateProfileSchema } from './user.schema';

const router = Router();

router.use(authenticateJwt as any);

router.get('/me', UserController.getProfile as any);
router.put('/me', validateRequest(updateProfileSchema), UserController.updateProfile as any);

export default router;
