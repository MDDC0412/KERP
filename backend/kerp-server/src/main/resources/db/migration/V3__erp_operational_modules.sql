CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    module_name VARCHAR(64) NOT NULL,
    permission_type VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_permission_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_role_permission UNIQUE (role_id, permission_id),
    CONSTRAINT fk_sys_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
    CONSTRAINT fk_sys_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
);

CREATE TABLE IF NOT EXISTS sys_data_scope (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    scope_code VARCHAR(64) NOT NULL,
    scope_name VARCHAR(128) NOT NULL,
    scope_rule VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_data_scope_role_code UNIQUE (role_id, scope_code),
    CONSTRAINT fk_sys_data_scope_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
);

CREATE TABLE IF NOT EXISTS bpm_form_definition (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    category VARCHAR(64) NOT NULL,
    version_no INT NOT NULL DEFAULT 1,
    schema_json TEXT NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_bpm_form_definition_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS bpm_process_definition (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    form_code VARCHAR(64) NOT NULL,
    approval_type VARCHAR(32) NOT NULL,
    config_json TEXT NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_bpm_process_definition_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS bpm_approval_request (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_no VARCHAR(64) NOT NULL,
    approval_type VARCHAR(32) NOT NULL,
    process_code VARCHAR(64) NOT NULL,
    employee_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    form_data_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    submitted_at DATETIME NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_bpm_approval_request_no UNIQUE (request_no),
    CONSTRAINT fk_bpm_approval_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id)
);

CREATE TABLE IF NOT EXISTS att_shift (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    start_time VARCHAR(8) NOT NULL,
    end_time VARCHAR(8) NOT NULL,
    rest_minutes INT NOT NULL DEFAULT 60,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_att_shift_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS att_group (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    shift_id BIGINT NOT NULL,
    manager_user_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_att_group_code UNIQUE (code),
    CONSTRAINT fk_att_group_shift FOREIGN KEY (shift_id) REFERENCES att_shift(id),
    CONSTRAINT fk_att_group_manager FOREIGN KEY (manager_user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS att_schedule (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_group_id BIGINT NOT NULL,
    shift_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_att_schedule_employee_date UNIQUE (employee_id, schedule_date),
    CONSTRAINT fk_att_schedule_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id),
    CONSTRAINT fk_att_schedule_group FOREIGN KEY (attendance_group_id) REFERENCES att_group(id),
    CONSTRAINT fk_att_schedule_shift FOREIGN KEY (shift_id) REFERENCES att_shift(id)
);

CREATE TABLE IF NOT EXISTS att_clock_record (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    clock_date DATE NOT NULL,
    clock_in_time DATETIME NULL,
    clock_out_time DATETIME NULL,
    source VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_att_clock_record_employee_date UNIQUE (employee_id, clock_date),
    CONSTRAINT fk_att_clock_record_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id)
);

CREATE TABLE IF NOT EXISTS att_month_summary (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    summary_month VARCHAR(7) NOT NULL,
    should_attend_days DECIMAL(10,2) NOT NULL DEFAULT 0,
    actual_attend_days DECIMAL(10,2) NOT NULL DEFAULT 0,
    late_count INT NOT NULL DEFAULT 0,
    leave_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
    overtime_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_att_month_summary_employee_month UNIQUE (employee_id, summary_month),
    CONSTRAINT fk_att_month_summary_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id)
);

CREATE TABLE IF NOT EXISTS hr_employee_change (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    change_type VARCHAR(32) NOT NULL,
    before_value VARCHAR(255) NULL,
    after_value VARCHAR(255) NULL,
    effective_date DATE NOT NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_hr_employee_change_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id)
);

