/**
 * Shared TypeScript interfaces matching the backend DTOs and entities.
 * All API responses and request bodies are typed here for type safety.
 */

// ==================== Product ====================

export interface ProductResponse {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  isDeleted: boolean;
  creationDate: string;
  creatorUserId: number | null;
  categories: CategoryResponse[];
  commentCount: number;
}

export interface ProductRequest {
  title: string;
  description: string;
  imageUrl?: string;
  categoryIds?: number[];
}

// Spring Boot Page<T> response shape
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // current page (0-indexed)
}

// ==================== Comment ====================

export interface CommentResponse {
  id: number;
  description: string;
  creationDate: string;
  creatorUserId: number | null;
  authorUsername: string | null;
  authorRole: string | null;
  productId: number;
}

export interface CommentRequest {
  description: string;
}

// ==================== Category ====================

export interface CategoryResponse {
  id: number;
  name: string;
}

export interface CategoryRequest {
  name: string;
}

// ==================== User ====================

export interface UserResponse {
  id: number;
  username: string;
  role: string;
}

export interface UserUpdateRequest {
  role: string;
}

// ==================== Auth ====================

export interface AuthUser {
  id: number;
  username: string;
  role: 'ROLE_JUNIOR' | 'ROLE_MENTOR' | 'ROLE_ADMIN';
}

export interface JwtResponse {
  token: string;
  id: number;
  username: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}
