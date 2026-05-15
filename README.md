# 墨屿博客 - 后端A（认证模块）

这是墨屿博客系统的后端认证模块，包含用户认证、安全配置和基础设施。

## 技术栈

- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security + JWT
- MySQL 8.4
- Redis 7.4（可选）

## 功能

- 用户注册和登录
- JWT 令牌管理（访问令牌和刷新令牌）
- 用户认证和授权
- 安全配置
- 数据实体和仓库
- Redis/内存双实现存储

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

本仓库需要与后端B（业务模块）合并后才是完整的后端系统。
