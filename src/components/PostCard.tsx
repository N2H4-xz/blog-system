import { Link } from 'react-router-dom';
import type { PostSummary } from '../types';
import { formatDate } from '../lib/format';

export function PostCard({ post, manageMode = false }: { post: PostSummary; manageMode?: boolean }) {
  return (
    <article className="post-card card">
      <div className="post-card-top">
        <div className="meta-row">
          {post.pinned && <span className="pill">置顶</span>}
          <span>{post.authorName}</span>
          <span>{formatDate(post.publishedAt ?? post.createdAt)}</span>
          <span>{post.viewCount} 次阅读</span>
          {manageMode && <span className="pill">{post.status === 'PUBLISHED' ? '已发布' : '草稿'}</span>}
        </div>
        <h3 className="post-card-title">
          <Link to={`/posts/${post.slug}`}>{post.title}</Link>
        </h3>
      </div>

      <p className="post-card-summary">{post.summary}</p>

      <div className="post-card-footer">
        <div className="tag-row tag-list">
          {post.category && <span className="tag">分类 · {post.category.name}</span>}
          {post.tags.map((tag) => (
            <span key={tag.id} className="tag">
              #{tag.name}
            </span>
          ))}
        </div>
        <div className="actions-row post-card-actions">
          <Link className="ghost-button" to={`/posts/${post.slug}`}>
            阅读全文
          </Link>
          {manageMode && (
            <Link className="text-button" to={`/editor/${post.id}`}>
              编辑
            </Link>
          )}
        </div>
      </div>
    </article>
  );
}
