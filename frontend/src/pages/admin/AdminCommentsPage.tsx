import { useCallback, useEffect, useState } from 'react';
import { api, unwrap } from '../../lib/api';
import { formatDate } from '../../lib/format';
import { usePageTitle } from '../../hooks/usePageTitle';
import type { AdminComment } from '../../types';

export function AdminCommentsPage() {
  usePageTitle('评论审核');
  const [comments, setComments] = useState<AdminComment[]>([]);

  const load = useCallback(async () => {
    setComments(await unwrap<AdminComment[]>(api.get('/admin/comments')));
  }, []);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      void load();
    }, 0);
    return () => window.clearTimeout(timeoutId);
  }, [load]);

  return (
    <section className="admin-card stack">
      <h1 className="section-title">评论审核</h1>
      <div className="table-list">
        {comments.map((comment) => (
          <div key={comment.id} className="table-row">
            <div>
              <strong>{comment.authorName}</strong>
              <div className="muted">{comment.postTitle}</div>
            </div>
            <div className="meta-row">{comment.guest ? '访客' : '用户'}</div>
            <div className="meta-row">{comment.status === 'PENDING' ? '待审核' : '已通过'}</div>
            <div className="actions-row">
              {comment.status === 'PENDING' && (
                <button
                  className="accent-button"
                  onClick={async () => {
                    await api.patch(`/admin/comments/${comment.id}/approve`);
                    await load();
                  }}
                >
                  通过
                </button>
              )}
              <button
                className="ghost-button"
                onClick={async () => {
                  await api.delete(`/admin/comments/${comment.id}`);
                  await load();
                }}
              >
                删除
              </button>
            </div>
            <div className="meta-row">{formatDate(comment.createdAt)}</div>
            <div className="muted">{comment.content}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
