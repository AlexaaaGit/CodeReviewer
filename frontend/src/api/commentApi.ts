import api from './axiosInstance';
import type { CommentRequest, CommentResponse } from '../types';

/**
 * Comment API calls — CRUD operations nested under products.
 */
export const commentApi = {
  /** GET /api/products/{productId}/comments — list all comments for a product */
  getByProductId: (productId: number) =>
    api.get<CommentResponse[]>(`/api/products/${productId}/comments`),

  /** POST /api/products/{productId}/comments — add a comment (requires auth) */
  create: (productId: number, data: CommentRequest) =>
    api.post<CommentResponse>(`/api/products/${productId}/comments`, data),

  /** PUT /api/products/{productId}/comments/{commentId} — edit a comment */
  update: (productId: number, commentId: number, data: CommentRequest) =>
    api.put<CommentResponse>(`/api/products/${productId}/comments/${commentId}`, data),

  /** DELETE /api/products/{productId}/comments/{commentId} — soft-delete (admin only) */
  delete: (productId: number, commentId: number) =>
    api.delete(`/api/products/${productId}/comments/${commentId}`),
};
