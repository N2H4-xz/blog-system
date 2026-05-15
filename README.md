# 墨屿博客 - 后端B（业务模块）

这是墨屿博客系统的后端业务模块，包含文章、评论和管理后台功能。

## 技术栈

- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- MySQL 8.4
- Redis 7.4（可选）

## 功能

- 文章管理（创建、编辑、删除、查询）
- 评论系统（评论、回复、审核）
- 管理后台（仪表盘、用户管理、文章管理、评论审核、分类标签管理）

## 本地运行

### 1. 启动基础服务

```bash
docker compose up -d
```

### 2. 启动后端

```bash
./gradlew bootRun
```

默认地址：`http://localhost:8080`

## 合并说明

本仓库需要与后端A（认证模块）合并后才是完整的后端系统。
