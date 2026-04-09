# BACKEND_AGENTS.md

## 目标

基于以下固定目录结构，生成一个可编译、可启动、可扩展的后端工程骨架。

技术栈固定为：

- Java 21
- Maven 3.9.x
- Spring Boot 3.5.13
- Spring AI 1.1.4
- SpringDoc OpenAPI 2.8.16
- PostgreSQL 17 + pgvector
- Redis 7

---

## 固定目录结构

项目根目录固定为：

```text
knowledge/
├── app/
│   ├── src/main/java/knowledge/aiapp/
│   │   ├── AiAppApplication.java
│   │   ├── common/
│   │   ├── infrastructure/
│   │   └── modules/
│   │       ├── auth/
│   │       ├── chat/
│   │       ├── document/
│   │       ├── knowledgebase/
│   │       ├── user/
│   │       └── system/
│   └── src/main/resources/
│       ├── application.yml
│       ├── db/migration/
│       └── prompts/
```

---

## 生成要求

### 1. 基础工程要求

生成标准 Maven Spring Boot 项目，保证以下要求：

- 项目可直接编译启动
- 不能生成伪代码
- 类、接口、配置文件必须完整
- 所有目录必须真实创建
- 所有基础类必须具备最小可运行实现
- 默认使用 `knowledge.aiapp` 作为根包名

---

## 2. 统一接口返回格式

所有后端接口必须统一返回以下 JSON 结构：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-04-07T17:00:00"
}
```

字段定义如下：

- `code`：业务状态码，成功固定为 `200`
- `message`：响应消息，例如“操作成功”“请求参数错误”“未授权”
- `data`：业务数据，可以是对象、数组、分页对象或 `null`
- `timestamp`：服务端生成的响应时间，格式固定为 `yyyy-MM-dd'T'HH:mm:ss`

统一约束如下：

1. 所有 Controller 接口统一返回 `knowledge.aiapp.common.result.Result<T>`
2. 禁止直接返回裸对象、字符串、Map、List、ResponseEntity 作为默认返回风格
3. 成功返回统一使用：
   - `Result.success()`
   - `Result.success(data)`
   - `Result.success(message, data)`
4. 失败返回统一使用：
   - `Result.failure(code, message)`
5. 全局异常处理器必须返回统一结构
6. `timestamp` 必须由服务端自动生成，不允许前端传入

### Result 类要求

在 `knowledge.aiapp.common.result` 下生成通用返回体 `Result<T>`，结构必须包含：

- `Integer code`
- `String message`
- `T data`
- `LocalDateTime timestamp`

并提供以下静态方法：

- `success()`
- `success(T data)`
- `success(String message, T data)`
- `failure(Integer code, String message)`

### 返回码规范

建议至少内置以下返回码：

- `200`：操作成功
- `400`：请求参数错误
- `401`：未授权
- `403`：禁止访问
- `404`：资源不存在
- `500`：系统内部错误

要求在 `knowledge.aiapp.common.enums` 下生成返回码枚举类。

---

## 3. common 模块规范

在 `knowledge.aiapp.common` 下生成以下子包：

```text
common/
├── advice/
├── config/
├── constant/
├── enums/
├── exception/
├── result/
└── utils/
```

至少生成以下内容：

- 全局异常处理类
- 自定义业务异常类
- 通用返回体 `Result<T>`
- 返回码枚举
- Swagger/OpenAPI 基础配置
- Jackson / 时间格式化基础配置
- 公共常量类
- 常用工具类占位

### 全局异常处理要求

全局异常处理必须统一返回 `Result<T>` 结构，例如：

#### 系统异常
```json
{
  "code": 500,
  "message": "系统内部错误",
  "data": null,
  "timestamp": "2024-04-07T17:00:00"
}
```

#### 参数校验异常
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": "2024-04-07T17:00:00"
}
```

#### 未授权异常
```json
{
  "code": 401,
  "message": "未授权",
  "data": null,
  "timestamp": "2024-04-07T17:00:00"
}
```

