import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useEffect } from 'react';
import { AppLayout } from './components/Layout';
import { AuthGuard, GuestGuard } from './components/RouteGuards';
import { bootstrapAuth, useAuthStore } from './store/auth';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ProfilePage } from './pages/ProfilePage';

function Bootstrapper() {
  const initialized = useAuthStore((state) => state.initialized);

  useEffect(() => {
    void bootstrapAuth();
  }, []);

  if (!initialized) {
    return <div className="page-loading">正在加载博客系统...</div>;
  }

  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<HomePage />} />
        <Route
          path="/login"
          element={
            <GuestGuard>
              <LoginPage />
            </GuestGuard>
          }
        />
        <Route
          path="/register"
          element={
            <GuestGuard>
              <RegisterPage />
            </GuestGuard>
          }
        />
        <Route
          path="/me"
          element={
            <AuthGuard>
              <ProfilePage />
            </AuthGuard>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Bootstrapper />
    </BrowserRouter>
  );
}

export default App;
