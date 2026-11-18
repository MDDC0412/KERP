CREATE TABLE IF NOT EXISTS pur_supplier (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    contact_person VARCHAR(64) NULL,
    contact_phone VARCHAR(32) NULL,
    payment_terms VARCHAR(128) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_pur_supplier_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS pur_purchase_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL,
    supplier_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(128) NOT NULL,
    quantity DECIMAL(18, 2) NOT NULL,
    unit_price DECIMAL(18, 2) NOT NULL,
    total_amount DECIMAL(18, 2) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    order_date DATE NOT NULL,
    expected_date DATE NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_pur_purchase_order_no UNIQUE (order_no),
    CONSTRAINT fk_pur_purchase_order_supplier FOREIGN KEY (supplier_id) REFERENCES pur_supplier(id)
);

CREATE TABLE IF NOT EXISTS pur_purchase_receipt (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    receipt_no VARCHAR(64) NOT NULL,
    order_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(128) NOT NULL,
    received_qty DECIMAL(18, 2) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    received_date DATE NOT NULL,
    received_by VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'RECEIVED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_pur_purchase_receipt_no UNIQUE (receipt_no),
    CONSTRAINT fk_pur_purchase_receipt_order FOREIGN KEY (order_id) REFERENCES pur_purchase_order(id),
    CONSTRAINT fk_pur_purchase_receipt_supplier FOREIGN KEY (supplier_id) REFERENCES pur_supplier(id)
);

CREATE TABLE IF NOT EXISTS inv_stock (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(128) NOT NULL,
    warehouse_name VARCHAR(128) NOT NULL,
    supplier_id BIGINT NULL,
    on_hand_qty DECIMAL(18, 2) NOT NULL DEFAULT 0,
    reserved_qty DECIMAL(18, 2) NOT NULL DEFAULT 0,
    available_qty DECIMAL(18, 2) NOT NULL DEFAULT 0,
    last_receipt_date DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_inv_stock_sku_warehouse_supplier UNIQUE (sku_code, warehouse_name, supplier_id),
    CONSTRAINT fk_inv_stock_supplier FOREIGN KEY (supplier_id) REFERENCES pur_supplier(id)
);

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'PROCUREMENT_MANAGE', 'Procurement Manage', 'PROCUREMENT', 'ACTION'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'PROCUREMENT_MANAGE');

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'INVENTORY_MANAGE', 'Inventory Manage', 'INVENTORY', 'ACTION'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'INVENTORY_MANAGE');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.code IN ('PROCUREMENT_MANAGE', 'INVENTORY_MANAGE')
WHERE r.code = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO pur_supplier (code, name, contact_person, contact_phone, payment_terms, status)
SELECT 'SUP-001', 'Default Supplier', 'Alice', '13800000001', 'Net 30', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM pur_supplier WHERE code = 'SUP-001');

INSERT INTO pur_purchase_order (
    order_no, supplier_id, sku_code, sku_name, quantity, unit_price, total_amount,
    warehouse_name, order_date, expected_date, status, remark
)
SELECT
    'PO-202604-001', s.id, 'NB-14-001', 'Business Notebook 14', 10.00, 5200.00, 52000.00,
    'Main Warehouse', '2026-04-20', '2026-04-25', 'PARTIAL_RECEIVED', 'Seed purchase order'
FROM pur_supplier s
WHERE s.code = 'SUP-001'
  AND NOT EXISTS (SELECT 1 FROM pur_purchase_order WHERE order_no = 'PO-202604-001');

INSERT INTO pur_purchase_receipt (
    receipt_no, order_id, supplier_id, sku_code, sku_name, received_qty,
    warehouse_name, received_date, received_by, status
)
SELECT
    'RCV-202604-001', po.id, po.supplier_id, po.sku_code, po.sku_name, 4.00,
    po.warehouse_name, '2026-04-21', 'admin', 'RECEIVED'
FROM pur_purchase_order po
WHERE po.order_no = 'PO-202604-001'
  AND NOT EXISTS (SELECT 1 FROM pur_purchase_receipt WHERE receipt_no = 'RCV-202604-001');

INSERT INTO inv_stock (
    sku_code, sku_name, warehouse_name, supplier_id,
    on_hand_qty, reserved_qty, available_qty, last_receipt_date
)
SELECT
    'NB-14-001', 'Business Notebook 14', 'Main Warehouse', s.id,
    4.00, 0.00, 4.00, '2026-04-21'
FROM pur_supplier s
WHERE s.code = 'SUP-001'
  AND NOT EXISTS (
      SELECT 1
      FROM inv_stock
      WHERE sku_code = 'NB-14-001'
        AND warehouse_name = 'Main Warehouse'
        AND supplier_id = s.id
  );
