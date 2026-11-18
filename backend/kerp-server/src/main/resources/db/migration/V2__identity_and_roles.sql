CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(255) NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_role_code UNIQUE (code),
    CONSTRAINT uk_sys_role_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(128) NULL,
    phone VARCHAR(32) NULL,
    department_id BIGINT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_sys_user_username UNIQUE (username),
    CONSTRAINT fk_sys_user_department FOREIGN KEY (department_id) REFERENCES sys_department(id)
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT uk_sys_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_sys_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_sys_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
);

INSERT INTO sys_role (code, name, description, enabled)
SELECT 'ADMIN', '系统管理员', '负责全局配置、账号与基础数据管理', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'ADMIN');

INSERT INTO sys_role (code, name, description, enabled)
SELECT 'HR_MANAGER', '人事经理', '负责员工档案、组织和人事配置', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'HR_MANAGER');

INSERT INTO sys_role (code, name, description, enabled)
SELECT 'DEPT_MANAGER', '部门负责人', '负责部门审批、人员查看与业务协调', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'DEPT_MANAGER');

INSERT INTO sys_user (username, display_name, password_hash, email, phone, department_id, enabled)
SELECT 'admin', '平台管理员', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6C9T7VDaRao7IhiHBpjz2uVH54cam', 'admin@kerp.local', '13800000000', NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.code = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );
