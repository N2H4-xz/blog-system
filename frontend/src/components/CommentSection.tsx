import { useState } from 'react';
import type { CommentNode } from '../types';
import { useAuthStore } from '../store/auth';
import { formatDate } from '../lib/format';

interface CommentSectionProps {
  comments: CommentNode[];
  submitting: boolean;
  onComment: (payload: { content: string; guestName?: string; guestEmail?: string; guestWebsite?: string }) => Promise<void>;
  onReply: (commentId: number, content: string) => Promise<void>;
  onDelete: (commentId: number) => Promise<void>;
}

export function CommentSection({ comments, submitting, onComment, onReply, onDelete }: CommentSectionProps) {
  const user = useAuthStore((state) => state.user);
  const [form, setForm] = useState({ content: '', guestName: '', guestEmail: '', guestWebsite: '' });
  const [replyingId, setReplyingId] = useState<number | null>(null);
  const [replyContent, setReplyContent] = useState('');

  const handleSubmit = async () => {
    await onComment(form);
    setForm({ content: '', guestName: '', guestEmail: '', guestWebsite: '' });
  };

  const renderNode = (node: CommentNode) => (
    <article key={node.id} className="comment-card">
      <div className="comment-card-header">
        <div className="comment-card-author">
          <strong>{node.authorName}</strong>
          <div className="meta-row">
            <span>{formatDate(node.createdAt)}</span>
            {node.status === 'PENDING' && <span className="pill">待审核</span>}
          </div>
        </div>
        <div className="actions-row">
          {user && (
            <button className="text-button" onClick={() => setReplyingId(replyingId === node.id ? null : node.id)}>
              {replyingId === node.id ? '取消' : '回复'}
            </button>
          )}
          {node.canDelete && (
            <button className="text-button" onClick={() => onDelete(node.id)}>
              删除
            </button>
          )}
        </div>
      </div>

      <div className="comment-card-body">
        <p>{node.content}</p>
      </div>

      {replyingId === node.id && (
        <div className="comment-compose">
          <label className="field">
            <span className="field-label">回复内容</span>
            <textarea value={replyContent} onChange={(event) => setReplyContent(event.target.value)} placeholder="输入回复内容" />
          </label>
          <div className="actions-row">
            <button
              className="accent-button"
              onClick={async () => {
                await onReply(node.id, replyContent);
                setReplyContent('');
                setReplyingId(null);
              }}
            >
              提交回复
            </button>
          </div>
        </div>
      )}

      {node.replies.length > 0 && <div className="comment-replies">{node.replies.map(renderNode)}</div>}
    </article>
  );

  return (
    <section className="panel comment-section">
      <div className="section-header">
        <div className="section-heading">
          <span className="section-kicker">Comments</span>
          <h2>评论</h2>
          <p className="section-copy">{user ? '登录身份参与讨论。' : '访客留言将进入审核。'}</p>
        </div>
        <span className="meta-row">{comments.length} 条</span>
      </div>

      <div className="comment-compose">
        <label className="field">
          <span className="field-label">评论内容</span>
          <textarea
            value={form.content}
            onChange={(event) => setForm((current) => ({ ...current, content: event.target.value }))}
            placeholder={user ? '分享你的看法...' : '留下你的想法，访客评论将进入审核'}
          />
        </label>

        {!user && (
          <div className="comment-guest-grid">
            <label className="field">
              <span className="field-label">昵称</span>
              <input value={form.guestName} onChange={(event) => setForm((current) => ({ ...current, guestName: event.target.value }))} />
            </label>
            <label className="field">
              <span className="field-label">邮箱</span>
              <input
                value={form.guestEmail}
                onChange={(event) => setForm((current) => ({ ...current, guestEmail: event.target.value }))}
                placeholder="可选"
              />
            </label>
            <label className="field">
              <span className="field-label">站点</span>
              <input
                value={form.guestWebsite}
                onChange={(event) => setForm((current) => ({ ...current, guestWebsite: event.target.value }))}
                placeholder="https://"
              />
            </label>
          </div>
        )}

        <div className="actions-row">
          <button className="accent-button" disabled={submitting} onClick={handleSubmit}>
            {submitting ? '提交中...' : '提交评论'}
          </button>
        </div>
      </div>

      <div className="comment-thread">
        {comments.length ? comments.map(renderNode) : <div className="empty-state">还没有评论。</div>}
      </div>
    </section>
  );
}
