CREATE TABLE IF NOT EXISTS bpm_approval_task (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    request_no VARCHAR(64) NOT NULL,
    node_name VARCHAR(64) NOT NULL,
    node_order INT NOT NULL,
    assignee_user_id BIGINT NOT NULL,
    action_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    action_comment VARCHAR(255) NULL,
    action_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bpm_approval_task_request FOREIGN KEY (request_id) REFERENCES bpm_approval_request(id),
    CONSTRAINT fk_bpm_approval_task_user FOREIGN KEY (assignee_user_id) REFERENCES sys_user(id)
);

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'WORKFLOW_MANAGE', 'Workflow Manage', 'WORKFLOW', 'ACTION'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'WORKFLOW_MANAGE');

INSERT INTO sys_permission (code, name, module_name, permission_type)
SELECT 'PERMISSION_MANAGE', 'Permission Manage', 'PERMISSION', 'ACTION'
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE code = 'PERMISSION_MANAGE');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.code IN ('WORKFLOW_MANAGE', 'PERMISSION_MANAGE')
WHERE r.code = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_permission rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
