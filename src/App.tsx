import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useEffect } from 'react';
import { AppLayout, AdminLayout } from './components/Layout';
import { AdminGuard, AuthGuard } from './components/RouteGuards';
import { bootstrapAuth, useAuthStore } from './store/auth';
import { PostListPage } from './pages/PostListPage';
import { PostDetailPage } from './pages/PostDetailPage';
import { MyPostsPage } from './pages/MyPostsPage';
import { EditorPage } from './pages/EditorPage';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';
import { AdminUsersPage } from './pages/admin/AdminUsersPage';
import { AdminCommentsPage } from './pages/admin/AdminCommentsPage';
import { AdminTaxonomyPage } from './pages/admin/AdminTaxonomyPage';
import { AdminPostsPage } from './pages/admin/AdminPostsPage';

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
        <Route index element={<Navigate to="/posts" replace />} />
        <Route path="/posts" element={<PostListPage />} />
        <Route path="/posts/:slug" element={<PostDetailPage />} />
        <Route
          path="/me/posts"
          element={
            <AuthGuard>
              <MyPostsPage />
            </AuthGuard>
          }
        />
        <Route
          path="/editor/new"
          element={
            <AuthGuard>
              <EditorPage />
            </AuthGuard>
          }
        />
        <Route
          path="/editor/:id"
          element={
            <AuthGuard>
              <EditorPage />
            </AuthGuard>
          }
        />
      </Route>

      <Route
        path="/admin"
        element={
          <AdminGuard>
            <AdminLayout />
          </AdminGuard>
        }
      >
        <Route index element={<AdminDashboardPage />} />
        <Route path="posts" element={<AdminPostsPage />} />
        <Route path="comments" element={<AdminCommentsPage />} />
        <Route path="users" element={<AdminUsersPage />} />
        <Route path="taxonomy" element={<AdminTaxonomyPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/posts" replace />} />
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
