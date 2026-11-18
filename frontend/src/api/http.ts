import axios from 'axios';
import { clearSession, readSession } from '../utils/session';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const session = readSession();
  if (session?.accessToken) {
    config.headers.Authorization = `Bearer ${session.accessToken}`;
  }
  return config;
});

http.interceptors.response.use(
  response => response,
  error => {
    if (error?.response?.status === 401) {
      clearSession();
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    const message = error?.response?.data?.message ?? error?.message ?? '请求失败';
    return Promise.reject(new Error(message));
  }
);

export default http;
