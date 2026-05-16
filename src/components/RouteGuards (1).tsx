import type { PropsWithChildren } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/auth';

export function AuthGuard({ children }: PropsWithChildren) {
  const user = useAuthStore((state) => state.user);
  const location = useLocation();
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }
  return children;
}

export function GuestGuard({ children }: PropsWithChildren) {
  const user = useAuthStore((state) => state.user);
  return user ? <Navigate to="/" replace /> : children;
}
