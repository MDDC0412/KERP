CREATE TABLE IF NOT EXISTS inv_stock_outbound (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    outbound_no VARCHAR(64) NOT NULL,
    stock_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(128) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    outbound_qty DECIMAL(18, 2) NOT NULL,
    outbound_date DATE NOT NULL,
    outbound_by VARCHAR(64) NOT NULL,
    purpose VARCHAR(255) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_inv_stock_outbound_no UNIQUE (outbound_no),
    CONSTRAINT fk_inv_stock_outbound_stock FOREIGN KEY (stock_id) REFERENCES inv_stock(id)
);

CREATE TABLE IF NOT EXISTS inv_stock_transfer (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transfer_no VARCHAR(64) NOT NULL,
    stock_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(128) NOT NULL,
    from_warehouse VARCHAR(128) NOT NULL,
    to_warehouse VARCHAR(128) NOT NULL,
    transfer_qty DECIMAL(18, 2) NOT NULL,
    transfer_date DATE NOT NULL,
    transfer_by VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_inv_stock_transfer_no UNIQUE (transfer_no),
    CONSTRAINT fk_inv_stock_transfer_stock FOREIGN KEY (stock_id) REFERENCES inv_stock(id)
);

CREATE TABLE IF NOT EXISTS inv_stock_check (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    check_no VARCHAR(64) NOT NULL,
    stock_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(128) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    system_qty DECIMAL(18, 2) NOT NULL,
    actual_qty DECIMAL(18, 2) NOT NULL,
    variance_qty DECIMAL(18, 2) NOT NULL,
    check_date DATE NOT NULL,
    checked_by VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'COUNTED',
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_inv_stock_check_no UNIQUE (check_no),
    CONSTRAINT fk_inv_stock_check_stock FOREIGN KEY (stock_id) REFERENCES inv_stock(id)
);

INSERT INTO inv_stock_outbound (
    outbound_no, stock_id, sku_code, sku_name, warehouse_name,
    outbound_qty, outbound_date, outbound_by, purpose, status
)
SELECT
    'OUT-202604-001', s.id, s.sku_code, s.sku_name, s.warehouse_name,
    1.00, '2026-04-22', 'admin', 'Seed outbound for office setup', 'COMPLETED'
FROM inv_stock s
WHERE s.sku_code = 'NB-14-001'
  AND s.warehouse_name = 'Main Warehouse'
  AND NOT EXISTS (SELECT 1 FROM inv_stock_outbound WHERE outbound_no = 'OUT-202604-001');

INSERT INTO inv_stock_transfer (
    transfer_no, stock_id, sku_code, sku_name, from_warehouse, to_warehouse,
    transfer_qty, transfer_date, transfer_by, status
)
SELECT
    'TRF-202604-001', s.id, s.sku_code, s.sku_name, s.warehouse_name, 'Branch Warehouse',
    1.00, '2026-04-23', 'admin', 'COMPLETED'
FROM inv_stock s
WHERE s.sku_code = 'NB-14-001'
  AND s.warehouse_name = 'Main Warehouse'
  AND NOT EXISTS (SELECT 1 FROM inv_stock_transfer WHERE transfer_no = 'TRF-202604-001');

INSERT INTO inv_stock (
    sku_code, sku_name, warehouse_name, supplier_id,
    on_hand_qty, reserved_qty, available_qty, last_receipt_date
)
SELECT
    'NB-14-001', 'Business Notebook 14', 'Branch Warehouse', s.id,
    1.00, 0.00, 1.00, '2026-04-23'
FROM pur_supplier s
WHERE s.code = 'SUP-001'
  AND NOT EXISTS (
      SELECT 1
      FROM inv_stock
      WHERE sku_code = 'NB-14-001'
        AND warehouse_name = 'Branch Warehouse'
        AND supplier_id = s.id
  );

UPDATE inv_stock s
JOIN pur_supplier sp ON sp.id = s.supplier_id
SET s.on_hand_qty = 2.00,
    s.available_qty = 2.00,
    s.reserved_qty = 0.00
WHERE s.sku_code = 'NB-14-001'
  AND s.warehouse_name = 'Main Warehouse'
  AND sp.code = 'SUP-001';

INSERT INTO inv_stock_check (
    check_no, stock_id, sku_code, sku_name, warehouse_name,
    system_qty, actual_qty, variance_qty, check_date, checked_by, status, remark
)
SELECT
    'CHK-202604-001', s.id, s.sku_code, s.sku_name, s.warehouse_name,
    s.on_hand_qty, s.on_hand_qty, 0.00, '2026-04-24', 'admin', 'COUNTED', 'Seed stock check'
FROM inv_stock s
WHERE s.sku_code = 'NB-14-001'
  AND s.warehouse_name = 'Main Warehouse'
  AND NOT EXISTS (SELECT 1 FROM inv_stock_check WHERE check_no = 'CHK-202604-001');
