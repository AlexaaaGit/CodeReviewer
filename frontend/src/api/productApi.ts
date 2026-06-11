import api from './axiosInstance';
import type { ProductRequest, ProductResponse, PageResponse, ReviewStatus } from '../types';

/**
 * Product API calls — CRUD operations with pagination, search, and category filtering.
 */
export const productApi = {
  /**
   * GET /api/products — paginated list with optional title search and category filter.
   * Server-side pagination: only one page of results is returned at a time.
   */
  getAll: (page: number, size: number, title?: string, categoryId?: number, status?: ReviewStatus) => {
    const params: Record<string, string | number> = { page, size };
    if (title?.trim()) params.title = title.trim();
    if (categoryId) params.categoryId = categoryId;
    if (status && status !== 'ALL') params.status = status;
    return api.get<PageResponse<ProductResponse>>('/api/products', { params });
  },

  /** GET /api/products/{id} — single product with categories and comment count */
  getById: (id: number) =>
    api.get<ProductResponse>(`/api/products/${id}`),

  /** POST /api/products — create a new product (requires auth) */
  create: (data: ProductRequest) =>
    api.post<ProductResponse>('/api/products', data),

  /** PUT /api/products/{id} — update an existing product (requires auth) */
  update: (id: number, data: ProductRequest) =>
    api.put<ProductResponse>(`/api/products/${id}`, data),

  /** DELETE /api/products/{id} — soft-delete a product (admin only) */
  delete: (id: number) =>
    api.delete(`/api/products/${id}`),
};
