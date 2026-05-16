import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import type { PageResponse, PostSummary } from '../types';
import { PostCard } from '../components/PostCard';
import { usePageTitle } from '../hooks/usePageTitle';

export function HomePage() {
  usePageTitle('首页');
  const [pinnedPosts, setPinnedPosts] = useState<PostSummary[]>([]);
  const [latestPosts, setLatestPosts] = useState<PostSummary[]>([]);

  useEffect(() => {
    void Promise.all([
      unwrap(api.get('/posts/pinned')),
      unwrap<PageResponse<PostSummary>>(api.get('/posts', { params: { page: 1, pageSize: 6 } })),
    ]).then(([pinned, latest]) => {
      setPinnedPosts(pinned as PostSummary[]);
      setLatestPosts(latest.records);
    });
  }, []);

  return (
    <div className="page-stack">
      <section className="hero">
        <div className="hero-content">
          <span className="section-kicker">Spring Boot + React + Redis</span>
          <h1 className="hero-title">墨屿博客</h1>
          <p className="hero-body">
            支持角色权限、Markdown 创作、分类标签、评论审核与独立后台管理的现代博客系统。
          </p>
          <div className="actions-row">
            <Link className="accent-button" to="/posts">
              浏览文章
            </Link>
            <Link className="ghost-button" to="/editor/new">
              开始写作
            </Link>
          </div>
        </div>
        <aside className="hero-aside">
          <div className="hero-metrics">
            <div className="hero-metric">
              <strong>双端架构</strong>
              <span>公共站点 + 后台管理</span>
            </div>
            <div className="hero-metric">
              <strong>Markdown 创作</strong>
              <span>分类标签与发布流</span>
            </div>
            <div className="hero-metric">
              <strong>内容治理</strong>
              <span>评论审核与权限控制</span>
            </div>
          </div>
        </aside>
      </section>

      <section className="home-columns">
        <div className="section-block">
          <div className="section-header">
            <div className="section-heading">
              <span className="section-kicker">Latest</span>
              <h2>最新文章</h2>
            </div>
            <Link className="text-button section-action" to="/posts">
              查看全部
            </Link>
          </div>
          <div className="card-stack">
            {latestPosts.length ? latestPosts.map((post) => <PostCard key={post.id} post={post} />) : <div className="empty-state compact">暂时还没有可展示的文章。</div>}
          </div>
        </div>

        <div className="section-block">
          <div className="section-header">
            <div className="section-heading">
              <span className="section-kicker">Pinned</span>
              <h2>推荐置顶</h2>
            </div>
            <span className="section-badge">精选</span>
          </div>
          <div className="card-stack">
            {pinnedPosts.length ? pinnedPosts.map((post) => <PostCard key={post.id} post={post} />) : <div className="empty-state compact">管理员还没有设置推荐文章。</div>}
          </div>
        </div>
      </section>
    </div>
  );
}
