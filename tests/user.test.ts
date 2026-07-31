import request from 'supertest';
import app from '../src/app';
import { prisma } from '../src/config/prisma';
import { signAccessToken } from '../src/utils/jwt';

jest.mock('../src/config/prisma', () => ({
  prisma: {
    user: {
      findUnique: jest.fn(),
      findFirst: jest.fn(),
      update: jest.fn(),
    },
  },
}));

describe('User Profile Endpoints', () => {
  const userId = 'usr_test_123';
  const token = signAccessToken({ userId, email: 'john@example.com' });

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/v1/users/me', () => {
    it('should return current user profile when token is valid', async () => {
      (prisma.user.findUnique as jest.Mock).mockResolvedValue({
        id: userId,
        email: 'john@example.com',
        fullName: 'John Doe',
        username: 'johndoe',
        avatarUrl: null,
        bio: 'Breaking chains',
        authProvider: 'LOCAL',
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const res = await request(app)
        .get('/api/v1/users/me')
        .set('Authorization', `Bearer ${token}`);

      expect(res.status).toBe(200);
      expect(res.body.status).toBe('success');
      expect(res.body.data.user.id).toBe(userId);
      expect(res.body.data.user.username).toBe('johndoe');
    });

    it('should return 401 when Authorization header is missing', async () => {
      const res = await request(app).get('/api/v1/users/me');

      expect(res.status).toBe(401);
      expect(res.body.code).toBe('UNAUTHORIZED');
    });
  });

  describe('PUT /api/v1/users/me', () => {
    it('should update user profile successfully', async () => {
      (prisma.user.findFirst as jest.Mock).mockResolvedValue(null);
      (prisma.user.update as jest.Mock).mockResolvedValue({
        id: userId,
        email: 'john@example.com',
        fullName: 'John Updated',
        username: 'john_updated',
        avatarUrl: null,
        bio: 'Updated bio',
        authProvider: 'LOCAL',
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      const res = await request(app)
        .put('/api/v1/users/me')
        .set('Authorization', `Bearer ${token}`)
        .send({
          fullName: 'John Updated',
          username: 'john_updated',
          bio: 'Updated bio',
        });

      expect(res.status).toBe(200);
      expect(res.body.status).toBe('success');
      expect(res.body.data.user.fullName).toBe('John Updated');
      expect(res.body.data.user.username).toBe('john_updated');
    });

    it('should return 409 when username is taken by another user', async () => {
      (prisma.user.findFirst as jest.Mock).mockResolvedValue({
        id: 'usr_other_999',
        username: 'taken_username',
      });

      const res = await request(app)
        .put('/api/v1/users/me')
        .set('Authorization', `Bearer ${token}`)
        .send({
          username: 'taken_username',
        });

      expect(res.status).toBe(409);
      expect(res.body.code).toBe('USER_EXISTS');
    });
  });
});
