import api from './axiosInstance';
import type { CategoryRequest, CategoryResponse } from '../types';

/**
 * Category API calls — CRUD operations for product categories.
 * Listing is public; create/update/delete require admin role.
 */
export const categoryApi = {
  /** GET /api/categories — list all non-deleted categories (public) */
  getAll: () =>
    api.get<CategoryResponse[]>('/api/categories'),

  /** POST /api/categories — create a new category (admin only) */
  create: (data: CategoryRequest) =>
    api.post<CategoryResponse>('/api/categories', data),

  /** PUT /api/categories/{id} — update a category name (admin only) */
  update: (id: number, data: CategoryRequest) =>
    api.put<CategoryResponse>(`/api/categories/${id}`, data),

  /** DELETE /api/categories/{id} — soft-delete a category (admin only) */
  delete: (id: number) =>
    api.delete(`/api/categories/${id}`),
};
