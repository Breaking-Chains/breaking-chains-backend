export class AppError extends Error {
  public readonly statusCode: number;
  public readonly code: string;
  public readonly details: any;

  constructor(message: string, statusCode = 400, code = 'BAD_REQUEST', details: any = null) {
    super(message);
    this.statusCode = statusCode;
    this.code = code;
    this.details = details;
    Object.setPrototypeOf(this, new.target.prototype);
    Error.captureStackTrace(this, this.constructor);
  }

  static badRequest(message: string, code = 'BAD_REQUEST', details: any = null) {
    return new AppError(message, 400, code, details);
  }

  static unauthorized(message = 'Unauthorized access', code = 'UNAUTHORIZED', details: any = null) {
    return new AppError(message, 401, code, details);
  }

  static forbidden(message = 'Forbidden access', code = 'FORBIDDEN', details: any = null) {
    return new AppError(message, 403, code, details);
  }

  static notFound(message = 'Resource not found', code = 'NOT_FOUND', details: any = null) {
    return new AppError(message, 404, code, details);
  }

  static conflict(message: string, code = 'CONFLICT', details: any = null) {
    return new AppError(message, 409, code, details);
  }

  static internal(message = 'Internal server error', code = 'INTERNAL_SERVER_ERROR', details: any = null) {
    return new AppError(message, 500, code, details);
  }
}
