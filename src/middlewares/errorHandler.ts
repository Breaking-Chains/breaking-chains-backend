import { Request, Response, NextFunction } from 'express';
import { AppError } from '../errors/AppError';
import { ZodError } from 'zod';

export const errorHandler = (
  err: Error,
  _req: Request,
  res: Response,
  _next: NextFunction
): void => {
  if (err instanceof AppError) {
    res.status(err.statusCode).json({
      status: 'error',
      code: err.code,
      message: err.message,
      details: err.details,
    });
    return;
  }

  if (err instanceof ZodError) {
    const issues = err.issues || (err as any).errors || [];
    res.status(400).json({
      status: 'error',
      code: 'VALIDATION_ERROR',
      message: 'Validation failed for request payload',
      details: issues.map((e: any) => ({
        field: Array.isArray(e.path) ? e.path.join('.') : String(e.path),
        message: e.message,
      })),
    });
    return;
  }

  console.error('Unhandled Server Error:', err);

  res.status(500).json({
    status: 'error',
    code: 'INTERNAL_SERVER_ERROR',
    message: 'An unexpected internal server error occurred',
    details: null,
  });
};
