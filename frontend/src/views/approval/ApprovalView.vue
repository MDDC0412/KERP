<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>审批中心</h2>
        <p>统一处理请假、报销、出差、补卡和加班单据。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="审批总数" :value="approvals.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="待我处理" :value="todoTasks.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="已通过" :value="approvedCount" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新建审批单</h3>
          <span>提交后进入已配置流程</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="单号">
              <el-input v-model="form.requestNo" placeholder="APR-202604-002" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="form.approvalType">
                <el-option
                  v-for="item in approvalTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="流程">
              <el-select v-model="form.processCode" placeholder="请选择流程">
                <el-option
                  v-for="item in workflows"
                  :key="item.id"
                  :label="`${item.name} (${item.code})`"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="申请人">
              <el-select v-model="form.employeeId" placeholder="请选择员工">
                <el-option
                  v-for="item in employees"
                  :key="item.id"
                  :label="item.fullName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item class="span-2" label="标题">
              <el-input v-model="form.title" placeholder="请输入审批标题" />
            </el-form-item>
            <el-form-item class="span-2" label="表单数据 JSON">
              <el-input
                v-model="form.formDataJson"
                :autosize="{ minRows: 5, maxRows: 8 }"
                type="textarea"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitApproval">提交审批</el-button>
        </el-form>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>待办任务</h3>
          <span>当前账号需要处理的审批节点</span>
        </div>
        <el-table :data="todoTasks" empty-text="当前没有待办审批">
          <el-table-column label="单号" min-width="140">
            <template #default="{ row }">
              <span class="table-code">{{ row.requestNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="类型" min-width="90">
            <template #default="{ row }">
              {{ approvalTypeText[row.approvalType] ?? row.approvalType }}
            </template>
          </el-table-column>
          <el-table-column label="节点" min-width="120" prop="nodeName" />
          <el-table-column label="标题" min-width="200" prop="title" />
          <el-table-column label="提交时间" min-width="160" prop="submittedAt" />
          <el-table-column align="right" fixed="right" label="操作" min-width="160">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button link type="primary" @click="processTask(row.id, 'APPROVE')">通过</el-button>
                <el-button link type="danger" @click="processTask(row.id, 'REJECT')">拒绝</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <el-card class="table-card" shadow="never">
      <div class="card-title">
        <h3>审批列表</h3>
        <div class="toolbar-row">
          <el-input v-model="keyword" clearable placeholder="搜索单号或标题" style="width: 220px" />
          <el-select v-model="typeFilter" clearable placeholder="审批类型" style="width: 160px">
            <el-option
              v-for="item in approvalTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <span class="text-muted">共 {{ filteredApprovals.length }} 条</span>
        </div>
      </div>
      <el-table :data="filteredApprovals" empty-text="暂无审批数据">
        <el-table-column label="单号" min-width="140">
          <template #default="{ row }">
            <span class="table-code">{{ row.requestNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" min-width="90">
          <template #default="{ row }">
            {{ approvalTypeText[row.approvalType] ?? row.approvalType }}
          </template>
        </el-table-column>
        <el-table-column label="申请人" min-width="120">
          <template #default="{ row }">
            {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
          </template>
        </el-table-column>
        <el-table-column label="标题" min-width="220" prop="title" />
        <el-table-column label="流程" min-width="140" prop="processCode" />
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="approvalStatusType(row.status)" effect="plain">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createApproval,
  fetchApprovals,
  fetchTodoApprovalTasks,
  handleApprovalTask,
  type ApprovalRequest,
  type ApprovalTask
} from '../../api/approval';
import { fetchEmployees, type Employee } from '../../api/employee';
import { fetchWorkflows, type ProcessDefinition } from '../../api/workflow';

const approvalTypeOptions = [
  { label: '请假', value: 'LEAVE' },
  { label: '报销', value: 'EXPENSE' },
  { label: '出差', value: 'TRAVEL' },
  { label: '补卡', value: 'REISSUE' },
  { label: '加班', value: 'OVERTIME' }
];

const approvalTypeText = Object.fromEntries(
  approvalTypeOptions.map((item) => [item.value, item.label])
) as Record<string, string>;

const keyword = ref('');
const typeFilter = ref('');
const approvals = ref<ApprovalRequest[]>([]);
const todoTasks = ref<ApprovalTask[]>([]);
const employees = ref<Employee[]>([]);
const workflows = ref<ProcessDefinition[]>([]);

const form = reactive({
  requestNo: '',
  approvalType: 'LEAVE',
  processCode: '',
  employeeId: undefined as number | undefined,
  title: '',
  formDataJson: '{"days":1,"reason":"business"}'
});

const employeeNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(employees.value.map((item) => [item.id, item.fullName]))
);

const filteredApprovals = computed(() => {
  const normalized = keyword.value.trim().toLowerCase();
  return approvals.value.filter((item) => {
    const matchesKeyword = !normalized
      || item.requestNo.toLowerCase().includes(normalized)
      || item.title.toLowerCase().includes(normalized);
    const matchesType = !typeFilter.value || item.approvalType === typeFilter.value;
    return matchesKeyword && matchesType;
  });
});

const approvedCount = computed(() =>
  approvals.value.filter((item) => item.status === 'APPROVED').length
);

function approvalStatusType(status: string) {
  if (status === 'APPROVED') return 'success';
  if (status === 'REJECTED') return 'danger';
  if (status === 'IN_PROGRESS') return 'warning';
  return 'info';
}

async function loadData() {
  const [approvalList, taskList, employeeList, workflowList] = await Promise.all([
    fetchApprovals(),
    fetchTodoApprovalTasks().catch(() => []),
    fetchEmployees(),
    fetchWorkflows()
  ]);

  approvals.value = approvalList;
  todoTasks.value = taskList;
  employees.value = employeeList;
  workflows.value = workflowList;
}

async function submitApproval() {
  if (!form.requestNo || !form.processCode || !form.employeeId || !form.title || !form.formDataJson) {
    ElMessage.warning('请填写完整审批信息');
    return;
  }

  await createApproval({
    requestNo: form.requestNo,
    approvalType: form.approvalType,
    processCode: form.processCode,
    employeeId: form.employeeId,
    title: form.title,
    formDataJson: form.formDataJson
  });

  form.requestNo = '';
  form.approvalType = 'LEAVE';
  form.processCode = '';
  form.employeeId = undefined;
  form.title = '';
  form.formDataJson = '{"days":1,"reason":"business"}';
  ElMessage.success('审批单已提交');
  await loadData();
}

async function processTask(taskId: number, action: 'APPROVE' | 'REJECT') {
  try {
    const result = await ElMessageBox.prompt(
      action === 'APPROVE' ? '请输入审批备注，可留空。' : '请输入拒绝原因，可留空。',
      action === 'APPROVE' ? '审批通过' : '审批拒绝',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputPlaceholder: '输入备注'
      }
    );

    await handleApprovalTask(taskId, {
      action,
      comment: result.value || undefined
    });
    ElMessage.success(action === 'APPROVE' ? '审批已通过' : '审批已拒绝');
    await loadData();
  } catch {
    // ignore cancel
  }
}

onMounted(loadData);
</script>
