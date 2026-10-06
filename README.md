# quality-trace 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务。本服务把**工单、批次、质检结论与不良记录接成一条放行链**：终检合格且无未处置严重不良时批次才可放行；只要还有未处置的严重不良，批次就停在「待处置」；不良记录一旦变更，已算出的放行结论立即作废并重算。质检员与产线主管同时提交同一批次时，先到的生效，晚到的留在「待复核」；只有质量经理能解除待处置，其它岗位越权提交直接拒绝。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

启动后访问：

- 健康检查：<http://localhost:21114/health>
- 接口前缀：`http://localhost:21114/api`

## 访问地址或 CLI 示例

```bash
# 健康检查
curl http://localhost:21114/health

# 登录（种子账号，密码均为 password123）
curl -X POST http://localhost:21114/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager1","password":"password123"}'

# 查询批次全链路追溯树（把工单/批次/检验/不良/放行结论串成一条链）
curl http://localhost:21114/api/trace/BATCH-0001

# 质量经理放行批次（解除待处置）
curl -X POST http://localhost:21114/api/batches/1/release \
  -H "Authorization: Bearer <manager-token>"
```

种子账号：

| 用户名 | 角色 | 密码 |
|---|---|---|
| inspector1 | 质检员 INSPECTOR | password123 |
| supervisor1 | 产线主管 LINE_SUPERVISOR | password123 |
| manager1 | 质量经理 QUALITY_MANAGER | password123 |
| auditor1 | 审计员 AUDITOR | password123 |

## 本地开发方式

- 技术栈：Spring Boot 3 + Java 17 + MyBatis-Plus + PostgreSQL 15。
- 本地需要 JDK 17 与 Maven 3.9+，以及一个 PostgreSQL 15 实例。
- 初始化数据库：执行 `database/init.sql`（含种子账号与示例批次）。
- 后端：进入 `backend` 后执行 `mvn spring-boot:run`，接口统一挂在 `/api`。
- 数据库连接通过环境变量覆盖（见 `backend/src/main/resources/application.yml`）：`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD`、`JWT_SECRET`。

```bash
cd backend
mvn spring-boot:run
# 或打包后运行
mvn -DskipTests package
java -jar target/quality-trace-0.1.0.jar --server.port=8080
```

## 核心业务规则（放行链）

1. **一条链**：`工单 → 批次 → 质检结论 / 不良记录 → 放行结论`。
2. **待处置**：只要存在未处置的严重不良（severity 为 MAJOR/CRITICAL 且 disposition_status ≠ CLOSED），批次状态立即置为 `HOLD_FOR_DISPOSITION`（待处置）。
3. **作废重算**：不良记录一登记或处置变更，`release_conclusion.valid` 先置为 false（作废），随后立即重算。
4. **先到先得**：同一批次同一检验类型只允许一张「生效」检验单。质检员与产线主管同时提交时，依赖数据库局部唯一索引 `uq_effective_inspection` 先到先得，先到的置为 EFFECTIVE，晚到的置为 PENDING_REVIEW（待复核），不覆盖已有结论。
5. **只有质量经理能放行**：`POST /api/batches/{id}/release` 标注 `@RequireRole(QUALITY_MANAGER)`，其它角色越权调用由 RBAC 中间件直接 403 拒绝。

放行结论状态：

| 状态 | 含义 |
|---|---|
| PENDING_FINAL_INSPECTION | 尚无生效终检，无法放行 |
| HOLD_FOR_DISPOSITION | 终检未过或存在未处置严重不良，批次停在待处置 |
| RELEASED | 终检合格且无未处置严重不良，质量经理已放行 |

