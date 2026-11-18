<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>动态表单与流程</h2>
        <p>维护审批业务的表单定义与流程配置。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="表单定义" :value="forms.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="流程定义" :value="workflows.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="已启用流程" :value="enabledWorkflowCount" />
      </el-card>
    </div>

    <div class="panel-grid panel-grid--equal">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增表单</h3>
          <span>为审批类型配置字段结构</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="表单编码">
              <el-input v-model="formDefinition.code" placeholder="例如 TRAVEL_FORM" />
            </el-form-item>
            <el-form-item label="表单名称">
              <el-input v-model="formDefinition.name" placeholder="例如 出差申请表" />
            </el-form-item>
            <el-form-item label="分类">
              <el-input v-model="formDefinition.category" placeholder="例如 APPROVAL" />
            </el-form-item>
            <el-form-item label="版本">
              <el-input-number v-model="formDefinition.versionNo" :min="1" style="width: 100%" />
            </el-form-item>
            <el-form-item class="span-2" label="Schema JSON">
              <el-input
                v-model="formDefinition.schemaJson"
                :autosize="{ minRows: 5, maxRows: 8 }"
                type="textarea"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitFormDefinition">保存表单</el-button>
        </el-form>
      </el-card>

      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增流程</h3>
          <span>绑定表单并定义审批节点</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="流程编码">
              <el-input v-model="workflow.code" placeholder="例如 TRAVEL_PROCESS" />
            </el-form-item>
            <el-form-item label="流程名称">
              <el-input v-model="workflow.name" placeholder="例如 出差审批流程" />
            </el-form-item>
            <el-form-item label="绑定表单">
              <el-select v-model="workflow.formCode" placeholder="请选择表单">
                <el-option
                  v-for="item in forms"
                  :key="item.id"
                  :label="`${item.name} (${item.code})`"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="审批类型">
              <el-select v-model="workflow.approvalType">
                <el-option label="请假" value="LEAVE" />
                <el-option label="报销" value="EXPENSE" />
                <el-option label="出差" value="TRAVEL" />
                <el-option label="补卡" value="REISSUE" />
                <el-option label="加班" value="OVERTIME" />
              </el-select>
            </el-form-item>
            <el-form-item class="span-2" label="流程配置 JSON">
              <el-input
                v-model="workflow.configJson"
                :autosize="{ minRows: 5, maxRows: 8 }"
                type="textarea"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitWorkflow">保存流程</el-button>
        </el-form>
      </el-card>
    </div>

    <div class="panel-grid panel-grid--equal">
      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>表单定义列表</h3>
          <span>当前系统表单配置</span>
        </div>
        <el-table :data="forms" empty-text="暂无表单定义">
          <el-table-column label="编码" min-width="160">
            <template #default="{ row }">
              <span class="table-code">{{ row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column label="名称" min-width="160" prop="name" />
          <el-table-column label="分类" min-width="120" prop="category" />
          <el-table-column label="版本" min-width="90" prop="versionNo" />
        </el-table>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>流程定义列表</h3>
          <span>当前系统流程配置</span>
        </div>
        <el-table :data="workflows" empty-text="暂无流程定义">
          <el-table-column label="编码" min-width="160">
            <template #default="{ row }">
              <span class="table-code">{{ row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column label="名称" min-width="160" prop="name" />
          <el-table-column label="审批类型" min-width="120" prop="approvalType" />
          <el-table-column label="绑定表单" min-width="160" prop="formCode" />
          <el-table-column label="状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.enabled === 1 ? 'success' : 'info'" effect="plain">
                {{ row.enabled === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createForm,
  createWorkflow,
  fetchForms,
  fetchWorkflows,
  type FormDefinition,
  type ProcessDefinition
} from '../../api/workflow';

const forms = ref<FormDefinition[]>([]);
const workflows = ref<ProcessDefinition[]>([]);

const formDefinition = reactive({
  code: '',
  name: '',
  category: 'APPROVAL',
  versionNo: 1,
  schemaJson: '{"fields":["fieldA","fieldB"]}'
});

const workflow = reactive({
  code: '',
  name: '',
  formCode: '',
  approvalType: 'LEAVE',
  configJson: '{"nodes":["manager","hr"]}'
});

const enabledWorkflowCount = computed(() =>
  workflows.value.filter((item) => item.enabled === 1).length
);

async function loadData() {
  const [formList, workflowList] = await Promise.all([fetchForms(), fetchWorkflows()]);
  forms.value = formList;
  workflows.value = workflowList;
}

async function submitFormDefinition() {
  if (!formDefinition.code || !formDefinition.name || !formDefinition.schemaJson) {
    ElMessage.warning('请填写完整表单信息');
    return;
  }
  await createForm(formDefinition);
  formDefinition.code = '';
  formDefinition.name = '';
  formDefinition.category = 'APPROVAL';
  formDefinition.versionNo = 1;
  formDefinition.schemaJson = '{"fields":["fieldA","fieldB"]}';
  ElMessage.success('表单已保存');
  await loadData();
}

async function submitWorkflow() {
  if (!workflow.code || !workflow.name || !workflow.formCode || !workflow.configJson) {
    ElMessage.warning('请填写完整流程信息');
    return;
  }
  await createWorkflow(workflow);
  workflow.code = '';
  workflow.name = '';
  workflow.formCode = '';
  workflow.approvalType = 'LEAVE';
  workflow.configJson = '{"nodes":["manager","hr"]}';
  ElMessage.success('流程已保存');
  await loadData();
}

onMounted(loadData);
</script>
