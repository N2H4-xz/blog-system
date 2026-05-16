import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { api } from '../lib/api';
import { clearSession, getRefreshToken, useAuthStore } from '../store/auth';

export function AppLayout() {
  const user = useAuthStore((state) => state.user);
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await api.post('/auth/logout', null, {
        headers: { 'X-Refresh-Token': getRefreshToken() ?? '' },
      });
    } finally {
      clearSession();
      navigate('/');
    }
  };

  return (
    <div className="shell">
      <header className="site-header">
        <div className="site-header-inner">
          <Link to="/" className="brand-block">
            <span className="brand">墨屿</span>
            <span className="brand-subtitle">Blog</span>
          </Link>
          <nav className="primary-nav">
            <NavLink to="/">首页</NavLink>
            {user && <NavLink to="/me/posts">我的创作</NavLink>}
            {user?.role === 'ADMIN' && <NavLink to="/admin">管理后台</NavLink>}
          </nav>
          <div className="header-actions">
            {user ? (
              <>
                <Link to="/me" className="ghost-button">
                  {user.displayName}
                </Link>
                <Link to="/editor/new" className="accent-button">
                  写文章
                </Link>
                <button className="text-button" onClick={handleLogout}>
                  退出
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="ghost-button">
                  登录
                </Link>
                <Link to="/register" className="accent-button">
                  注册
                </Link>
              </>
            )}
          </div>
        </div>
      </header>
      <main className="page-shell">
        <Outlet />
      </main>
    </div>
  );
}
