-- quality-trace 制造业质量追溯数据库 schema（PostgreSQL 15）
-- 首次启动时由 docker-entrypoint-initdb.d 执行

-- 用户表（RBAC）
CREATE TABLE IF NOT EXISTS app_user (
  id            BIGSERIAL PRIMARY KEY,
  username      VARCHAR(64) UNIQUE NOT NULL,
  password_hash VARCHAR(128) NOT NULL,
  role          VARCHAR(32) NOT NULL,
  display_name  VARCHAR(128),
  created_at    TIMESTAMP DEFAULT now()
);

-- 生产工单
CREATE TABLE IF NOT EXISTS work_order (
  id            BIGSERIAL PRIMARY KEY,
  order_no      VARCHAR(64) UNIQUE NOT NULL,
  product_code  VARCHAR(64) NOT NULL,
  product_name  VARCHAR(128),
  planned_qty   INTEGER,
  line_code     VARCHAR(32),
  start_at      TIMESTAMP,
  status        VARCHAR(32) NOT NULL
);

-- 产品批次
CREATE TABLE IF NOT EXISTS product_batch (
  id              BIGSERIAL PRIMARY KEY,
  batch_no        VARCHAR(64) UNIQUE NOT NULL,
  work_order_id   BIGINT REFERENCES work_order(id),
  quantity        INTEGER,
  material_lot_no VARCHAR(64),
  produced_at     TIMESTAMP,
  batch_status    VARCHAR(32) NOT NULL
);

-- 质量检验单
CREATE TABLE IF NOT EXISTS quality_inspection (
  id               BIGSERIAL PRIMARY KEY,
  batch_id         BIGINT REFERENCES product_batch(id),
  inspector_id     BIGINT REFERENCES app_user(id),
  inspection_type  VARCHAR(32) NOT NULL,
  standard_version VARCHAR(32),
  result_status    VARCHAR(32),
  review_status    VARCHAR(32) NOT NULL DEFAULT 'EFFECTIVE',
  inspected_at     TIMESTAMP
);

-- 同批次同检验类型只允许一张生效检验单（先到先得，晚到转待复核）
CREATE UNIQUE INDEX IF NOT EXISTS uq_effective_inspection
  ON quality_inspection (batch_id, inspection_type)
  WHERE review_status = 'EFFECTIVE';

-- 检验项结果
CREATE TABLE IF NOT EXISTS inspection_item_result (
  id             BIGSERIAL PRIMARY KEY,
  inspection_id  BIGINT REFERENCES quality_inspection(id),
  item_code      VARCHAR(64),
  item_name      VARCHAR(128),
  measured_value VARCHAR(64),
  limit_min      VARCHAR(64),
  limit_max      VARCHAR(64),
  item_status    VARCHAR(32)
);

-- 不良记录
CREATE TABLE IF NOT EXISTS defect_record (
  id                 BIGSERIAL PRIMARY KEY,
  batch_id           BIGINT REFERENCES product_batch(id),
  defect_type        VARCHAR(64),
  defect_qty         INTEGER,
  severity           VARCHAR(16),
  root_cause         TEXT,
  disposition_status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_at         TIMESTAMP DEFAULT now(),
  updated_at         TIMESTAMP DEFAULT now()
);

-- 放行结论（计算结果缓存，不良变更即作废重算）
CREATE TABLE IF NOT EXISTS release_conclusion (
  id                            BIGSERIAL PRIMARY KEY,
  batch_id                      BIGINT UNIQUE REFERENCES product_batch(id),
  conclusion_status             VARCHAR(32) NOT NULL,
  valid                         BOOLEAN NOT NULL DEFAULT true,
  effective_final_inspection_id BIGINT,
  blocking_defect_count         INTEGER DEFAULT 0,
  computed_at                   TIMESTAMP,
  released_at                   TIMESTAMP,
  released_by                   VARCHAR(64)
);

-- 操作 / 追溯事件日志
CREATE TABLE IF NOT EXISTS audit_log (
  id          BIGSERIAL PRIMARY KEY,
  actor       VARCHAR(64),
  action      VARCHAR(64),
  target_type VARCHAR(64),
  target_id   VARCHAR(64),
  detail      TEXT,
  created_at  TIMESTAMP DEFAULT now()
);

-- ============ 种子数据 ============
-- 密码均为 password123（BCrypt 强度 10）
INSERT INTO app_user (username, password_hash, role, display_name) VALUES
  ('inspector1',       '$2a$10$MZM9dXJZQ8u6nopY0xFQQu6eKxrU/ivGwM9gRuQyEYpvOoBOYD.cO', 'INSPECTOR',       '质检员小周'),
  ('supervisor1',      '$2a$10$MZM9dXJZQ8u6nopY0xFQQu6eKxrU/ivGwM9gRuQyEYpvOoBOYD.cO', 'LINE_SUPERVISOR', '产线主管老吴'),
  ('manager1',         '$2a$10$MZM9dXJZQ8u6nopY0xFQQu6eKxrU/ivGwM9gRuQyEYpvOoBOYD.cO', 'QUALITY_MANAGER', '质量经理老郑'),
  ('auditor1',         '$2a$10$MZM9dXJZQ8u6nopY0xFQQu6eKxrU/ivGwM9gRuQyEYpvOoBOYD.cO', 'AUDITOR',         '审计员小林')
ON CONFLICT (username) DO NOTHING;

-- 一张运行中的工单
INSERT INTO work_order (order_no, product_code, product_name, planned_qty, line_code, start_at, status)
VALUES ('WO-2026-0001', 'P-1001', '铝合金外壳', 1000, 'LINE-A', now(), 'RUNNING')
ON CONFLICT (order_no) DO NOTHING;

-- 一个批次（待终检）
INSERT INTO product_batch (batch_no, work_order_id, quantity, material_lot_no, produced_at, batch_status)
SELECT 'BATCH-0001', w.id, 500, 'LOT-2026-0901', now(), 'PENDING_FINAL_INSPECTION'
FROM work_order w WHERE w.order_no = 'WO-2026-0001'
ON CONFLICT (batch_no) DO NOTHING;
