 # 墨屿博客系统

一个前后端分离的博客系统，后端使用 `Spring Boot + MySQL + Redis`，前端使用 `React + TypeScript + Vite`。

## 已实现能力

- 用户注册、登录、当前用户获取、退出登录、JWT 刷新
- 普通用户 / 管理员双角色权限
- Markdown 博客创作、草稿 / 发布状态、自动摘要
- 分类、标签、关键词组合搜索，分页浏览
- 文章详情、置顶推荐、阅读量统计
- 我的文章管理：编辑、删除、状态切换
- 访客昵称留言、登录用户回复、评论审核与删除
- 管理后台：仪表盘、文章管理、评论审核、用户管理、分类标签管理

## 目录结构

- [backend](D:\project\博客\backend)
- [frontend](D:\project\博客\frontend)

## 本地运行

### 1. 启动基础服务

先在项目根目录执行：

```powershell
docker compose up -d
```

### 2. 启动后端

```powershell
cd backend
.\gradlew.bat bootRun
```

默认后端地址：`http://localhost:8080`

### 3. 启动前端

```powershell
cd frontend
npm install
npm run dev
```

默认前端地址：`http://localhost:5173`

## 初始化管理员

默认不会自动创建管理员账号。需要初始化管理员时，启动后端前设置：

```powershell
$env:APP_BOOTSTRAP_ENABLED="true"
$env:BOOTSTRAP_ADMIN_USERNAME="admin"
$env:BOOTSTRAP_ADMIN_PASSWORD="<your-password>"
.\gradlew.bat bootRun
```

`APP_BOOTSTRAP_ENABLED=true` 时必须显式提供管理员密码。

## 说明

- 前端已配置 Vite 代理，开发环境下 `/api` 会转发到后端。
- 如果本地暂时没有 Redis，可设置环境变量 `APP_REDIS_ENABLED=false` 退回内存实现。
- 默认数据库连接可通过 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 覆盖。
