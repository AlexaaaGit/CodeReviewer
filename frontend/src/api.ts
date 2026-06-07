import axios from 'axios';

/**
 * Central axios instance.
 * Automatically attaches Authorization: Bearer <token> from localStorage.
 */
const api = axios.create({
  baseURL: 'http://localhost:8080',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
