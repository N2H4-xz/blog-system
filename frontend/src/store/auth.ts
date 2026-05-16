import { create } from 'zustand';
import axios from 'axios';
import type { ApiResponse, AuthPayload, CurrentUser } from '../types';

const ACCESS_TOKEN_KEY = 'blog_access_token';
const REFRESH_TOKEN_KEY = 'blog_refresh_token';
const USER_KEY = 'blog_user';

interface AuthState {
  initialized: boolean;
  user: CurrentUser | null;
  setInitialized: (initialized: boolean) => void;
  setUser: (user: CurrentUser | null) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  initialized: false,
  user: getStoredUser(),
  setInitialized: (initialized) => set({ initialized }),
  setUser: (user) => set({ user }),
}));

export function getToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

function getStoredUser(): CurrentUser | null {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as CurrentUser;
  } catch {
    localStorage.removeItem(USER_KEY);
    return null;
  }
}

export function setSession(payload: AuthPayload) {
  localStorage.setItem(ACCESS_TOKEN_KEY, payload.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, payload.refreshToken);
  localStorage.setItem(USER_KEY, JSON.stringify(payload.user));
  useAuthStore.getState().setUser(payload.user);
}

export function clearSession() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  useAuthStore.getState().setUser(null);
}

export async function bootstrapAuth() {
  const refreshToken = getRefreshToken();
  if (!getToken() && !refreshToken) {
    useAuthStore.getState().setInitialized(true);
    return;
  }
  try {
    const response = await axios.get<{ data: CurrentUser }>('/api/auth/me', {
      headers: getToken() ? { Authorization: `Bearer ${getToken()}` } : undefined,
    });
    useAuthStore.getState().setUser(response.data.data);
  } catch {
    if (refreshToken) {
      try {
        const response = await axios.post<ApiResponse<AuthPayload>>('/api/auth/refresh', { refreshToken });
        setSession(response.data.data);
      } catch {
        clearSession();
      }
    } else {
      clearSession();
    }
  } finally {
    useAuthStore.getState().setInitialized(true);
  }
}