## 核心 API

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| POST | `/api/auth/login` | 登录签发 JWT | 公开 |
| POST | `/api/work-orders` | 创建工单（校验产品编码） | 质量经理/产线主管 |
| POST | `/api/batches` | 创建批次，同时初始放行链 | 质量经理/产线主管 |
| POST | `/api/inspections` | 提交检验，逐项写入检验结果（先到先生效） | 质检员/产线主管 |
| POST | `/api/defects` | 登记不良，反向联动批次状态 | 质检员/质量经理 |
| POST | `/api/defects/{id}/disposition` | 处置/关闭不良，触发放行结论作废重算 | 质检员/质量经理 |
| POST | `/api/batches/{id}/release` | 质量经理放行/解除待处置 | 仅质量经理 |
| GET | `/api/batches/{id}/release-conclusion` | 查询当前放行结论（缺失/作废自动重算） | 登录用户 |
| GET | `/api/trace/{batchNo}` | 查询批次全链路追溯树 | 登录用户 |

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | -（纯后端服务） |
| 后端 | Spring Boot 3.3 + Java 17 + MyBatis-Plus 3.5 |
| 数据库 | PostgreSQL 15 |
| 认证 | JWT（HMAC-SHA256）+ RBAC |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/main/java/com/generated/qualityTrace/
├── QualityTraceApplication.java   # 入口
├── config/                        # AppConfig / MybatisPlusConfig / WebConfig
├── common/                        # BusinessException
├── constants/                     # 枚举、错误码、错误消息、日志模板
├── constructors/                  # 请求/响应 DTO 构造器与响应对象
├── controllers/                   # 按实体分文件
├── middlewares/                   # 认证 / RBAC / 限流 / 审计 / 全局异常
├── models/                        # 实体（MyBatis-Plus 表映射）
├── repositories/                  # 数据访问层（Mapper）
├── routes/                        # 路由常量
├── services/                      # 按实体分文件（含放行链核心服务）
├── types/                         # 请求载荷 DTO（record）
├── utils/                         # JWT / 格式化 / 日期 / 当前用户
└── validators/                    # 入参校验
```

## 环境变量说明

| 变量 | 说明 | 默认 |
|---|---|---|
| `COMPOSE_PROJECT_NAME` | Compose 项目名，容器名前缀 | `quality-trace` |
| `BACKEND_PORT` | 后端宿主机端口（容器内 8080） | `21114` |
| `DB_PORT` | 数据库宿主机端口（容器内 5432） | `54320` |
| `DB_USER` / `DB_PASSWORD` / `DB_NAME` | 数据库凭据 | `app_user` / `app_password` / `app_db` |
| `JWT_SECRET` | JWT 签名密钥 | 本地开发密钥 |
| `JWT_EXPIRATION_MS` | 令牌有效期（毫秒） | `86400000` |
| `RATE_LIMIT_MAX` / `RATE_LIMIT_WINDOW_MS` | 限流阈值与窗口 | `120` / `60000` |

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷 `db_data`，避免绑定中文路径。
- 数据库配置 healthcheck，后端通过 `depends_on: condition: service_healthy` 等待数据库就绪。
- 后端提供 `/health` 健康检查。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- **WorkOrderStatus**（PLANNED/RUNNING/PAUSED/FINISHED/CANCELLED）
  - 常量：`constants/WorkOrderStatus.java`
  - 类型/校验：`validators/WorkOrderValidator.java`、`types/WorkOrderPayload.java`
  - 日志：`constants/LogTemplates.java`（WO_STATUS_CHANGED 等）
  - 错误：`constants/ErrorCodes.java`（WORK_ORDER_STATUS_INVALID）、`constants/ErrorMessages.java`
  - 格式化：`utils/Formatters.java`（workOrderStatusLabel）
  - 构造器/控制器：`constructors/WorkOrderDtoFactory.java`、`controllers/WorkOrderController.java`
- **InspectionResultStatus**（PASS/FAIL/CONDITIONAL_PASS/RECHECK）
  - 常量：`constants/InspectionResultStatus.java`
  - 类型/校验：`validators/QualityInspectionValidator.java`
  - 日志：`constants/LogTemplates.java`（INSPECTION_EFFECTIVE 等）
  - 错误：`constants/ErrorCodes.java`（INSPECTION_RESULT_INVALID）、`constants/ErrorMessages.java`
  - 格式化：`utils/Formatters.java`（inspectionResultLabel）
  - 构造器/控制器：`constructors/QualityInspectionDtoFactory.java`、`controllers/QualityInspectionController.java`
- **DefectSeverity**（MINOR/MAJOR/CRITICAL）
  - 常量：`constants/DefectSeverity.java`（含 isBlocking）
  - 类型/校验：`validators/DefectRecordValidator.java`
  - 日志：`constants/LogTemplates.java`（DEFECT_REGISTERED、DEFECT_SEVERITY_CLASSIFIED）
  - 错误：`constants/ErrorCodes.java`（DEFECT_SEVERITY_INVALID）、`constants/ErrorMessages.java`
  - 格式化：`utils/Formatters.java`（defectSeverityLabel、riskLevel）
  - 构造器/控制器：`constructors/DefectRecordDtoFactory.java`、`controllers/DefectRecordController.java`
- 其它共享枚举：`DefectDispositionStatus`、`ReleaseConclusionStatus`、`ReviewStatus`、`UserRole`、`BatchStatus`。

## 为什么该项目会牵一发动全身

实体字段、枚举、日志模板、错误码、错误消息、构造器、校验器、格式化器与路由常量被刻意拆散到多个目录，并被 controller/service/repository/middleware 多层直接引用。例如：

- 修改一个不良严重等级，需要同步常量、校验器、日志模板、错误码、格式化器（风险等级）、DTO 构造器与数据库种子；
- 修改放行结论状态，需要同步 `ReleaseConclusionStatus`、`ReleaseChainService`、批次状态联动、日志模板与错误消息；
- 错误码集中在 `constants/ErrorCodes`，文案集中在 `constants/ErrorMessages`，service 与 controller 分别包装异常，全局处理器只负责统一格式化；
- 日志模板集中在 `constants/LogTemplates`，所有写操作都记录日志，字段变更必须同步改模板与调用处。

## License

MIT
