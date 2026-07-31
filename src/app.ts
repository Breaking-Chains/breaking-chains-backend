import express from 'express';
import cors from 'cors';
import authRoutes from './modules/auth/auth.routes';
import userRoutes from './modules/user/user.routes';
import { errorHandler } from './middlewares/errorHandler';
import { AppError } from './errors/AppError';

const app = express();

app.use(cors());
app.use(express.json());

// Health Check Endpoint
app.get('/health', (_req, res) => {
  res.status(200).json({ status: 'ok', timestamp: new Date().toISOString() });
});

// API v1 Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/users', userRoutes);

// 404 Not Found Handler
app.use((_req, _res, next) => {
  next(AppError.notFound('Route not found'));
});

// Global Error Handler
app.use(errorHandler);

export default app;
