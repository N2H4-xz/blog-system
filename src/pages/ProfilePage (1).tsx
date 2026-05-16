import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/auth';
import { usePageTitle } from '../hooks/usePageTitle';

export function ProfilePage() {
  usePageTitle('个人中心');
  const user = useAuthStore((state) => state.user);
  if (!user) {
    return null;
  }

  return (
    <section className="panel stack">
      <div>
        <h1 className="section-title">{user.displayName}</h1>
        <p className="muted">
          @{user.username} · {user.role}
        </p>
      </div>
      <div className="actions-row">
        <Link className="accent-button" to="/editor/new">
          新建文章
        </Link>
        <Link className="ghost-button" to="/me/posts">
          我的文章
        </Link>
      </div>
      {user.role === 'ADMIN' && (
        <div className="panel">
          <h3>管理员</h3>
          <p className="muted">管理文章、评论、分类标签和用户。</p>
          <Link className="ghost-button" to="/admin">
            进入后台
          </Link>
        </div>
      )}
    </section>
  );
}
