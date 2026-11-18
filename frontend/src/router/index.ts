import { createRouter, createWebHistory } from 'vue-router';
import AppLayout from '../layouts/AppLayout.vue';
import LoginView from '../views/auth/LoginView.vue';
import DashboardView from '../views/dashboard/DashboardView.vue';
import DepartmentView from '../views/organization/DepartmentView.vue';
import EmployeeView from '../views/employee/EmployeeView.vue';
import RoleView from '../views/role/RoleView.vue';
import UserView from '../views/user/UserView.vue';
import AuthorityView from '../views/authority/AuthorityView.vue';
import WorkflowView from '../views/workflow/WorkflowView.vue';
import ApprovalView from '../views/approval/ApprovalView.vue';
import AttendanceView from '../views/attendance/AttendanceView.vue';
import HrView from '../views/hr/HrView.vue';
import NotificationView from '../views/notification/NotificationView.vue';
import AuditView from '../views/audit/AuditView.vue';
import AssetView from '../views/asset/AssetView.vue';
import ProcurementView from '../views/procurement/ProcurementView.vue';
import IntegrationView from '../views/integration/IntegrationView.vue';
import { readSession } from '../utils/session';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: '系统登录',
        section: '身份认证'
      }
    },
    {
      path: '/',
      component: AppLayout,
      children: [
        {
          path: '',
          name: 'dashboard',
          component: DashboardView,
          meta: {
            title: '工作台',
            section: '运营概览'
          }
        },
        {
          path: 'departments',
          name: 'departments',
          component: DepartmentView,
          meta: {
            title: '组织单元',
            section: '组织管理'
          }
        },
        {
          path: 'employees',
          name: 'employees',
          component: EmployeeView,
          meta: {
            title: '员工档案',
            section: '组织人事'
          }
        },
        {
          path: 'hr',
          name: 'hr',
          component: HrView,
          meta: {
            title: '组织人事',
            section: '组织人事'
          }
        },
        {
          path: 'roles',
          name: 'roles',
          component: RoleView,
          meta: {
            title: '角色模板',
            section: '账号权限'
          }
        },
        {
          path: 'users',
          name: 'users',
          component: UserView,
          meta: {
            title: '系统账号',
            section: '账号权限'
          }
        },
        {
          path: 'authority',
          name: 'authority',
          component: AuthorityView,
          meta: {
            title: '权限与数据权限',
            section: '账号权限'
          }
        },
        {
          path: 'workflow',
          name: 'workflow',
          component: WorkflowView,
          meta: {
            title: '动态表单与流程',
            section: '流程配置'
          }
        },
        {
          path: 'approvals',
          name: 'approvals',
          component: ApprovalView,
          meta: {
            title: '审批中心',
            section: '业务审批'
          }
        },
        {
          path: 'attendance',
          name: 'attendance',
          component: AttendanceView,
          meta: {
            title: '考勤管理',
            section: '业务审批'
          }
        },
        {
          path: 'procurement',
          name: 'procurement',
          component: ProcurementView,
          meta: {
            title: '采购与库存',
            section: '供应链'
          }
        },
        {
          path: 'notifications',
          name: 'notifications',
          component: NotificationView,
          meta: {
            title: '通知中心',
            section: '系统运营'
          }
        },
        {
          path: 'integrations',
          name: 'integrations',
          component: IntegrationView,
          meta: {
            title: '外部集成',
            section: '系统运营'
          }
        },
        {
          path: 'audit',
          name: 'audit',
          component: AuditView,
          meta: {
            title: '审计日志',
            section: '系统运营'
          }
        },
        {
          path: 'assets',
          name: 'assets',
          component: AssetView,
          meta: {
            title: '行政与资产',
            section: '系统运营'
          }
        }
      ]
    }
  ]
});

router.beforeEach((to) => {
  const session = readSession();
  if (to.path !== '/login' && !session) {
    return '/login';
  }
  if (to.path === '/login' && session) {
    return '/';
  }

  document.title = `${String(to.meta.title ?? 'KERP')} - KERP`;
  return true;
});

export default router;
