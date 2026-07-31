import request from 'supertest';
import app from '../src/app';
import { prisma } from '../src/config/prisma';

// Mock Prisma Client methods
jest.mock('../src/config/prisma', () => ({
  prisma: {
    user: {
      findFirst: jest.fn(),
      findUnique: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
    },
    refreshToken: {
      create: jest.fn(),
      findUnique: jest.fn(),
      delete: jest.fn(),
    },
  },
}));

describe('Auth Endpoints', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /health', () => {
    it('should return health check status ok', async () => {
      const res = await request(app).get('/health');
      expect(res.status).toBe(200);
      expect(res.body.status).toBe('ok');
    });
  });

  describe('POST /api/v1/auth/register', () => {
    it('should register a new user successfully', async () => {
      (prisma.user.findFirst as jest.Mock).mockResolvedValue(null);
      (prisma.user.create as jest.Mock).mockResolvedValue({
        id: 'user_123',
        email: 'test@example.com',
        fullName: 'Test User',
        username: 'testuser',
        avatarUrl: null,
        bio: null,
        authProvider: 'LOCAL',
        createdAt: new Date(),
        updatedAt: new Date(),
      });
      (prisma.refreshToken.create as jest.Mock).mockResolvedValue({ id: 'rt_1' });

      const res = await request(app).post('/api/v1/auth/register').send({
        email: 'test@example.com',
        password: 'Password123!',
        fullName: 'Test User',
        username: 'testuser',
      });

      expect(res.status).toBe(201);
      expect(res.body.status).toBe('success');
      expect(res.body.data.user.email).toBe('test@example.com');
      expect(res.body.data.tokens).toHaveProperty('accessToken');
      expect(res.body.data.tokens).toHaveProperty('refreshToken');
    });

    it('should return 400 when validation fails', async () => {
      const res = await request(app).post('/api/v1/auth/register').send({
        email: 'invalid-email',
        password: '123',
      });

      expect(res.status).toBe(400);
      expect(res.body.status).toBe('error');
      expect(res.body.code).toBe('VALIDATION_ERROR');
    });

    it('should return 409 when user already exists', async () => {
      (prisma.user.findFirst as jest.Mock).mockResolvedValue({
        email: 'existing@example.com',
        username: 'existinguser',
      });

      const res = await request(app).post('/api/v1/auth/register').send({
        email: 'existing@example.com',
        password: 'Password123!',
        fullName: 'Test User',
        username: 'existinguser',
      });

      expect(res.status).toBe(409);
      expect(res.body.status).toBe('error');
      expect(res.body.code).toBe('USER_EXISTS');
    });
  });

  describe('POST /api/v1/auth/login', () => {
    it('should reject login with wrong password', async () => {
      (prisma.user.findUnique as jest.Mock).mockResolvedValue({
        id: 'user_123',
        email: 'test@example.com',
        password: '$2a$10$invalidhashedpassword',
      });

      const res = await request(app).post('/api/v1/auth/login').send({
        email: 'test@example.com',
        password: 'WrongPassword!',
      });

      expect(res.status).toBe(401);
      expect(res.body.code).toBe('INVALID_CREDENTIALS');
    });
  });
});
