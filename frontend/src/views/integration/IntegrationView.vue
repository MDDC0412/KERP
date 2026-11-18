<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>外部集成</h2>
        <p>统一查看飞书、企业微信、邮件、文件存储与身份同步的预留状态，不在当前阶段启用真实对接。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadOverview">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="info-card" shadow="never">
        <el-statistic title="集成项总数" :value="providerCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="已启用通道" :value="enabledCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="预留通道" :value="reservedCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="当前无需准备资源" :value="noPreparationCount" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="info-card" shadow="never">
        <div class="card-title">
          <h3>预留原则</h3>
          <span>保证本机可运行，不让第三方成为当前阻塞项</span>
        </div>
        <div class="integration-principles">
          <div
            v-for="(principle, index) in overview?.architecturePrinciples ?? []"
            :key="principle"
            class="integration-principle"
          >
            <span class="integration-principle__index">{{ index + 1 }}</span>
            <p>{{ principle }}</p>
          </div>
        </div>
      </el-card>

      <el-card class="info-card" shadow="never">
        <div class="card-title">
          <h3>当前无需准备</h3>
          <span>后续真正接入时，我再按模块给你最小准备清单</span>
        </div>
        <div class="list-chip-group">
          <span
            v-for="item in overview?.noPreparationNeeded ?? []"
            :key="item"
            class="list-chip list-chip--neutral"
          >
            {{ item }}
          </span>
        </div>
        <p class="panel-hint integration-hint">
          现在先专注把 ERP 主业务链路做完整，外部产品只保留标准化入口和扩展位。
        </p>
      </el-card>
    </div>

    <el-alert
      v-if="loadError"
      type="warning"
      :closable="false"
      show-icon
      :title="loadError"
      description="当前先展示本地预留模型，后端接口恢复后会自动使用实时数据。"
    />

    <el-card class="table-card" shadow="never">
      <div class="card-title">
        <h3>预留矩阵</h3>
        <span>这里展示“现在能做什么”和“以后接入需要什么”</span>
      </div>

      <el-table v-loading="loading" :data="overview?.providers ?? []" empty-text="暂无集成预留项">
        <el-table-column label="产品" min-width="160">
          <template #default="{ row }">
            <div class="integration-provider">
              <strong>{{ row.name }}</strong>
              <span>{{ row.category }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" effect="plain">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前说明" min-width="260" prop="summary" />
        <el-table-column label="已启用能力" min-width="240">
          <template #default="{ row }">
            <div class="integration-chip-list">
              <span
                v-for="item in row.enabledCapabilities"
                :key="`${row.code}-enabled-${item}`"
                class="list-chip"
              >
                {{ item }}
              </span>
              <span v-if="row.enabledCapabilities.length === 0" class="text-muted">当前未启用</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预留能力" min-width="260">
          <template #default="{ row }">
            <div class="integration-chip-list">
              <span
                v-for="item in row.reservedCapabilities"
                :key="`${row.code}-reserved-${item}`"
                class="list-chip list-chip--reserved"
              >
                {{ item }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="后续接入需要" min-width="240">
          <template #default="{ row }">
            <div class="integration-chip-list">
              <span
                v-for="item in row.futureRequirements"
                :key="`${row.code}-requirement-${item}`"
                class="list-chip list-chip--neutral"
              >
                {{ item }}
              </span>
              <span v-if="row.futureRequirements.length === 0" class="text-muted">无需额外准备</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import { fetchIntegrationOverview, type IntegrationOverview } from '../../api/integration';

const defaultOverview: IntegrationOverview = {
  architecturePrinciples: [
    '当前版本以本机可运行为第一优先，不依赖任何第三方平台账号。',
    '业务模块只产生日志、审批、通知、库存等领域事件，外部平台只作为通道或同步增强层。',
    '身份、组织、通知、文件能力都通过统一接口预留，后续接入时不重做现有业务模型。'
  ],
  noPreparationNeeded: [
    '邮件服务账号',
    '企业微信开放平台应用',
    '飞书开放平台应用',
    '钉钉开放平台应用',
    '短信网关账号',
    '域名与 HTTPS'
  ],
  providers: [
    {
      code: 'IN_APP',
      name: '站内通知',
      category: '通知',
      status: 'ENABLED',
      summary: '当前唯一启用的内置通知通道，用于审批、流程、资产和库存事件提醒。',
      enabledCapabilities: ['站内消息发送', '按用户/角色/部门投递', '通知中心查询'],
      reservedCapabilities: ['审批节点提醒增强', '库存预警联动', '人事异动广播'],
      futureRequirements: []
    },
    {
      code: 'SMTP',
      name: 'SMTP 邮件',
      category: '通知',
      status: 'RESERVED',
      summary: '保留为正式邮件通知通道，未来可承接审批提醒、库存预警和日报发送。',
      enabledCapabilities: [],
      reservedCapabilities: ['审批结果邮件', '库存低水位预警', '日报/周报推送'],
      futureRequirements: ['SMTP Host', 'SMTP Port', '发件邮箱', '授权码或应用密码']
    },
    {
      code: 'WECOM',
      name: '企业微信',
      category: '消息协同',
      status: 'RESERVED',
      summary: '保留消息通知和组织同步能力，后续可接入企业内部账号体系。',
      enabledCapabilities: [],
      reservedCapabilities: ['审批待办提醒', '到货通知', '部门与成员同步', '企业微信登录'],
      futureRequirements: ['企业微信应用', 'AgentId', 'Secret', '回调地址']
    },
    {
      code: 'FEISHU',
      name: '飞书',
      category: '消息协同',
      status: 'RESERVED',
      summary: '保留审批、采购、组织同步等扩展位，当前不发起真实调用。',
      enabledCapabilities: [],
      reservedCapabilities: ['审批提醒', '采购到货通知', '组织架构同步', '飞书登录'],
      futureRequirements: ['飞书应用', 'App ID', 'App Secret', 'Encrypt Key', '事件订阅回调地址']
    },
    {
      code: 'DINGTALK',
      name: '钉钉',
      category: '消息协同',
      status: 'RESERVED',
      summary: '保留为候选通知与组织同步通道，当前不纳入主流程依赖。',
      enabledCapabilities: [],
      reservedCapabilities: ['消息机器人通知', '审批同步提醒', '组织通讯录同步'],
      futureRequirements: ['钉钉应用', 'AppKey', 'AppSecret', '回调地址']
    },
    {
      code: 'MINIO',
      name: 'MinIO / 对象存储',
      category: '文件存储',
      status: 'RESERVED',
      summary: '附件能力先走本地目录，后续可切换到对象存储而不改变业务数据结构。',
      enabledCapabilities: ['本地上传目录预留'],
      reservedCapabilities: ['审批附件存储', '资产图片存储', '单据文件归档'],
      futureRequirements: ['访问地址', 'Bucket', 'Access Key', 'Secret Key']
    },
    {
      code: 'SSO',
      name: 'AD / LDAP / SSO',
      category: '身份认证',
      status: 'RESERVED',
      summary: '保留统一身份接入层，避免未来重做账号、角色、数据权限模型。',
      enabledCapabilities: ['本地账号登录'],
      reservedCapabilities: ['企业单点登录', '组织账号同步', '统一认证映射'],
      futureRequirements: ['目录服务地址', '租户信息', '客户端密钥', '回调地址']
    }
  ]
};

const loading = ref(false);
const loadError = ref('');
const overview = ref<IntegrationOverview>(defaultOverview);

const providerCount = computed(() => overview.value.providers.length);
const enabledCount = computed(() =>
  overview.value.providers.filter((item) => item.status === 'ENABLED').length
);
const reservedCount = computed(() =>
  overview.value.providers.filter((item) => item.status === 'RESERVED').length
);
const noPreparationCount = computed(() => overview.value.noPreparationNeeded.length);

function statusTagType(status: 'ENABLED' | 'RESERVED') {
  return status === 'ENABLED' ? 'success' : 'info';
}

function statusLabel(status: 'ENABLED' | 'RESERVED') {
  return status === 'ENABLED' ? '已启用' : '已预留';
}

async function loadOverview() {
  loading.value = true;
  loadError.value = '';
  try {
    const response = await fetchIntegrationOverview();
    overview.value = {
      architecturePrinciples: response.architecturePrinciples ?? defaultOverview.architecturePrinciples,
      noPreparationNeeded: response.noPreparationNeeded ?? defaultOverview.noPreparationNeeded,
      providers: response.providers?.length ? response.providers : defaultOverview.providers
    };
  } catch (error) {
    loadError.value = error instanceof Error ? error.message : '外部集成信息加载失败';
    ElMessage.warning('外部集成接口暂不可用，已切换到本地预留模型');
  } finally {
    loading.value = false;
  }
}

onMounted(loadOverview);
</script>

<style scoped>
.integration-principles {
  display: grid;
  gap: 12px;
}

.integration-principle {
  display: grid;
  grid-template-columns: 28px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
}

.integration-principle__index {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
}

.integration-principle p,
.integration-hint {
  margin: 0;
}

.integration-provider {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.integration-provider strong {
  color: #0f172a;
}

.integration-provider span {
  color: var(--kerp-text-muted);
  font-size: 12px;
}

.integration-chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.list-chip--reserved {
  background: #eef2ff;
  color: #4338ca;
}

.list-chip--neutral {
  background: #f1f5f9;
  color: #475569;
}
</style>
