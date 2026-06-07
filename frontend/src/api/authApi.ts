import api from './axiosInstance';
import type { JwtResponse } from '../types';

/**
 * Authentication API calls — login and registration.
 */
export const authApi = {
  /** POST /api/auth/login — authenticates user and returns JWT */
  login: (username: string, password: string) =>
    api.post<JwtResponse>('/api/auth/login', { username, password }),

  /** POST /api/auth/register — creates a new user account */
  register: (username: string, password: string) =>
    api.post<string>('/api/auth/register', { username, password }),
};
