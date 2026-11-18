CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    parent_id BIGINT NULL,
    sort INT NOT NULL DEFAULT 100,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_department_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS hr_employee (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_no VARCHAR(64) NOT NULL,
    full_name VARCHAR(64) NOT NULL,
    department_id BIGINT NOT NULL,
    phone VARCHAR(32) NULL,
    email VARCHAR(128) NULL,
    hire_date DATE NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_hr_employee_no UNIQUE (employee_no),
    CONSTRAINT fk_hr_employee_department FOREIGN KEY (department_id) REFERENCES sys_department(id)
);

INSERT INTO sys_department (code, name, parent_id, sort, enabled)
SELECT 'HQ', '总部', NULL, 1, 1
WHERE NOT EXISTS (
    SELECT 1 FROM sys_department WHERE code = 'HQ'
);

