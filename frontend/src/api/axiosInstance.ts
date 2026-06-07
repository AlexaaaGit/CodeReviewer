import axios from 'axios';

/**
 * Central axios instance for all API calls.
 * Automatically attaches the JWT token from localStorage to every request.
 */
const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Request interceptor: attach Bearer token if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
