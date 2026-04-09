# Knowledge AI App

基于 Java 21 + Spring Boot 3.5.13 + Spring AI 1.1.4 的单体后端骨架工程。

## 项目结构

```text
knowledge/
├── AGENTS.md
├── README.md
└── app/
    ├── pom.xml
    └── src/main/
        ├── java/knowledge/aiapp/
        └── resources/
```

## 启动命令

```bash
cd app
mvn clean package
mvn spring-boot:run
```

### 本地推荐启动（local）

```bash
cd app
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```

说明：
- `application-local.yml` 默认使用 `DB_PASSWORD=123456`。
- 本地 profile 已开启 `flyway.baseline-on-migrate=true`，避免已有表但无历史表时阻塞启动。
- 本地 profile 提供了 OpenAI Key 占位值，未显式配置时也可启动联调。

## 本地依赖说明

- Java 21
- Maven 3.9.x
- PostgreSQL 17（安装 pgvector 扩展）
- Redis 7

## PostgreSQL / Redis 启动说明

### PostgreSQL 17 + pgvector（Docker 示例）

```bash
docker run -d --name pg17-vector \
  -e POSTGRES_DB=knowledge \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  pgvector/pgvector:pg17
```

### Redis 7（Docker 示例）

```bash
docker run -d --name redis7 -p 6379:6379 redis:7
```

## Swagger 地址

启动后访问：

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## 环境变量说明

### 通用

- `SPRING_PROFILES_ACTIVE`：运行环境，默认 `dev`

### 数据库

- `DB_HOST`（默认 `localhost`）
- `DB_PORT`（默认 `5432`）
- `DB_NAME`（默认 `knowledge`）
- `DB_USERNAME`（默认 `postgres`）
- `DB_PASSWORD`（默认 `postgres`）
- `DB_URL`（生产环境使用）

### Redis

- `REDIS_HOST`（默认 `localhost`）
- `REDIS_PORT`（默认 `6379`）
- `REDIS_PASSWORD`（默认空）
- `REDIS_DB`（默认 `0`）

### Spring AI

- `OPENAI_API_KEY`
- `OPENAI_CHAT_MODEL`（dev 默认 `gpt-4o-mini`）
- `OPENAI_EMBEDDING_MODEL`（dev 默认 `text-embedding-3-small`）
- `OPENAI_TEMPERATURE`（默认 `0.2`）

### 本地文件存储

- `APP_STORAGE_LOCAL_BASE_PATH`（dev 默认 `./data/storage`）

## 说明

- 所有接口统一返回 `knowledge.aiapp.common.result.Result<T>`。
- Flyway 脚本位于 `app/src/main/resources/db/migration/`。
- Prompt 模板位于 `app/src/main/resources/prompts/`。
