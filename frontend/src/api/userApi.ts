import api from './axiosInstance';
import type { CommentResponse, ProductResponse, UserResponse, UserUpdateRequest } from '../types';

/**
 * User management and profile API calls.
 */
export const userApi = {
  /** GET /api/users — list all registered users (admin only) */
  getAll: () =>
    api.get<UserResponse[]>('/api/users'),

  /** PUT /api/users/{id}/role — change a user's role (admin only) */
  updateRole: (id: number, data: UserUpdateRequest) =>
    api.put<UserResponse>(`/api/users/${id}/role`, data),

  /** GET /api/users/me/projects — get all projects submitted by the current user */
  getMyProjects: () =>
    api.get<ProductResponse[]>('/api/users/me/projects'),

  /** GET /api/users/me/comments — get all code reviews posted by the current user */
  getMyComments: () =>
    api.get<CommentResponse[]>('/api/users/me/comments'),
};
