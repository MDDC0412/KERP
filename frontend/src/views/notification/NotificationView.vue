<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>通知中心</h2>
        <p>当前先支持站内通知，并为邮件、微信、飞书预留扩展位。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadNotifications">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="通知总数" :value="notifications.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="未读通知" :value="unreadCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="当前通道" :value="1" suffix=" 个" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>发送站内通知</h3>
          <span>支持按用户、角色、部门进行投递</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="例如 审批已通过" />
            </el-form-item>
            <el-form-item label="目标类型">
              <el-select v-model="form.targetType">
                <el-option label="用户" value="USER" />
                <el-option label="角色" value="ROLE" />
                <el-option label="部门" value="DEPARTMENT" />
              </el-select>
            </el-form-item>
            <el-form-item class="span-2" label="目标值">
              <el-input v-model="form.targetValue" placeholder="例如 admin / ADMIN / HQ" />
            </el-form-item>
            <el-form-item class="span-2" label="内容">
              <el-input
                v-model="form.content"
                :autosize="{ minRows: 5, maxRows: 8 }"
                type="textarea"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitNotification">发送通知</el-button>
        </el-form>
      </el-card>

      <el-card class="info-card" shadow="never">
        <div class="card-title">
          <h3>预留通道</h3>
          <span>后续可按你的资源准备情况启用</span>
        </div>
        <div class="list-chip-group">
          <span class="list-chip">站内通知：已启用</span>
          <span class="list-chip">邮件：已预留接口位</span>
          <span class="list-chip">微信：已预留接口位</span>
          <span class="list-chip">飞书：已预留接口位</span>
        </div>
        <p class="panel-hint">
          当前先不要求你准备第三方账号，等功能链路稳定后再逐项接入。
        </p>
      </el-card>
    </div>

    <el-card class="table-card" shadow="never">
      <div class="card-title">
        <h3>通知列表</h3>
        <span>当前仅展示站内通知</span>
      </div>
      <el-table :data="notifications" empty-text="暂无通知">
        <el-table-column label="标题" min-width="180" prop="title" />
        <el-table-column label="目标类型" min-width="110" prop="targetType" />
        <el-table-column label="目标值" min-width="140" prop="targetValue" />
        <el-table-column label="通道" min-width="100" prop="channel" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.readStatus === 1 ? 'success' : 'warning'" effect="plain">
              {{ row.readStatus === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createNotification,
  fetchNotifications,
  markAllNotificationsRead,
  type NotificationMessage
} from '../../api/notification';

const notifications = ref<NotificationMessage[]>([]);

const form = reactive({
  title: '',
  content: '',
  targetType: 'USER',
  targetValue: 'admin'
});

const unreadCount = computed(() =>
  notifications.value.filter((item) => item.readStatus === 0).length
);

async function loadNotifications() {
  notifications.value = await fetchNotifications();
}

async function openNotifications() {
  await markAllNotificationsRead();
  await loadNotifications();
}

async function submitNotification() {
  if (!form.title || !form.content || !form.targetValue) {
    ElMessage.warning('请填写完整通知信息');
    return;
  }
  await createNotification(form);
  form.title = '';
  form.content = '';
  form.targetType = 'USER';
  form.targetValue = 'admin';
  ElMessage.success('通知已发送');
  await loadNotifications();
}

onMounted(openNotifications);
</script>
