import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import { MarkdownEditor } from '../components/MarkdownEditor';
import { getErrorMessage } from '../lib/format';
import { usePageTitle } from '../hooks/usePageTitle';
import type { Category, PostDetail, Tag } from '../types';

export function EditorPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  usePageTitle(id ? '编辑文章' : '新建文章');
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [form, setForm] = useState({
    title: '',
    summary: '',
    contentMarkdown: '',
    categoryId: '',
    tagIds: [] as number[],
    status: 'DRAFT',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void Promise.all([unwrap<Category[]>(api.get('/categories')), unwrap<Tag[]>(api.get('/tags'))]).then(([categoryData, tagData]) => {
      setCategories(categoryData);
      setTags(tagData);
    });
  }, []);

  useEffect(() => {
    if (!id) {
      return;
    }
    void unwrap<PostDetail>(api.get(`/user/posts/${id}`))
      .then((post) => {
        setForm({
          title: post.title,
          summary: post.summary,
          contentMarkdown: post.contentMarkdown,
          categoryId: String(post.category?.id ?? ''),
          tagIds: post.tags.map((tag) => tag.id),
          status: post.status,
        });
      })
      .catch((err) => setError(getErrorMessage(err)));
  }, [id]);

  const handleSubmit = async (status: 'DRAFT' | 'PUBLISHED') => {
    setSubmitting(true);
    try {
      const payload = {
        ...form,
        status,
        categoryId: form.categoryId ? Number(form.categoryId) : null,
      };
      if (id) {
        await api.put(`/user/posts/${id}`, payload);
      } else {
        await api.post('/user/posts', payload);
      }
      navigate('/me/posts');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="editor-shell stack">
      <div>
        <h1 className="section-title">{id ? '编辑文章' : '新建文章'}</h1>
        <p className="muted">Markdown 编辑、分类标签、草稿与发布。</p>
      </div>
      <div className="form-grid">
        <label>
          标题
          <input value={form.title} onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))} />
        </label>
        <label>
          摘要
          <textarea value={form.summary} onChange={(event) => setForm((current) => ({ ...current, summary: event.target.value }))} placeholder="留空自动生成" />
        </label>
        <div className="grid-two">
          <label>
            分类
            <select value={form.categoryId} onChange={(event) => setForm((current) => ({ ...current, categoryId: event.target.value }))}>
              <option value="">未分类</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            标签
            <select
              multiple
              value={form.tagIds.map(String)}
              onChange={(event) =>
                setForm((current) => ({
                  ...current,
                  tagIds: Array.from(event.target.selectedOptions).map((option) => Number(option.value)),
                }))
              }
            >
              {tags.map((tag) => (
                <option key={tag.id} value={tag.id}>
                  {tag.name}
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>
      <MarkdownEditor value={form.contentMarkdown} onChange={(contentMarkdown) => setForm((current) => ({ ...current, contentMarkdown }))} />
      {error && <div className="empty-state">{error}</div>}
      <div className="actions-row">
        <button className="ghost-button" disabled={submitting} onClick={() => handleSubmit('DRAFT')}>
          保存草稿
        </button>
        <button className="accent-button" disabled={submitting} onClick={() => handleSubmit('PUBLISHED')}>
          {submitting ? '提交中...' : '发布文章'}
        </button>
      </div>
    </section>
  );
}
