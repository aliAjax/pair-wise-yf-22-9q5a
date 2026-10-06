CREATE TABLE IF NOT EXISTS work_order (
  id INTEGER PRIMARY KEY,
  order_no TEXT,
  product_code TEXT,
  product_name TEXT,
  planned_qty TEXT,
  line_code TEXT,
  start_at TEXT,
  status TEXT
);

CREATE TABLE IF NOT EXISTS product_batch (
  id INTEGER PRIMARY KEY,
  batch_no TEXT,
  work_order_id TEXT,
  quantity TEXT,
  material_lot_no TEXT,
  produced_at TEXT,
  batch_status TEXT
);

CREATE TABLE IF NOT EXISTS quality_inspection (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  inspector_id TEXT,
  inspection_type TEXT,
  standard_version TEXT,
  result_status TEXT,
  inspected_at TEXT
);

CREATE TABLE IF NOT EXISTS inspection_item_result (
  id INTEGER PRIMARY KEY,
  inspection_id TEXT,
  item_code TEXT,
  item_name TEXT,
  measured_value TEXT,
  limit_min TEXT,
  limit_max TEXT,
  item_status TEXT
);

CREATE TABLE IF NOT EXISTS defect_record (
  id INTEGER PRIMARY KEY,
  batch_id TEXT,
  defect_type TEXT,
  defect_qty TEXT,
  severity TEXT,
  root_cause TEXT,
  disposition_status TEXT
);

CREATE TABLE IF NOT EXISTS audit_log (
  id INTEGER PRIMARY KEY,
  actor TEXT,
  action TEXT,
  target_type TEXT,
  target_id TEXT,
  created_at TEXT
);

-- 放行结论：按批次缓存的放行链计算结果，不良记录一变即作废重算
CREATE TABLE IF NOT EXISTS batch_release (
  id INTEGER PRIMARY KEY,
  batch_no TEXT,
  work_order_id TEXT,
  status TEXT,
  reason TEXT,
  cycle INTEGER,
  stale TEXT,
  computed_at TEXT
);

-- 放行提交：同一批次同一周期内先到生效（APPLIED），晚到留待复核（PENDING_RECHECK）
CREATE TABLE IF NOT EXISTS release_submission (
  id INTEGER PRIMARY KEY,
  batch_no TEXT,
  cycle INTEGER,
  actor_id TEXT,
  role TEXT,
  outcome TEXT,
  note TEXT,
  submitted_at TEXT
);