CREATE TABLE IF NOT EXISTS hr_onboarding (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    plan_date DATE NOT NULL,
    owner_user_id BIGINT NULL,
    checklist_json TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_hr_onboarding_employee UNIQUE (employee_id),
    CONSTRAINT fk_hr_onboarding_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id),
    CONSTRAINT fk_hr_onboarding_owner FOREIGN KEY (owner_user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS hr_offboarding (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    plan_date DATE NOT NULL,
    last_working_date DATE NOT NULL,
    reason VARCHAR(255) NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_hr_offboarding_employee UNIQUE (employee_id),
    CONSTRAINT fk_hr_offboarding_employee FOREIGN KEY (employee_id) REFERENCES hr_employee(id)
);

CREATE TABLE IF NOT EXISTS msg_notification (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    content VARCHAR(500) NOT NULL,
    target_type VARCHAR(32) NOT NULL,
    target_value VARCHAR(128) NOT NULL,
    channel VARCHAR(32) NOT NULL DEFAULT 'IN_APP',
    read_status TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    module_name VARCHAR(64) NOT NULL,
    action_name VARCHAR(64) NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    target_ref VARCHAR(128) NOT NULL,
    result_status VARCHAR(32) NOT NULL,
    detail VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS adm_asset_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    depreciation_years INT NOT NULL DEFAULT 3,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_adm_asset_category_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS adm_asset (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    asset_no VARCHAR(64) NOT NULL,
    category_id BIGINT NOT NULL,
    asset_name VARCHAR(128) NOT NULL,
    owner_employee_id BIGINT NULL,
    storage_location VARCHAR(128) NULL,
    asset_status VARCHAR(32) NOT NULL,
    purchase_date DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_adm_asset_no UNIQUE (asset_no),
    CONSTRAINT fk_adm_asset_category FOREIGN KEY (category_id) REFERENCES adm_asset_category(id),
    CONSTRAINT fk_adm_asset_owner FOREIGN KEY (owner_employee_id) REFERENCES hr_employee(id)
);

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'EMPLOYEE_VIEW', 'Employee View', 'EMPLOYEE', 'MENU'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'EMPLOYEE_VIEW');

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'APPROVAL_APPROVE', 'Approval Approve', 'APPROVAL', 'ACTION'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'APPROVAL_APPROVE');

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'ATTENDANCE_MANAGE', 'Attendance Manage', 'ATTENDANCE', 'ACTION'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'ATTENDANCE_MANAGE');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.code IN ('EMPLOYEE_VIEW', 'APPROVAL_APPROVE', 'ATTENDANCE_MANAGE')
WHERE r.code = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO sys_data_scope (role_id, scope_code, scope_name, scope_rule)
SELECT r.id, 'ALL', 'All Data', 'all'
FROM sys_role r
WHERE r.code = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM sys_data_scope ds WHERE ds.role_id = r.id AND ds.scope_code = 'ALL'
  );

INSERT INTO bpm_form_definition (code, name, category, version_no, schema_json, enabled)
SELECT 'LEAVE_FORM', 'Leave Form', 'APPROVAL', 1, '{"fields":["days","reason"]}', 1
WHERE NOT EXISTS (SELECT 1 FROM bpm_form_definition WHERE code = 'LEAVE_FORM');

INSERT INTO bpm_process_definition (code, name, form_code, approval_type, config_json, enabled)
SELECT 'LEAVE_PROCESS', 'Leave Process', 'LEAVE_FORM', 'LEAVE', '{"nodes":["manager","hr"]}', 1
WHERE NOT EXISTS (SELECT 1 FROM bpm_process_definition WHERE code = 'LEAVE_PROCESS');

INSERT INTO bpm_approval_request (request_no, approval_type, process_code, employee_id, title, form_data_json, status, submitted_at, approved_at)
SELECT 'APR-202604-001', 'LEAVE', 'LEAVE_PROCESS', e.id, 'Annual Leave Request', '{"days":2,"reason":"family"}', 'APPROVED', NOW(), NOW()
FROM hr_employee e
WHERE e.employee_no = 'E1001'
  AND NOT EXISTS (SELECT 1 FROM bpm_approval_request WHERE request_no = 'APR-202604-001');

INSERT INTO att_shift (code, name, start_time, end_time, rest_minutes)
SELECT 'DAY', 'Day Shift', '09:00', '18:00', 60
WHERE NOT EXISTS (SELECT 1 FROM att_shift WHERE code = 'DAY');

