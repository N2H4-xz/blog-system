import axios from 'axios';
import type { ApiResponse, AuthPayload, CurrentUser } from '../types';
import { clearSession, getRefreshToken, getToken, setSession } from '../store/auth';

export const api = axios.create({
  baseURL: '/api',
});

api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshingPromise: Promise<void> | null = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as typeof error.config & { _retry?: boolean };
    if (error.response?.status === 401 && !originalRequest?._retry && getRefreshToken()) {
      originalRequest._retry = true;
      if (!refreshingPromise) {
        refreshingPromise = axios
          .post<ApiResponse<AuthPayload>>('/api/auth/refresh', { refreshToken: getRefreshToken() })
          .then((response) => {
            setSession(response.data.data);
          })
          .catch(() => {
            clearSession();
          })
          .finally(() => {
            refreshingPromise = null;
          });
      }
      await refreshingPromise;
      const token = getToken();
      if (token) {
        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return api(originalRequest);
      }
    }
    return Promise.reject(error);
  },
);

export async function unwrap<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const response = await promise;
  return response.data.data;
}

export async function fetchCurrentUser(): Promise<CurrentUser> {
  return unwrap(api.get<ApiResponse<CurrentUser>>('/auth/me'));
}
