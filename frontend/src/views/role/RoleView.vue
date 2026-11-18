<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>角色模板</h2>
        <p>维护系统角色，作为账号和权限分配的基础。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadRoles">刷新角色</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="角色总数" :value="roles.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="关联账号数" :value="assignedUserCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="启用角色" :value="enabledRoleCount" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增角色</h3>
          <span>创建角色模板供账号授权使用</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="角色编码">
              <el-input v-model="form.code" placeholder="例如 ATTENDANCE_ADMIN" />
            </el-form-item>
            <el-form-item label="角色名称">
              <el-input v-model="form.name" placeholder="例如 考勤管理员" />
            </el-form-item>
            <el-form-item class="span-2" label="角色描述">
              <el-input
                v-model="form.description"
                :autosize="{ minRows: 4, maxRows: 6 }"
                placeholder="描述角色负责的业务边界"
                type="textarea"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitRole">创建角色</el-button>
        </el-form>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>角色列表</h3>
          <div class="toolbar-row">
            <el-input v-model="keyword" clearable placeholder="搜索编码、名称、说明" style="width: 260px" />
            <span class="text-muted">共 {{ filteredRoles.length }} 条</span>
          </div>
        </div>
        <el-table :data="filteredRoles" empty-text="暂无角色数据">
          <el-table-column label="角色编码" min-width="150">
            <template #default="{ row }">
              <span class="table-code">{{ row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column label="角色名称" min-width="140" prop="name" />
          <el-table-column label="说明" min-width="240" prop="description" />
          <el-table-column label="关联账号数" min-width="100" prop="userCount" />
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
import { createRole, fetchRoles, type Role } from '../../api/role';

const roles = ref<Role[]>([]);
const keyword = ref('');
const form = reactive({
  code: '',
  name: '',
  description: ''
});

const assignedUserCount = computed(() =>
  roles.value.reduce((count, item) => count + item.userCount, 0)
);

const enabledRoleCount = computed(() =>
  roles.value.filter((item) => item.enabled === 1).length
);

const filteredRoles = computed(() => {
  const normalized = keyword.value.trim().toLowerCase();
  return roles.value.filter((item) =>
    !normalized
    || item.code.toLowerCase().includes(normalized)
    || item.name.toLowerCase().includes(normalized)
    || (item.description ?? '').toLowerCase().includes(normalized)
  );
});

async function loadRoles() {
  roles.value = await fetchRoles();
}

async function submitRole() {
  if (!form.code || !form.name) {
    ElMessage.warning('请填写角色编码和角色名称');
    return;
  }

  await createRole(form);
  form.code = '';
  form.name = '';
  form.description = '';
  ElMessage.success('角色已创建');
  await loadRoles();
}

onMounted(loadRoles);
</script>