INSERT INTO att_group (code, name, shift_id, manager_user_id)
SELECT 'HQ_DAY', 'HQ Day Group', s.id, u.id
FROM att_shift s
LEFT JOIN sys_user u ON u.username = 'admin'
WHERE s.code = 'DAY'
  AND NOT EXISTS (SELECT 1 FROM att_group WHERE code = 'HQ_DAY');

INSERT INTO att_schedule (employee_id, attendance_group_id, shift_id, schedule_date)
SELECT e.id, g.id, s.id, CURRENT_DATE
FROM hr_employee e
JOIN att_group g ON g.code = 'HQ_DAY'
JOIN att_shift s ON s.code = 'DAY'
WHERE e.employee_no = 'E1001'
  AND NOT EXISTS (
      SELECT 1 FROM att_schedule sc WHERE sc.employee_id = e.id AND sc.schedule_date = CURRENT_DATE
  );

INSERT INTO att_clock_record (employee_id, clock_date, clock_in_time, clock_out_time, source, status)
SELECT e.id, CURRENT_DATE, NOW(), NOW(), 'WEB', 'NORMAL'
FROM hr_employee e
WHERE e.employee_no = 'E1001'
  AND NOT EXISTS (
      SELECT 1 FROM att_clock_record cr WHERE cr.employee_id = e.id AND cr.clock_date = CURRENT_DATE
  );

INSERT INTO att_month_summary (employee_id, summary_month, should_attend_days, actual_attend_days, late_count, leave_hours, overtime_hours)
SELECT e.id, DATE_FORMAT(CURRENT_DATE, '%Y-%m'), 21, 20, 1, 8, 6
FROM hr_employee e
WHERE e.employee_no = 'E1001'
  AND NOT EXISTS (
      SELECT 1 FROM att_month_summary ms WHERE ms.employee_id = e.id AND ms.summary_month = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
  );

INSERT INTO hr_employee_change (employee_id, change_type, before_value, after_value, effective_date, remark)
SELECT e.id, 'TRANSFER', 'Operations', 'HR', CURRENT_DATE, 'Initial seeded transfer'
FROM hr_employee e
WHERE e.employee_no = 'E1001'
  AND NOT EXISTS (
      SELECT 1 FROM hr_employee_change c WHERE c.employee_id = e.id AND c.change_type = 'TRANSFER'
  );

INSERT INTO hr_onboarding (employee_id, plan_date, owner_user_id, checklist_json, status)
SELECT e.id, CURRENT_DATE, u.id, '{"items":["account","desk","orientation"]}', 'DONE'
FROM hr_employee e
LEFT JOIN sys_user u ON u.username = 'admin'
WHERE e.employee_no = 'E1001'
  AND NOT EXISTS (SELECT 1 FROM hr_onboarding o WHERE o.employee_id = e.id);

INSERT INTO msg_notification (title, content, target_type, target_value, channel, read_status)
SELECT 'Leave approved', 'APR-202604-001 has been approved.', 'USER', 'admin', 'IN_APP', 0
WHERE NOT EXISTS (
    SELECT 1 FROM msg_notification WHERE title = 'Leave approved' AND target_value = 'admin'
);

INSERT INTO sys_audit_log (module_name, action_name, operator_name, target_ref, result_status, detail)
SELECT 'SYSTEM', 'BOOTSTRAP', 'system', 'V3_SEED', 'SUCCESS', 'Operational modules seeded'
WHERE NOT EXISTS (SELECT 1 FROM sys_audit_log WHERE target_ref = 'V3_SEED');

INSERT INTO adm_asset_category (code, name, depreciation_years)
SELECT 'LAPTOP', 'Laptop', 3
WHERE NOT EXISTS (SELECT 1 FROM adm_asset_category WHERE code = 'LAPTOP');

INSERT INTO adm_asset (asset_no, category_id, asset_name, owner_employee_id, storage_location, asset_status, purchase_date)
SELECT 'AST-2026-001', c.id, 'ThinkPad X1', e.id, 'HQ-IT-01', 'IN_USE', CURRENT_DATE
FROM adm_asset_category c
LEFT JOIN hr_employee e ON e.employee_no = 'E1001'
WHERE c.code = 'LAPTOP'
  AND NOT EXISTS (SELECT 1 FROM adm_asset WHERE asset_no = 'AST-2026-001');
