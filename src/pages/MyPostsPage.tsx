import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import { Pagination } from '../components/Pagination';
import { PostCard } from '../components/PostCard';
import { getErrorMessage } from '../lib/format';
import { usePageTitle } from '../hooks/usePageTitle';
import type { PageResponse, PostSummary } from '../types';

export function MyPostsPage() {
  usePageTitle('我的文章');
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const [pageData, setPageData] = useState<PageResponse<PostSummary>>();
  const [error, setError] = useState('');

  const load = async (currentPage = page) => {
    try {
      const data = await unwrap<PageResponse<PostSummary>>(api.get('/user/posts', { params: { page: currentPage, pageSize: 8 } }));
      setPageData(data);
      setError('');
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  useEffect(() => {
    void load(page);
  }, [page]);

  return (
    <div className="stack">
      <section className="panel">
        <div className="actions-row" style={{ justifyContent: 'space-between' }}>
          <div>
            <h1 className="section-title">我的文章</h1>
            <p className="muted">管理草稿和已发布文章。</p>
          </div>
          <button className="accent-button" onClick={() => navigate('/editor/new')}>
            新建文章
          </button>
        </div>
      </section>
      {error && <div className="empty-state">{error}</div>}
      <section className="list-grid">
        {pageData?.records.length ? pageData.records.map((post) => <PostCard key={post.id} post={post} manageMode />) : <div className="empty-state">还没有文章。</div>}
      </section>
      <Pagination page={page} totalPages={pageData?.totalPages ?? 1} onChange={setPage} />
    </div>
  );
}
