<template>
  <div class="app-shell">
    <aside
      class="app-sidebar"
      :class="{
        'is-collapsed': !isMobile && collapsed,
        'is-mobile-open': isMobile && sidebarOpen
      }"
    >
      <div class="app-sidebar__brand">
        <div class="app-sidebar__logo">
          <el-icon><Grid /></el-icon>
        </div>
        <div v-show="isMobile || !collapsed" class="app-sidebar__brand-copy">
          <strong>KERP</strong>
          <span>企业运营后台</span>
        </div>
      </div>

      <div v-show="isMobile || !collapsed" class="app-sidebar__env">
        <span class="status-dot success"></span>
        <span>本机环境 / 8080 / MySQL</span>
      </div>

      <el-scrollbar class="app-sidebar__scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="!isMobile && collapsed"
          class="app-menu"
          router
          @select="handleMenuSelect"
        >
          <template v-for="group in navigation" :key="group.label">
            <div v-show="isMobile || !collapsed" class="app-menu__group">{{ group.label }}</div>
            <el-menu-item
              v-for="item in group.items"
              :key="item.path"
              :index="item.path"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>

      <div v-show="isMobile || !collapsed" class="app-sidebar__footer">
        <el-avatar :size="34" class="app-sidebar__avatar">
          {{ avatarText }}
        </el-avatar>
        <div class="app-sidebar__footer-copy">
          <strong>{{ session?.displayName ?? '未登录' }}</strong>
          <span>{{ session?.roles?.join(' / ') || '本地会话' }}</span>
        </div>
      </div>
    </aside>

    <div
      v-if="isMobile && sidebarOpen"
      class="app-sidebar__mask"
      @click="sidebarOpen = false"
    ></div>

    <div class="app-main">
      <header class="app-header">
        <div class="app-header__left">
          <el-button class="app-header__toggle" text @click="toggleSidebar">
            <el-icon size="18">
              <component :is="isMobile ? Menu : collapsed ? Expand : Fold" />
            </el-icon>
          </el-button>
          <div>
            <div class="app-header__section">{{ currentSection }}</div>
            <h1>{{ currentTitle }}</h1>
          </div>
        </div>

        <div class="app-header__right">
          <el-tag effect="plain" round>本地测试</el-tag>
          <el-badge :hidden="unreadNotifications === 0" :value="unreadNotifications" class="app-header__badge">
            <el-button circle @click="goToNotifications">
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>
          <div class="app-user">
            <el-avatar :size="34">{{ avatarText }}</el-avatar>
            <div class="app-user__copy">
              <strong>{{ session?.displayName ?? '未登录' }}</strong>
              <span>{{ session?.username ?? 'local' }}</span>
            </div>
          </div>
          <el-button type="primary" plain @click="logout">退出</el-button>
        </div>
      </header>

      <main class="app-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Avatar,
  Bell,
  Checked,
  Connection,
  Expand,
  Fold,
  Grid,
  Histogram,
  Menu,
  Monitor,
  OfficeBuilding,
  Operation,
  Postcard,
  ScaleToOriginal,
  SetUp,
  ShoppingCart,
  Tickets,
  User
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchNotifications } from '../api/notification';
import { clearSession, readSession } from '../utils/session';

const route = useRoute();
const router = useRouter();

const collapsed = ref(false);
const sidebarOpen = ref(false);
const isMobile = ref(window.innerWidth <= 1080);
const unreadNotifications = ref(0);

const navigation = [
  {
    label: '概览',
    items: [
      { path: '/', label: '工作台', icon: Monitor }
    ]
  },
  {
    label: '组织人事',
    items: [
      { path: '/departments', label: '组织单元', icon: OfficeBuilding },
      { path: '/employees', label: '员工档案', icon: Avatar },
      { path: '/hr', label: '组织人事', icon: Postcard }
    ]
  },
  {
    label: '账号权限',
    items: [
      { path: '/users', label: '系统账号', icon: User },
      { path: '/roles', label: '角色模板', icon: Checked },
      { path: '/authority', label: '权限中心', icon: ScaleToOriginal }
    ]
  },
  {
    label: '流程业务',
    items: [
      { path: '/workflow', label: '流程配置', icon: SetUp },
      { path: '/approvals', label: '审批中心', icon: Tickets },
      { path: '/attendance', label: '考勤管理', icon: Histogram }
    ]
  },
  {
    label: '供应链',
    items: [
      { path: '/procurement', label: '采购与库存', icon: ShoppingCart }
    ]
  },
  {
    label: '系统运营',
    items: [
      { path: '/notifications', label: '通知中心', icon: Bell },
      { path: '/integrations', label: '外部集成', icon: Connection },
      { path: '/audit', label: '审计日志', icon: Operation },
      { path: '/assets', label: '行政与资产', icon: Connection }
    ]
  }
] as const;

const session = computed(() => readSession());
const currentTitle = computed(() => String(route.meta.title ?? 'KERP'));
const currentSection = computed(() => String(route.meta.section ?? '企业资源计划'));
const activeMenu = computed(() => route.path);
const avatarText = computed(() => (session.value?.displayName ?? 'K').slice(0, 1).toUpperCase());

function syncViewport() {
  const nextIsMobile = window.innerWidth <= 1080;
  isMobile.value = nextIsMobile;
  if (!nextIsMobile) {
    sidebarOpen.value = false;
  }
}

function toggleSidebar() {
  if (isMobile.value) {
    sidebarOpen.value = !sidebarOpen.value;
    return;
  }
  collapsed.value = !collapsed.value;
}

function handleMenuSelect() {
  if (isMobile.value) {
    sidebarOpen.value = false;
  }
}

async function refreshUnreadNotifications() {
  try {
    const notifications = await fetchNotifications();
    unreadNotifications.value = notifications.filter((item) => item.readStatus === 0).length;
  } catch {
    unreadNotifications.value = 0;
  }
}

async function goToNotifications() {
  if (route.path === '/notifications') {
    ElMessage.info('当前已经在通知中心');
    return;
  }
  await router.push('/notifications');
}

async function logout() {
  clearSession();
  ElMessage.success('已退出登录');
  await router.push('/login');
}

onMounted(() => {
  window.addEventListener('resize', syncViewport);
  syncViewport();
  void refreshUnreadNotifications();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', syncViewport);
});

watch(
  () => route.path,
  () => {
    void refreshUnreadNotifications();
  },
  { immediate: true }
);
</script>
