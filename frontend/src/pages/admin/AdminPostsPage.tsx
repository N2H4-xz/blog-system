import { useCallback, useEffect, useState } from 'react';
import { api, unwrap } from '../../lib/api';
import { Pagination } from '../../components/Pagination';
import { usePageTitle } from '../../hooks/usePageTitle';
import type { PageResponse, PostSummary } from '../../types';

export function AdminPostsPage() {
  usePageTitle('文章管理');
  const [page, setPage] = useState(1);
  const [data, setData] = useState<PageResponse<PostSummary>>();

  const load = useCallback(async (currentPage: number) => {
    setData(await unwrap<PageResponse<PostSummary>>(api.get('/admin/posts', { params: { page: currentPage, pageSize: 10 } })));
  }, []);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      void load(page);
    }, 0);
    return () => window.clearTimeout(timeoutId);
  }, [load, page]);

  return (
    <section className="admin-card stack">
      <h1 className="section-title">文章管理</h1>
      <div className="table-list">
        {data?.records.map((post) => (
          <div key={post.id} className="table-row">
            <div>
              <strong>{post.title}</strong>
              <div className="muted">{post.authorName}</div>
            </div>
            <div className="meta-row">{post.status === 'PUBLISHED' ? '已发布' : '草稿'}</div>
            <div className="meta-row">{post.pinned ? '已置顶' : '—'}</div>
            <div className="actions-row">
              <button
                className="ghost-button"
                onClick={async () => {
                  await api.patch(`/admin/posts/${post.id}/pin`, null, { params: { pinned: !post.pinned } });
                  await load(page);
                }}
              >
                {post.pinned ? '取消置顶' : '置顶'}
              </button>
              <button
                className="text-button"
                onClick={async () => {
                  await api.delete(`/admin/posts/${post.id}`);
                  await load(page);
                }}
              >
                删除
              </button>
            </div>
          </div>
        ))}
      </div>
      <Pagination page={page} totalPages={data?.totalPages ?? 1} onChange={setPage} />
    </section>
  );
}
