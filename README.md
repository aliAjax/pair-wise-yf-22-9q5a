# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录和追溯查询。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

放行链 CLI 示例（本地开发可用 `X-Role` 头代替 JWT）：

```bash
# 1. 质检员提交放行（同一批次同一周期内先到生效，晚到留待复核）
curl -X POST http://localhost:21114/api/batches/BATCH-20261006-001/release-submissions \
  -H 'X-Role: INSPECTOR' -H 'X-User-Id: insp-01' \
  -H 'Content-Type: application/json' -d '{"note":"终检合格"}'

# 2. 登记一条严重不良：已放行批次立即被拉回待处置
curl -X POST http://localhost:21114/api/defects \
  -H 'X-Role: LINE_SUPERVISOR' -H 'Content-Type: application/json' \
  -d '{"batchId":1,"defectType":"裂纹","defectQty":3,"severity":"CRITICAL","rootCause":"铸造应力"}'

# 3. 处置不良（放行结论随之作废重算，但待处置保持锁定）
curl -X POST http://localhost:21114/api/defects/1/disposition \
  -H 'X-Role: LINE_SUPERVISOR' -H 'Content-Type: application/json' \
  -d '{"dispositionStatus":"DISPOSED","rootCause":"返修完成"}'

# 4. 只有质量经理能解除待处置，其他岗位提交直接 403
curl -X POST http://localhost:21114/api/batches/BATCH-20261006-001/release/resolve \
  -H 'X-Role: QUALITY_MANAGER' -H 'X-User-Id: qm-01'

# 5. 全链路追溯：工单 -> 批次 -> 检验 -> 不良 -> 放行结论
curl http://localhost:21114/api/trace/BATCH-20261006-001
```

## 放行链说明

工单、批次、终检结论和不良记录被接成一条放行链，批次放行结论（`BatchRelease`）按批次缓存：

| 状态 | 含义 |
|---|---|
| `PENDING_RELEASE` | 待放行：终检未做或未通过 |
| `RELEASED` | 已放行：终检通过且无未处置严重不良 |
| `PENDING_DISPOSITION` | 待处置：存在未处置严重不良（`CRITICAL` + `OPEN`），批次被锁定 |

规则：

1. **严重不良锁批次**：只要还有未处置的严重不良，批次就停在待处置；终检通过也不能放行。
2. **结论作废重算**：不良记录登记/处置一变，已算出的放行结论立即作废（`stale=true`、放行周期 `cycle+1`）并重算；已放行的批次会被拉回待处置。
3. **先到生效，晚到待复核**：同一批次同一放行周期内，第一个放行提交 `APPLIED`，其余 `PENDING_RECHECK`（原子占位 + 每批次一把锁保证并发正确）。
4. **待处置是粘性的**：不良处置完后批次不会自动放行，只有 `QUALITY_MANAGER` 调 `POST /api/batches/{batchNo}/release/resolve` 才能解除；其他岗位越权提交直接 `403 RELEASE_FORBIDDEN`；仍有未处置严重不良时连质量经理也解除不了（`409 RELEASE_STILL_BLOCKED`）。

角色（`constants/RoleCode`）：`INSPECTOR` 质检员、`LINE_SUPERVISOR` 产线主管、`QUALITY_MANAGER` 质量经理、`AUDITOR` 审计员（只读）。身份来自 HS256 JWT（`Authorization: Bearer`，claims 含 `sub`/`role`）或本地开发的 `X-Role`/`X-User-Id` 头。

## 本地开发方式


- 后端：进入 `backend` 后按技术栈运行开发命令，接口统一挂在 `/api`。


## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text

backend/src/routes, controllers, services, models, repositories, middlewares, constants, constructors, utils, types, config
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`

- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据
- `JWT_SECRET`: HS256 JWT 验签密钥，默认 `local-dev-secret`

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus: constants/WorkOrderStatus、types/WorkOrderStatus、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- InspectionResultStatus: constants/InspectionResultStatus、types/InspectionResultStatus、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- DefectSeverity: constants/DefectSeverity、types/DefectSeverity、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- BatchReleaseStatus: constants/BatchReleaseStatus、services/ReleaseChainService、constructors/BatchReleaseDtoFactory、utils/Formatters、LogTemplates、database/init.sql（batch_release 表）。
- ReleaseSubmissionOutcome: constants/ReleaseSubmissionOutcome、services/ReleaseChainService、constructors/ReleaseSubmissionDtoFactory、utils/Formatters、LogTemplates、database/init.sql（release_submission 表）。
- DefectDispositionStatus: constants/DefectDispositionStatus、repositories/DefectRecordRepository、services/DefectRecordService、validators/DefectRecordValidator。
- RoleCode: constants/RoleCode、middlewares/RbacMiddleware、services/ReleaseChainService、controllers/BatchReleaseController、controllers/DefectRecordController。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、筛选器和展示组件被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、store、页面、README 与数据库种子。

## License

MIT
