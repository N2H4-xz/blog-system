import { useEffect, useState } from 'react';
import { api, unwrap } from '../../lib/api';
import { usePageTitle } from '../../hooks/usePageTitle';
import type { Category, Tag } from '../../types';

export function AdminTaxonomyPage() {
  usePageTitle('分类标签');
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [categoryName, setCategoryName] = useState('');
  const [tagName, setTagName] = useState('');

  const load = async () => {
    const [categoryData, tagData] = await Promise.all([unwrap<Category[]>(api.get('/categories')), unwrap<Tag[]>(api.get('/tags'))]);
    setCategories(categoryData);
    setTags(tagData);
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <div className="grid-two">
      <section className="admin-card stack">
        <h1 className="section-title">分类管理</h1>
        <div className="actions-row">
          <input value={categoryName} onChange={(event) => setCategoryName(event.target.value)} placeholder="新增分类名称" />
          <button
            className="accent-button"
            onClick={async () => {
              await api.post('/admin/categories', { name: categoryName });
              setCategoryName('');
              await load();
            }}
          >
            添加
          </button>
        </div>
        {categories.map((category) => (
          <div key={category.id} className="table-row">
            <div>{category.name}</div>
            <div className="muted">{category.slug}</div>
            <button
              className="ghost-button"
              onClick={async () => {
                await api.delete(`/admin/categories/${category.id}`);
                await load();
              }}
            >
              删除
            </button>
          </div>
        ))}
      </section>
      <section className="admin-card stack">
        <h1 className="section-title">标签管理</h1>
        <div className="actions-row">
          <input value={tagName} onChange={(event) => setTagName(event.target.value)} placeholder="新增标签名称" />
          <button
            className="accent-button"
            onClick={async () => {
              await api.post('/admin/tags', { name: tagName });
              setTagName('');
              await load();
            }}
          >
            添加
          </button>
        </div>
        {tags.map((tag) => (
          <div key={tag.id} className="table-row">
            <div>{tag.name}</div>
            <div className="muted">{tag.slug}</div>
            <button
              className="ghost-button"
              onClick={async () => {
                await api.delete(`/admin/tags/${tag.id}`);
                await load();
              }}
            >
              删除
            </button>
          </div>
        ))}
      </section>
    </div>
  );
}
