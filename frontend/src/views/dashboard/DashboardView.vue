<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>工作台</h2>
        <p>按组织、人、权限和流程查看当前系统运行状态。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadOverview">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid">
      <el-card
        v-for="(item, index) in overview.metrics"
        :key="item.label"
        class="info-card"
        shadow="hover"
      >
        <div class="metric-item">
          <div class="metric-item__icon">
            <el-icon size="18">
              <component :is="metricIcons[index % metricIcons.length]" />
            </el-icon>
          </div>
          <div class="metric-item__content">
            <div>{{ item.label }}</div>
            <strong>{{ item.value }}</strong>
            <span>{{ item.hint }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <div class="panel-grid panel-grid--equal">
      <el-card class="chart-card" shadow="never">
        <div class="card-title">
          <h3>组织分布</h3>
          <span>按部门人数统计</span>
        </div>
        <v-chart autoresize class="chart-box" :option="departmentChartOption" />
      </el-card>

      <el-card class="chart-card" shadow="never">
        <div class="card-title">
          <h3>角色分布</h3>
          <span>按账号角色占比统计</span>
        </div>
        <v-chart autoresize class="chart-box" :option="roleChartOption" />
      </el-card>
    </div>

    <div class="panel-grid panel-grid--equal">
      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>快捷入口</h3>
          <span>高频操作模块</span>
        </div>
        <el-row :gutter="12">
          <el-col
            v-for="item in shortcuts"
            :key="item.path"
            :lg="12"
            :md="12"
            :sm="24"
            :xs="24"
          >
            <RouterLink :to="item.path">
              <el-card class="info-card" shadow="hover">
                <div class="metric-item">
                  <div class="metric-item__icon">
                    <el-icon size="18">
                      <component :is="item.icon" />
                    </el-icon>
                  </div>
                  <div class="metric-item__content">
                    <div>{{ item.label }}</div>
                    <span>{{ item.description }}</span>
                  </div>
                </div>
              </el-card>
            </RouterLink>
          </el-col>
        </el-row>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>最近入职员工</h3>
          <span>便于 HR 跟进档案和权限开通</span>
        </div>
        <el-table :data="overview.recentEmployees" empty-text="暂无员工记录">
          <el-table-column label="员工编号" min-width="130">
            <template #default="{ row }">
              <span class="table-code">{{ row.employeeNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="姓名" min-width="120" prop="fullName" />
          <el-table-column label="部门" min-width="140" prop="departmentName" />
          <el-table-column label="入职日期" min-width="120" prop="hireDate" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import {
  Avatar,
  Checked,
  Histogram,
  OfficeBuilding,
  Postcard,
  Tickets,
  User
} from '@element-plus/icons-vue';
import { computed, onMounted, reactive } from 'vue';
import { fetchDashboardOverview, type DashboardOverview } from '../../api/dashboard';

const overview = reactive<DashboardOverview>({
  metrics: [],
  departmentDistribution: [],
  roleDistribution: [],
  recentEmployees: []
});

const metricIcons = [OfficeBuilding, Avatar, User, Checked, Tickets, Histogram];

const shortcuts = [
  { path: '/employees', label: '员工档案', description: '维护员工主数据', icon: Avatar },
  { path: '/approvals', label: '审批中心', description: '处理待办与审批单', icon: Tickets },
  { path: '/attendance', label: '考勤管理', description: '班次、排班与月汇总', icon: Histogram },
  { path: '/workflow', label: '流程配置', description: '管理表单和流程定义', icon: Postcard }
];

const departmentChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 16, right: 16, top: 20, bottom: 8, containLabel: true },
  xAxis: {
    type: 'category',
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#dfe5ec' } },
    axisLabel: { color: '#64748b' },
    data: overview.departmentDistribution.map((item) => item.label)
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#eef2f7' } },
    axisLabel: { color: '#64748b' }
  },
  series: [
    {
      type: 'bar',
      barWidth: 18,
      itemStyle: {
        color: '#315efb',
        borderRadius: [6, 6, 0, 0]
      },
      data: overview.departmentDistribution.map((item) => item.value)
    }
  ]
}));

const roleChartOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: {
    bottom: 0,
    textStyle: { color: '#64748b' }
  },
  series: [
    {
      type: 'pie',
      radius: ['48%', '72%'],
      center: ['50%', '45%'],
      label: { formatter: '{b}' },
      data: overview.roleDistribution.map((item) => ({
        name: item.label,
        value: item.value
      })),
      color: ['#315efb', '#0f766e', '#d97706', '#7c3aed', '#dc2626']
    }
  ]
}));

async function loadOverview() {
  const data = await fetchDashboardOverview();
  overview.metrics = data.metrics;
  overview.departmentDistribution = data.departmentDistribution;
  overview.roleDistribution = data.roleDistribution;
  overview.recentEmployees = data.recentEmployees;
}

onMounted(loadOverview);
</script>
