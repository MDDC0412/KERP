# 本地开发说明

## 1. 当前本地基线

- JDK：`17.0.14`
- Maven：`3.9.11`
- Node.js：`24.1.0`
- npm：`11.11.1`
- MySQL：`8.0.44`

## 2. 本地准备

### 2.1 MySQL

先创建数据库：

```sql
CREATE DATABASE kerp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

数据库连接配置文件：

- `backend/kerp-server/src/main/resources/application.yml`

数据库连接通过环境变量注入，仓库中不保存任何凭据：

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://127.0.0.1:3306/kerp?...` | 数据库连接串 |
| `DB_USERNAME` | `root` | 数据库用户名 |
| `DB_PASSWORD` | 空 | 数据库密码，**必须由使用方自行设置** |
| `KERP_STORAGE_ROOT` | `./data/uploads` | 本地附件存储目录 |

启动前设置环境变量（示例）：

```powershell
$env:DB_USERNAME='root'
$env:DB_PASSWORD='<你的本机密码>'
$env:KERP_STORAGE_ROOT='D:/KERP_DATA/uploads'
```

### 2.2 本地附件目录

创建本地目录（可通过 `KERP_STORAGE_ROOT` 覆盖）：

```powershell
New-Item -ItemType Directory -Force -Path 'D:\KERP_DATA\uploads'
```

当前只预留本地目录，不接入对象存储。

## 3. 启动后端

重点：必须让 Maven 使用 `JDK 17`，不要继续使用 `JDK 8`。

推荐启动方式：

```powershell
$env:JAVA_HOME='D:\Java JDK\java-jdk17\jdk-17.0.14'
$env:Path="D:\Maven\apache-maven-3.9.11\bin;$env:JAVA_HOME\bin;$env:Path"
Set-Location .\backend\kerp-server
mvn spring-boot:run
```

默认地址：

- 后端接口：`http://localhost:8080`
- 健康检查：`http://localhost:8080/actuator/health`

如果你只是临时验证，可临时覆盖端口：

```powershell
$env:SERVER_PORT='18080'
mvn spring-boot:run
```

## 4. Flyway 迁移

首次启动会自动执行：

- `V1__init.sql`
- `V2__identity_and_roles.sql`
- `V3__erp_operational_modules.sql`
- `V4__workflow_security_and_attendance.sql`
- `V5__fix_admin_seed_password.sql`
- `V6__procurement_and_inventory.sql`
- `V7__inventory_operations.sql`

### 4.1 当前种子数据

默认会生成：

- 部门：`HQ`
- 系统账号：`admin`
- 角色：`ADMIN`、`HR_MANAGER`、`DEPT_MANAGER`
- 权限、数据权限、表单、流程、审批、考勤、通知、审计、资产的基础演示数据

### 4.2 登录信息

本地开发默认管理员可使用：

- 用户名：`admin`
- 密码：`password`

如果你的数据库是更早版本初始化出来的，`V5` 会只在命中旧管理员种子哈希时自动修正默认密码，不会覆盖你后续自己改过的密码。

## 5. 启动前端

```powershell
Set-Location .\frontend
npm install
npm run dev
```

默认地址：

- 前端：`http://localhost:5173`

前端已配置 `/api` 代理到后端 `8080`。

### 5.1 当前前端界面基线

当前前端已经切换为产品化后台界面基线，而不是演示页样式：

- UI 组件：`Element Plus`
- 图表组件：`Apache ECharts` + `vue-echarts`
- 当前已完成页面：登录、工作台、组织单元、员工档案、系统账号、角色模板、权限中心、流程配置、审批中心、考勤管理、组织人事、采购与库存、通知中心、外部集成、审计日志、行政与资产
- 采购与库存当前已覆盖：供应商、采购订单、收货入库、库存出库、仓库调拨、库存盘点、库存台账
- 外部集成当前已覆盖：站内通知已启用，邮件、企业微信、飞书、钉钉、对象存储、AD/LDAP/SSO 已预留

注意：

- `frontend/src/api/` 下不要保留与 `.ts` 同名的旧 `.js` 文件，否则 Vite 会优先解析到旧文件并导致构建失败。

后续新增模块时，默认沿用这套后台组件体系继续扩展。

## 6. 当前开放接口

### 组织与人事

- `GET/POST /api/v1/departments`
- `GET/POST /api/v1/employees`
- `GET/POST /api/v1/hr/changes`
- `GET /api/v1/hr/onboarding`
- `GET /api/v1/hr/offboarding`

### 用户与权限

- `POST /api/v1/auth/login`
- `GET/POST /api/v1/roles`
- `GET/POST /api/v1/users`
- `GET/POST /api/v1/permissions`
- `GET/POST /api/v1/role-permissions`
- `GET/POST /api/v1/data-scopes`

### 流程与审批

- `GET/POST /api/v1/forms`
- `GET/POST /api/v1/workflows`
- `GET/POST /api/v1/approvals`
- `GET /api/v1/approvals/tasks/todo`
- `POST /api/v1/approvals/tasks/{taskId}/actions`

### 考勤

- `GET/POST /api/v1/attendance/shifts`
- `GET/POST /api/v1/attendance/groups`
- `GET/POST /api/v1/attendance/schedules`
- `GET/POST /api/v1/attendance/clock-records`
- `GET/POST /api/v1/attendance/monthly-summaries`
- `POST /api/v1/attendance/monthly-summaries/generate`

### 通知、审计、资产

- `GET/POST /api/v1/notifications`
- `POST /api/v1/notifications/mark-all-read`
- `GET /api/v1/integrations/reservations`
- `GET/POST /api/v1/audit-logs`
- `GET/POST /api/v1/assets/categories`
- `GET/POST /api/v1/assets`

### 采购与库存

- `GET/POST /api/v1/procurement/suppliers`
- `GET/POST /api/v1/procurement/orders`
- `GET/POST /api/v1/procurement/receipts`
- `GET /api/v1/procurement/stocks`
- `GET/POST /api/v1/procurement/outbounds`
- `GET/POST /api/v1/procurement/transfers`
- `GET/POST /api/v1/procurement/checks`

### 工作台

- `GET /api/v1/dashboard/overview`

## 7. 当前不需要准备的外部资源

目前不需要准备：

- 邮件服务
- 企业微信
- 飞书
- 钉钉
- 短信网关
- 域名
- HTTPS 证书

这些能力都只是预留，没有要求你现在准备。