---

## 4. infrastructure 模块规范

在 `knowledge.aiapp.infrastructure` 下生成以下子包：

```text
infrastructure/
├── ai/
├── file/
├── persistence/
├── redis/
├── security/
├── storage/
└── vector/
```

各包职责如下：

- `ai/`：Spring AI、ChatClient、EmbeddingModel、PromptTemplate、模型相关配置
- `file/`：文件解析、文件类型识别、分片预处理
- `persistence/`：JPA 基础配置、审计字段、基类实体
- `redis/`：RedisTemplate、缓存配置、Stream 基础支持
- `security/`：Spring Security 基础配置、认证拦截、密码编码器
- `storage/`：对象存储抽象，先提供本地存储实现
- `vector/`：pgvector / VectorStore 基础配置

要求：

- 每个子包至少生成一个基础配置类或服务类
- 不允许只留空目录
- 对外部系统的适配统一放在 `infrastructure`
- 不要在 `infrastructure` 中放业务规则

---

## 5. modules 模块规范

在 `knowledge.aiapp.modules` 下生成以下业务模块：

```text
modules/
├── auth/
├── chat/
├── document/
├── knowledgebase/
├── user/
└── system/
```

其中以下模块必须按统一结构生成：

- `auth`
- `chat`
- `document`
- `knowledgebase`

统一目录模板如下：

```text
模块名/
├── controller/
├── service/
├── dto/
├── domain/
├── repository/
└── converter/
```

目录职责如下：

- `controller/`：REST 接口层
- `service/`：业务服务层
- `dto/`：请求对象与响应对象
- `domain/`：实体、领域对象
- `repository/`：仓储接口
- `converter/`：对象转换器

要求：

- 每个模块至少生成一个 Controller
- 每个模块至少生成一个 Service 接口与实现
- 每个模块至少生成一个 DTO 请求类和响应类
- 每个模块至少生成一个基础实体类
- 每个模块至少生成一个 Repository 接口
- Controller 返回值统一使用 `Result<T>`

---

## 6. resources 目录规范

在 `src/main/resources/` 下生成：

```text
resources/
├── application.yml
├── application-dev.yml
├── application-prod.yml
├── db/migration/
└── prompts/
```

### 配置文件要求

#### application.yml
放公共配置，例如：

- `spring.application.name`
- `jackson`
- `servlet.multipart`
- `springdoc`
- `logging`
- `management.endpoints`
- `spring.profiles.active`

#### application-dev.yml
放开发环境配置，例如：

- PostgreSQL 连接
- Redis 连接
- JPA 配置
- Spring AI 模型配置占位
- 文件存储本地路径

#### application-prod.yml
放生产环境配置占位，字段完整，但敏感值使用占位符。

---

## 7. Flyway 规范

在 `db/migration/` 下至少生成以下脚本占位：

```text
db/migration/
├── V1__init_schema.sql
├── V2__create_user_table.sql
├── V3__create_chat_table.sql
└── V4__create_knowledgebase_table.sql
```

要求：

- SQL 语法面向 PostgreSQL 17
- 表结构具备基础字段
- 预留 `created_at`、`updated_at`
- 知识库相关表预留向量字段扩展能力
- 不要求一开始写完整业务字段，但必须具备后续扩展空间

---

## 8. prompts 目录规范

在 `prompts/` 下生成以下子目录：

```text
prompts/
├── chat/
├── extract/
├── knowledgebase/
└── system/
```

并在每个目录下至少生成一个 `.st` 或 `.txt` 提示词模板占位文件，例如：

- `prompts/chat/rag-answer.st`
- `prompts/chat/session-title.st`
- `prompts/extract/document-metadata.st`
- `prompts/knowledgebase/chunk-summary.st`

要求：

- 提示词文件必须真实存在
- 文件内容使用可维护的模板文本
- 不要生成空文件

---

## 9. API 设计要求

请至少生成以下接口骨架：

