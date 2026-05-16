import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import { Pagination } from '../components/Pagination';
import { PostCard } from '../components/PostCard';
import type { Category, PageResponse, PostSummary, Tag } from '../types';
import { usePageTitle } from '../hooks/usePageTitle';

export function PostListPage() {
  usePageTitle('文章列表');
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [pageData, setPageData] = useState<PageResponse<PostSummary>>();
  const [filters, setFilters] = useState({
    keyword: searchParams.get('keyword') ?? '',
    categoryId: searchParams.get('categoryId') ?? '',
    tagId: searchParams.get('tagId') ?? '',
  });
  const page = Number(searchParams.get('page') ?? '1');

  useEffect(() => {
    void Promise.all([unwrap<Category[]>(api.get('/categories')), unwrap<Tag[]>(api.get('/tags'))]).then(([categoryData, tagData]) => {
      setCategories(categoryData);
      setTags(tagData);
    });
  }, []);

  useEffect(() => {
    setFilters({
      keyword: searchParams.get('keyword') ?? '',
      categoryId: searchParams.get('categoryId') ?? '',
      tagId: searchParams.get('tagId') ?? '',
    });
  }, [searchParams]);

  useEffect(() => {
    void unwrap<PageResponse<PostSummary>>(
      api.get('/posts', {
        params: {
          page,
          pageSize: 8,
          keyword: searchParams.get('keyword') ?? undefined,
          categoryId: searchParams.get('categoryId') ?? undefined,
          tagId: searchParams.get('tagId') ?? undefined,
        },
      }),
    ).then(setPageData);
  }, [page, searchParams]);

  const activeLabel = useMemo(() => {
    if (filters.categoryId) {
      const category = categories.find((item) => String(item.id) === filters.categoryId);
      return category ? `分类 · ${category.name}` : '文章筛选';
    }
    if (filters.tagId) {
      const tag = tags.find((item) => String(item.id) === filters.tagId);
      return tag ? `标签 · ${tag.name}` : '文章筛选';
    }
    if (filters.keyword) {
      return `搜索 · ${filters.keyword}`;
    }
    return '全部文章';
  }, [categories, filters.categoryId, filters.keyword, filters.tagId, tags]);

  const hasActiveFilters = Boolean(filters.keyword || filters.categoryId || filters.tagId);
  const selectedCategory = categories.find((item) => String(item.id) === filters.categoryId);
  const selectedTag = tags.find((item) => String(item.id) === filters.tagId);

  const applyFilters = (nextPage = 1) => {
    const params = new URLSearchParams();
    if (filters.keyword) params.set('keyword', filters.keyword);
    if (filters.categoryId) params.set('categoryId', filters.categoryId);
    if (filters.tagId) params.set('tagId', filters.tagId);
    params.set('page', String(nextPage));
    navigate(`/posts?${params.toString()}`);
  };

  const resetFilters = () => {
    setFilters({ keyword: '', categoryId: '', tagId: '' });
    navigate('/posts?page=1');
  };

  return (
    <div className="page-stack">
      <section className="filters-panel">
        <div className="section-header">
          <div className="section-heading">
            <h1 className="section-title">{activeLabel}</h1>
            {hasActiveFilters && (
              <div className="filter-summary">
                {filters.keyword && <span className="pill">关键词 · {filters.keyword}</span>}
                {selectedCategory && <span className="pill">分类 · {selectedCategory.name}</span>}
                {selectedTag && <span className="pill">标签 · {selectedTag.name}</span>}
                <button className="text-button" onClick={resetFilters}>
                  清空筛选
                </button>
              </div>
            )}
          </div>
          <span className="meta-row">{pageData?.total ?? 0} 篇</span>
        </div>

        <div className="filters-grid">
          <label className="field">
            <span className="field-label">关键词</span>
            <input
              value={filters.keyword}
              onChange={(event) => setFilters((current) => ({ ...current, keyword: event.target.value }))}
              placeholder="标题或摘要"
            />
          </label>

          <label className="field">
            <span className="field-label">分类</span>
            <span className="select-shell">
              <select
                className="select-input"
                value={filters.categoryId}
                onChange={(event) => setFilters((current) => ({ ...current, categoryId: event.target.value }))}
              >
                <option value="">全部分类</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
              <span className="select-chevron" aria-hidden="true" />
            </span>
          </label>

          <label className="field">
            <span className="field-label">标签</span>
            <span className="select-shell">
              <select className="select-input" value={filters.tagId} onChange={(event) => setFilters((current) => ({ ...current, tagId: event.target.value }))}>
                <option value="">全部标签</option>
                {tags.map((tag) => (
                  <option key={tag.id} value={tag.id}>
                    {tag.name}
                  </option>
                ))}
              </select>
              <span className="select-chevron" aria-hidden="true" />
            </span>
          </label>
        </div>

        <div className="actions-row">
          <button className="accent-button" onClick={() => applyFilters(1)}>
            检索
          </button>
          <button className="text-button" onClick={resetFilters}>
            清空
          </button>
        </div>
      </section>

      <section className="list-grid">
        {pageData?.records.length ? pageData.records.map((post) => <PostCard key={post.id} post={post} />) : <div className="empty-state">没有匹配到文章，试试放宽筛选条件。</div>}
      </section>

      <Pagination page={page} totalPages={pageData?.totalPages ?? 1} onChange={applyFilters} />
    </div>
  );
}
