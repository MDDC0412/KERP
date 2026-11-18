<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>系统账号</h2>
        <p>配置登录账号、角色和基础联系方式。</p>
      </div>
      <div class="page-actions">
        <el-button @click="reload">刷新账号</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="账号总数" :value="users.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="启用账号" :value="enabledUserCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="管理员账号" :value="hasAdmin ? 1 : 0" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增系统账号</h3>
          <span>创建登录账号并分配角色</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="登录名">
              <el-input v-model="form.username" placeholder="例如 zhangsan" />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="form.displayName" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="所属部门">
              <el-select v-model="form.departmentId" clearable placeholder="可不分配">
                <el-option
                  v-for="item in departments"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="初始密码">
              <el-input
                v-model="form.initialPassword"
                placeholder="留空则使用默认规则"
                show-password
              />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="例如 ops@company.com" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="例如 13800000000" />
            </el-form-item>
            <el-form-item class="span-2" label="角色分配">
              <el-checkbox-group v-model="selectedRoleIds">
                <el-checkbox
                  v-for="item in roles"
                  :key="item.id"
                  :label="item.id"
                >
                  {{ item.name }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitUser">创建账号</el-button>
        </el-form>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>账号列表</h3>
          <div class="toolbar-row">
            <el-input
              v-model="keyword"
              clearable
              placeholder="搜索登录名、姓名、邮箱、手机号"
              style="width: 280px"
            />
            <el-select v-model="roleFilter" clearable placeholder="全部角色" style="width: 160px">
              <el-option
                v-for="item in roles"
                :key="item.id"
                :label="item.name"
                :value="item.name"
              />
            </el-select>
            <span class="text-muted">共 {{ filteredUsers.length }} 条</span>
          </div>
        </div>
        <el-table :data="filteredUsers" empty-text="暂无账号数据">
          <el-table-column label="登录名" min-width="120">
            <template #default="{ row }">
              <span class="table-code">{{ row.username }}</span>
            </template>
          </el-table-column>
          <el-table-column label="姓名" min-width="120" prop="displayName" />
          <el-table-column label="部门" min-width="140" prop="departmentName" />
          <el-table-column label="邮箱" min-width="200" prop="email" />
          <el-table-column label="手机号" min-width="140" prop="phone" />
          <el-table-column label="角色" min-width="220">
            <template #default="{ row }">
              <div class="list-chip-group">
                <span v-for="role in row.roles" :key="role" class="list-chip">{{ role }}</span>
              </div>
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
import { fetchDepartments, type Department } from '../../api/organization';
import { fetchRoles, type Role } from '../../api/role';
import { createUser, fetchUsers, type User } from '../../api/user';

const users = ref<User[]>([]);
const roles = ref<Role[]>([]);
const departments = ref<Department[]>([]);
const selectedRoleIds = ref<number[]>([]);
const keyword = ref('');
const roleFilter = ref('');

const form = reactive({
  username: '',
  displayName: '',
  email: '',
  phone: '',
  departmentId: undefined as number | undefined,
  initialPassword: ''
});

const hasAdmin = computed(() => users.value.some((item) => item.username === 'admin'));
const enabledUserCount = computed(() => users.value.filter((item) => item.enabled === 1).length);

const filteredUsers = computed(() => {
  const normalized = keyword.value.trim().toLowerCase();
  return users.value.filter((item) => {
    const matchesKeyword = !normalized
      || item.username.toLowerCase().includes(normalized)
      || item.displayName.toLowerCase().includes(normalized)
      || (item.email ?? '').toLowerCase().includes(normalized)
      || (item.phone ?? '').toLowerCase().includes(normalized);

    const matchesRole = !roleFilter.value || item.roles.includes(roleFilter.value);
    return matchesKeyword && matchesRole;
  });
});

async function loadUsers() {
  users.value = await fetchUsers();
}

async function loadMeta() {
  const [departmentList, roleList] = await Promise.all([fetchDepartments(), fetchRoles()]);
  departments.value = departmentList;
  roles.value = roleList;
}

async function reload() {
  await Promise.all([loadUsers(), loadMeta()]);
}

async function submitUser() {
  if (!form.username || !form.displayName || !selectedRoleIds.value.length) {
    ElMessage.warning('请填写登录名、姓名并至少选择一个角色');
    return;
  }

  await createUser({
    username: form.username,
    displayName: form.displayName,
    email: form.email || undefined,
    phone: form.phone || undefined,
    departmentId: form.departmentId,
    initialPassword: form.initialPassword || undefined,
    roleIds: selectedRoleIds.value
  });

  form.username = '';
  form.displayName = '';
  form.email = '';
  form.phone = '';
  form.departmentId = undefined;
  form.initialPassword = '';
  selectedRoleIds.value = [];
  ElMessage.success('系统账号已创建');
  await loadUsers();
}

onMounted(reload);
</script>
