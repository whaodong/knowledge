# Knowledge Frontend

基于 React + TypeScript + Vite 的前端工程，已实现 Cookie/Session 登录闭环（无 JWT、无本地 Token 存储）。

## 环境要求

- Node.js >= 20
- 建议 npm >= 10（也可使用 pnpm）

## 安装与启动

```bash
npm install
npm run dev
```

默认开发地址：`http://localhost:5173`

## 构建

```bash
npm run build
```

## 目录说明（核心）

- `src/api`：统一请求层与鉴权接口封装
- `src/stores/auth-store.ts`：登录态管理（用户、初始化状态、登录中状态）
- `src/components/auth`：`AuthInitializer` / `ProtectedRoute` / `PublicOnlyRoute`
- `src/pages/LoginPage.tsx`：登录页
- `src/layouts/AppShellLayout.tsx`：业务页布局（含用户信息与登出按钮）

## Session 联调配置

可选环境变量（前端根目录 `.env`）：

```bash
VITE_API_BASE_URL=http://localhost:8080
```

如果不配置 `VITE_API_BASE_URL`，默认走同域请求。

请求层默认开启 `credentials: 'include'`，用于携带 Cookie。

后端需确保：

1. 开启跨域凭证支持（`Access-Control-Allow-Credentials: true`）
2. `Access-Control-Allow-Origin` 不能为 `*`，必须是具体前端域名
3. Session Cookie 策略与域名策略满足本地联调

## 已实现的登录联调流程

1. 应用启动自动请求 `GET /api/auth/me` 初始化登录态
2. 未登录访问受保护路由自动跳转 `/login`
3. 登录页提交 `POST /api/auth/login`，成功后回跳目标路由或 `/knowledge`
4. 点击退出登录调用 `POST /api/auth/logout`，清空登录态并跳转 `/login`
5. 全局请求层对 `401` 做统一未授权处理

## 路由鉴权范围

受保护：

- `/knowledge`
- `/chat`
- `/chat/:sessionId`
- `/settings`

匿名可访问：

- `/login`（已登录用户访问会自动跳转业务首页）
