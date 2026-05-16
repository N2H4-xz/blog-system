import { useCallback, useEffect, useState } from 'react';
import DOMPurify from 'dompurify';
import { Link, useParams } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import { CommentSection } from '../components/CommentSection';
import { formatDate, getErrorMessage } from '../lib/format';
import { usePageTitle } from '../hooks/usePageTitle';
import type { CommentNode, PostDetail } from '../types';

export function PostDetailPage() {
  const { slug = '' } = useParams();
  const [post, setPost] = useState<PostDetail>();
  const [comments, setComments] = useState<CommentNode[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  usePageTitle(post?.title ?? '文章详情');

  const load = useCallback(async () => {
    try {
      const detail = await unwrap<PostDetail>(api.get(`/posts/${slug}`));
      const commentList = await unwrap<CommentNode[]>(api.get(`/posts/${detail.id}/comments`));
      setPost(detail);
      setComments(commentList);
      setError('');
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, [slug]);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      void load();
    }, 0);
    return () => window.clearTimeout(timeoutId);
  }, [load]);

  if (error) {
    return <div className="empty-state">{error}</div>;
  }

  if (!post) {
    return <div className="page-loading">加载中...</div>;
  }

  return (
    <div className="post-detail">
      <header className="post-detail-header card">
        <Link className="detail-link" to="/posts">
          ← 返回列表
        </Link>
        <h1 className="post-detail-title">{post.title}</h1>
        <div className="meta-row">
          {post.pinned && <span className="pill">置顶</span>}
          <span>{post.authorName}</span>
          <span>{formatDate(post.publishedAt ?? post.createdAt)}</span>
          <span>{post.viewCount} 次阅读</span>
        </div>
        <p className="post-summary">{post.summary}</p>
        <div className="tag-row tag-list">
          {post.category && <span className="tag">分类 · {post.category.name}</span>}
          {post.tags.map((tag) => (
            <span key={tag.id} className="tag">
              #{tag.name}
            </span>
          ))}
        </div>
      </header>

      <section className="post-article-shell">
        <article className="post-content" dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(post.contentHtml) }} />
      </section>

      <CommentSection
        comments={comments}
        submitting={submitting}
        onComment={async (payload) => {
          setSubmitting(true);
          try {
            await api.post(`/posts/${post.id}/comments`, payload);
            await load();
          } finally {
            setSubmitting(false);
          }
        }}
        onReply={async (commentId, content) => {
          await api.post(`/comments/${commentId}/replies`, { content });
          await load();
        }}
        onDelete={async (commentId) => {
          await api.delete(`/comments/${commentId}`);
          await load();
        }}
      />
    </div>
  );
}