### auth
- `POST /api/auth/login`

### document
- `POST /api/documents/upload`

### knowledgebase
- `POST /api/knowledgebases/reindex/{fileId}`

### chat
- `POST /api/chat/sessions`
- `GET /api/chat/sessions/{sessionId}/messages`
- `POST /api/chat/sessions/{sessionId}/messages`

### system
- `GET /api/system/health`

统一要求：

- 使用 RESTful 风格
- 添加 OpenAPI 注解
- 返回统一使用 `Result<T>`
- Controller 不直接写复杂业务逻辑

---

## 10. Controller 返回风格约束

所有 Controller 代码必须遵循以下风格：

### 正确示例
```java
@GetMapping("/{id}")
public Result<UserResponse> getUser(@PathVariable Long id) {
    UserResponse response = userService.getById(id);
    return Result.success(response);
}
```

### 禁止示例
```java
@GetMapping("/{id}")
public UserResponse getUser(@PathVariable Long id) {
    return userService.getById(id);
}
```

```java
@GetMapping("/{id}")
public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getById(id));
}
```

```java
@GetMapping("/{id}")
public Map<String, Object> getUser(@PathVariable Long id) {
    return Map.of("code", 200, "message", "操作成功");
}
```

---

## 11. 依赖要求

请在 `pom.xml` 中至少包含以下依赖：

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-security`
- `spring-boot-starter-actuator`
- `org.postgresql:postgresql`
- `org.flywaydb:flyway-core`
- `org.springdoc:springdoc-openapi-starter-webmvc-ui`
- Spring AI OpenAI Starter
- Spring AI pgvector Starter
- Lombok
- Spring Boot Test
- Testcontainers（PostgreSQL / Redis 可选）

要求：

- 版本尽量由 Spring Boot BOM 管理
- Spring AI 与 Spring Boot 版本必须兼容
- 保证 `mvn clean package` 可以通过

---

## 12. 代码风格要求

- 使用清晰的分层架构
- 禁止把所有逻辑堆到 Controller
- 禁止生成明显无法编译的占位代码
- DTO、Entity、Repository、Service 分离
- 命名统一、语义明确
- 优先生成可运行的最小实现，而不是复杂设计
- 所有注释使用中文
- 所有类和接口有明确职责
- 所有后端接口必须统一返回 `knowledge.aiapp.common.result.Result<T>`

---

## 13. 第一阶段交付物

第一轮生成时，请输出并创建：

1. 完整目录结构
2. `pom.xml`
3. `AiAppApplication.java`
4. `application.yml`
5. `application-dev.yml`
6. `application-prod.yml`
7. `common` 下基础类
8. `infrastructure` 下基础配置类
9. `modules/auth`、`chat`、`document`、`knowledgebase`、`system` 的基础代码
10. `db/migration` 初始化脚本
11. `prompts` 目录和示例模板
12. `README`，包含：
   - 启动命令
   - 本地依赖说明
   - PostgreSQL / Redis 启动说明
   - Swagger 地址
   - 环境变量说明

---

## 14. 输出顺序要求

请按以下顺序执行生成：

1. 先创建目录结构
2. 再生成 `pom.xml`
3. 再生成配置文件
4. 再生成 `common`
5. 再生成 `infrastructure`
6. 再生成 `modules`
7. 再生成 Flyway 脚本
8. 再生成 prompts 模板
9. 最后生成 `README`

---

## 15. 特别约束

- 不要擅自更改项目根目录名 `knowledge`
- 不要修改根包名 `knowledge.aiapp`
- 不要删除任何已约定模块
- 不要省略 `application-dev.yml` 与 `application-prod.yml`
- 不要生成无法编译的伪实现
- 不要引入未说明的大型额外框架
- 不要过早设计复杂微服务结构
- 当前目标是生成一个稳定、清晰、可启动的单体后端骨架工程
- 登录与会话改造约束：涉及 Spring Security + Spring Session + Redis 的登录改造时，必须同时验证 Flyway 历史表兼容性（旧字段非空约束如 password_hash/status）与 Session 序列化可读性（SecurityContext/Principal 可被 Redis 正反序列化）；
  完成后必须执行 /api/auth/me -> /api/auth/login -> /api/auth/me -> /api/auth/logout -> /api/auth/me 全链路实测，且所有响应统一为 Result<T>。

---

## 16. 后端编码规范要求（阿里巴巴开发规范）

本项目后端编码规范统一使用《阿里巴巴 Java 开发手册》作为默认标准。

执行原则：

1. 所有 Java 代码默认遵循阿里巴巴开发规范。
2. 若本文件中的项目约束与通用规范冲突，优先遵循本项目 `AGENTS.md`。
3. 所有新增代码、重构代码、修复代码都必须按阿里规范编写。
4. 生成代码时，优先选择“清晰、稳定、可维护”的实现，不为了炫技牺牲可读性。
5. 禁止生成明显违背阿里规范的代码风格。

### 16.1 命名规范

1. 包名全部小写，使用有语义的英文单词，禁止使用拼音、无意义缩写。
2. 类名使用 UpperCamelCase。
3. 方法名、变量名、参数名使用 lowerCamelCase。
4. 常量名使用全部大写，单词间使用下划线分隔。
5. 抽象类命名优先使用 `Abstract` 或 `Base` 前缀。
6. 接口实现类命名应语义明确，避免简单使用 `Impl`，但在 Service 层允许使用 `XXXServiceImpl`。
7. DTO、VO、Query、Command、Response、Entity、Repository、Converter 等后缀必须明确，不允许混用。

### 16.2 Controller 规范

1. Controller 仅负责参数接收、参数校验、结果返回。
2. Controller 中禁止写复杂业务逻辑。
3. Controller 所有接口统一返回 `knowledge.aiapp.common.result.Result<T>`。
4. Controller 方法必须添加清晰的接口语义命名和 OpenAPI 注解。
5. 禁止在 Controller 中直接操作 Repository。
6. 禁止在 Controller 中直接拼接复杂 SQL、Prompt 或文件处理逻辑。

### 16.3 Service 规范

1. Service 层负责业务编排和核心业务逻辑。
2. Service 接口与实现分离。
3. 事务边界优先放在 Service 层。
4. 单个方法职责要单一，避免超长方法。
5. 对外暴露的 Service 方法命名必须体现业务意图，不允许使用 `doSomething`、`handle`、`processData` 等模糊命名。

### 16.4 DTO / Entity / VO 规范

1. DTO、Entity、Response 必须分离，禁止混用数据库实体作为接口出参。
2. 请求对象与响应对象分开定义。
3. Entity 类字段必须与数据库语义一致，不允许塞入前端展示字段。
4. Response 对象应只暴露前端需要的字段。
5. 对象转换统一放在 `converter` 包中，不要在 Controller 中手工散落转换逻辑。

### 16.5 异常规范

1. 业务异常必须使用项目统一的自定义异常类型。
2. 禁止直接抛出裸 `RuntimeException` 作为业务表达。
3. 所有异常必须通过全局异常处理器统一转换为 `Result<T>`。
4. 异常信息应明确、可定位，避免只有“失败”“错误”这类模糊描述。
5. 对用户可见的异常信息应友好，对日志中的异常信息应完整。

### 16.6 日志规范

1. 必须使用统一日志框架，禁止使用 `System.out.println`。
2. 日志内容必须包含关键业务上下文，但不得泄露密码、令牌、密钥、身份证号、完整手机号等敏感信息。
3. 日志占位符统一使用 `{}`，禁止字符串拼接日志。
4. `error` 日志必须带异常堆栈。
5. 高频路径避免打印无意义 info 日志。
6. 对 AI 请求、向量检索、文件上传等关键链路应保留必要日志，但要避免输出完整敏感 Prompt 和原始密钥。

### 16.7 数据库规范

1. 表名、字段名使用小写下划线风格。
2. 所有表必须具备基础审计字段，至少包括：
   - `created_at`
   - `updated_at`
3. 主键命名保持统一，例如 `id`。
4. 状态字段、删除标记字段、类型字段必须语义明确。
5. 禁止在代码中散落硬编码 SQL 字段名和魔法值。
6. Flyway 脚本必须可重复审阅，命名规范清晰。
7. PostgreSQL / pgvector 相关表设计应为后续扩展预留空间，不要在第一版过度设计。

### 16.8 集合、对象、判空规范

1. 任何可能为空的返回值都要明确处理。
2. 能返回空集合时，不返回 `null` 集合。
3. 拆箱、类型转换、字符串转数值时必须考虑空值和异常场景。
4. `equals` 比较优先使用常量或确定不为空的对象调用。
5. 禁止过度链式调用导致空指针风险不可控。

### 16.9 并发与缓存规范

1. Redis key 必须统一命名规范，例如：`业务:模块:标识`。
2. 缓存过期时间必须显式定义，禁止无说明地永久缓存。
3. 使用 Redis Stream、分布式锁、异步任务时，必须考虑幂等、重复消费、异常恢复。
4. 并发代码禁止使用模糊不清的共享变量写法。
5. 线程池必须显式配置，禁止直接大量使用默认线程池。

### 16.10 注释规范

1. 所有类、核心方法、复杂逻辑必须有中文注释。
2. 注释应解释“为什么这样做”，而不是机械复述代码。
3. 禁止保留大量失效注释、注释掉的大段废弃代码。
4. 对 AI 提示词装配、向量检索策略、文件解析规则等复杂逻辑，必须补充清晰注释。

### 16.11 AI 相关编码规范

1. Prompt 模板统一放在 `src/main/resources/prompts/` 下，不允许在业务代码中大段硬编码 Prompt。
2. 模型调用参数必须集中配置，不要散落在多个类中硬编码。
3. AI 返回结果若进入业务流程，优先使用结构化对象承接。
4. AI 调用失败、超时、降级、重试策略必须显式处理。
5. 向量检索、Embedding、RAG 相关代码应封装在 `infrastructure.ai`、`infrastructure.vector` 或对应业务模块中，避免 Controller 直接调用底层实现。

### 16.12 安全规范

1. 禁止硬编码密钥、口令、Token、数据库密码。
2. 敏感配置统一从环境变量或配置文件占位读取。
3. 上传文件必须校验类型、大小和必要的安全限制。
4. 接口鉴权、权限校验、安全配置统一收敛到 `security` 模块。
5. 禁止在日志、异常、返回体中泄露敏感信息。

### 16.13 禁止事项

禁止生成以下代码风格：

- 巨大的 God Class
- 过长方法
- 过深嵌套 if/else
- 魔法值散落
- 复制粘贴式重复代码
- Controller 直连 Repository
- 直接返回裸对象、Map、List、String 作为接口标准响应
- 硬编码 Prompt、URL、密钥、路径
- 未分类的 util 万能类滥用
- 无注释的复杂逻辑

### 16.14 代码生成执行要求

Codex 在生成或修改后端代码时，必须主动自检以下内容：

1. 是否符合阿里巴巴开发规范的基本命名、分层、异常、日志要求。
2. 是否遵循本项目统一返回体 `Result<T>`。
3. 是否出现 Controller 过重、DTO/Entity 混用、硬编码配置、魔法值散落等问题。
4. 是否存在明显可抽取的重复代码。
5. 是否补充了必要注释、配置项与目录结构。

若发现不符合规范，应优先修正后再输出最终结果。

---

## 最终目标

最终产出应满足以下条件：

- 项目结构清晰
- 能直接启动
- 接口返回格式统一
- 具备后续接入 Spring AI、pgvector、Redis、鉴权、文件上传、知识库检索的扩展能力
- 适合作为 Java + AI 项目的正式后端起点
