import { useEffect, useState } from 'react';
import { api, unwrap } from '../../lib/api';
import { usePageTitle } from '../../hooks/usePageTitle';
import type { AdminUser } from '../../types';

export function AdminUsersPage() {
  usePageTitle('用户管理');
  const [users, setUsers] = useState<AdminUser[]>([]);

  const load = async () => {
    setUsers(await unwrap<AdminUser[]>(api.get('/admin/users')));
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <section className="admin-card stack">
      <h1 className="section-title">用户管理</h1>
      <div className="table-list">
        {users.map((user) => (
          <div key={user.id} className="table-row">
            <div>
              <strong>{user.displayName}</strong>
              <div className="muted">@{user.username}</div>
            </div>
            <div className="meta-row">{user.role}</div>
            <div className="meta-row">{user.enabled ? '正常' : '已禁用'}</div>
            <button
              className="ghost-button"
              onClick={async () => {
                await api.patch(`/admin/users/${user.id}/enabled`, null, { params: { enabled: !user.enabled } });
                await load();
              }}
            >
              {user.enabled ? '禁用' : '启用'}
            </button>
          </div>
        ))}
      </div>
    </section>
  );
}
