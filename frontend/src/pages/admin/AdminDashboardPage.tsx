import { useEffect, useState } from 'react';
import { api, unwrap } from '../../lib/api';
import { usePageTitle } from '../../hooks/usePageTitle';
import type { DashboardStats } from '../../types';

export function AdminDashboardPage() {
  usePageTitle('仪表盘');
  const [stats, setStats] = useState<DashboardStats>();

  useEffect(() => {
    void unwrap<DashboardStats>(api.get('/admin/dashboard')).then(setStats);
  }, []);

  return (
    <div className="stack">
      <section className="admin-card">
        <h1 className="section-title">仪表盘</h1>
        <p className="muted">博客系统运营数据概览。</p>
      </section>
      <section className="stat-grid">
        <div className="stat-card">
          用户总数
          <strong>{stats?.users ?? 0}</strong>
        </div>
        <div className="stat-card">
          文章总数
          <strong>{stats?.posts ?? 0}</strong>
        </div>
        <div className="stat-card">
          已发布
          <strong>{stats?.publishedPosts ?? 0}</strong>
        </div>
        <div className="stat-card">
          待审评论
          <strong>{stats?.pendingComments ?? 0}</strong>
        </div>
      </section>
    </div>
  );
}
