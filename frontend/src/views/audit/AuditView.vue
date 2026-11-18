<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>审计日志</h2>
        <p>记录关键模块操作，用于问题追踪与合规留痕。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadAuditLogs">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="日志总数" :value="auditLogs.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="成功日志" :value="successCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="失败日志" :value="failedCount" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>写入日志</h3>
          <span>用于测试日志链路或补录关键动作</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="模块">
              <el-input v-model="form.moduleName" placeholder="例如 APPROVAL" />
            </el-form-item>
            <el-form-item label="动作">
              <el-input v-model="form.actionName" placeholder="例如 CREATE" />
            </el-form-item>
            <el-form-item label="操作人">
              <el-input v-model="form.operatorName" placeholder="例如 admin" />
            </el-form-item>
            <el-form-item label="目标引用">
              <el-input v-model="form.targetRef" placeholder="例如 APR-202604-002" />
            </el-form-item>
            <el-form-item label="结果">
              <el-select v-model="form.resultStatus">
                <el-option label="SUCCESS" value="SUCCESS" />
                <el-option label="FAILED" value="FAILED" />
              </el-select>
            </el-form-item>
            <el-form-item label="详情">
              <el-input v-model="form.detail" placeholder="例如 Created by local admin" />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitAuditLog">写入日志</el-button>
        </el-form>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>日志列表</h3>
          <span>记录系统关键操作</span>
        </div>
        <el-table :data="auditLogs" empty-text="暂无日志">
          <el-table-column label="模块" min-width="120" prop="moduleName" />
          <el-table-column label="动作" min-width="120" prop="actionName" />
          <el-table-column label="操作人" min-width="120" prop="operatorName" />
          <el-table-column label="目标" min-width="160" prop="targetRef" />
          <el-table-column label="结果" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.resultStatus === 'SUCCESS' ? 'success' : 'danger'" effect="plain">
                {{ row.resultStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="详情" min-width="220" prop="detail" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import { createAuditLog, fetchAuditLogs, type AuditLog } from '../../api/audit';

const auditLogs = ref<AuditLog[]>([]);

const form = reactive({
  moduleName: '',
  actionName: '',
  operatorName: 'admin',
  targetRef: '',
  resultStatus: 'SUCCESS',
  detail: ''
});

const successCount = computed(() =>
  auditLogs.value.filter((item) => item.resultStatus === 'SUCCESS').length
);

const failedCount = computed(() =>
  auditLogs.value.filter((item) => item.resultStatus === 'FAILED').length
);

async function loadAuditLogs() {
  auditLogs.value = await fetchAuditLogs();
}

async function submitAuditLog() {
  if (!form.moduleName || !form.actionName || !form.operatorName || !form.targetRef) {
    ElMessage.warning('请填写完整日志信息');
    return;
  }
  await createAuditLog(form);
  form.moduleName = '';
  form.actionName = '';
  form.operatorName = 'admin';
  form.targetRef = '';
  form.resultStatus = 'SUCCESS';
  form.detail = '';
  ElMessage.success('日志已写入');
  await loadAuditLogs();
}

onMounted(loadAuditLogs);
</script>
