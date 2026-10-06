-- H2 内存库测试 schema（PostgreSQL 兼容模式，仅用于单元测试，不含局部唯一索引）
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS release_conclusion;
DROP TABLE IF EXISTS defect_record;
DROP TABLE IF EXISTS inspection_item_result;
DROP TABLE IF EXISTS quality_inspection;
DROP TABLE IF EXISTS product_batch;
DROP TABLE IF EXISTS work_order;
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(64) UNIQUE NOT NULL,
  password_hash VARCHAR(128) NOT NULL,
  role VARCHAR(32) NOT NULL,
  display_name VARCHAR(128),
  created_at TIMESTAMP
);

CREATE TABLE work_order (
  id BIGSERIAL PRIMARY KEY,
  order_no VARCHAR(64) UNIQUE NOT NULL,
  product_code VARCHAR(64) NOT NULL,
  product_name VARCHAR(128),
  planned_qty INT,
  line_code VARCHAR(32),
  start_at TIMESTAMP,
  status VARCHAR(32) NOT NULL
);

CREATE TABLE product_batch (
  id BIGSERIAL PRIMARY KEY,
  batch_no VARCHAR(64) UNIQUE NOT NULL,
  work_order_id BIGINT,
  quantity INT,
  material_lot_no VARCHAR(64),
  produced_at TIMESTAMP,
  batch_status VARCHAR(32) NOT NULL
);

CREATE TABLE quality_inspection (
  id BIGSERIAL PRIMARY KEY,
  batch_id BIGINT,
  inspector_id BIGINT,
  inspection_type VARCHAR(32) NOT NULL,
  standard_version VARCHAR(32),
  result_status VARCHAR(32),
  review_status VARCHAR(32) NOT NULL DEFAULT 'EFFECTIVE',
  inspected_at TIMESTAMP
);

CREATE TABLE inspection_item_result (
  id BIGSERIAL PRIMARY KEY,
  inspection_id BIGINT,
  item_code VARCHAR(64),
  item_name VARCHAR(128),
  measured_value VARCHAR(64),
  limit_min VARCHAR(64),
  limit_max VARCHAR(64),
  item_status VARCHAR(32)
);

CREATE TABLE defect_record (
  id BIGSERIAL PRIMARY KEY,
  batch_id BIGINT,
  defect_type VARCHAR(64),
  defect_qty INT,
  severity VARCHAR(16),
  root_cause CLOB,
  disposition_status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE release_conclusion (
  id BIGSERIAL PRIMARY KEY,
  batch_id BIGINT UNIQUE,
  conclusion_status VARCHAR(32) NOT NULL,
  valid BOOLEAN NOT NULL DEFAULT true,
  effective_final_inspection_id BIGINT,
  blocking_defect_count INT DEFAULT 0,
  computed_at TIMESTAMP,
  released_at TIMESTAMP,
  released_by VARCHAR(64)
);

CREATE TABLE audit_log (
  id BIGSERIAL PRIMARY KEY,
  actor VARCHAR(64),
  action VARCHAR(64),
  target_type VARCHAR(64),
  target_id VARCHAR(64),
  detail CLOB,
  created_at TIMESTAMP
);
