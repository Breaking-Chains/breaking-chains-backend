import { z } from 'zod';

export const updateProfileSchema = z.object({
  fullName: z.string().min(2, 'Full name must be at least 2 characters long').optional(),
  username: z
    .string()
    .min(3, 'Username must be at least 3 characters long')
    .max(30, 'Username must not exceed 30 characters')
    .regex(/^[a-zA-Z0-9_]+$/, 'Username can only contain letters, numbers, and underscores')
    .optional(),
  bio: z.string().max(500, 'Bio cannot exceed 500 characters').nullable().optional(),
  avatarUrl: z.string().url('Invalid URL format for avatar').nullable().optional(),
});

export type UpdateProfileInput = z.infer<typeof updateProfileSchema>;
