<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>权限与数据权限</h2>
        <p>维护功能权限、角色授权以及数据范围规则。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="权限项" :value="permissions.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="角色授权关系" :value="rolePermissions.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="数据范围规则" :value="dataScopes.length" />
      </el-card>
    </div>

    <el-tabs>
      <el-tab-pane label="权限项">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增权限</h3>
              <span>定义菜单、动作或接口权限</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="权限编码">
                  <el-input v-model="permissionForm.code" placeholder="例如 APPROVAL_EXPORT" />
                </el-form-item>
                <el-form-item label="权限名称">
                  <el-input v-model="permissionForm.name" placeholder="例如 审批导出" />
                </el-form-item>
                <el-form-item label="所属模块">
                  <el-input v-model="permissionForm.moduleName" placeholder="例如 APPROVAL" />
                </el-form-item>
                <el-form-item label="权限类型">
                  <el-select v-model="permissionForm.permissionType">
                    <el-option label="菜单" value="MENU" />
                    <el-option label="动作" value="ACTION" />
                    <el-option label="接口" value="API" />
                  </el-select>
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitPermission">保存权限</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>权限列表</h3>
              <div class="toolbar-row">
                <el-input v-model="keyword" clearable placeholder="搜索编码、名称、模块" style="width: 260px" />
                <span class="text-muted">共 {{ filteredPermissions.length }} 条</span>
              </div>
            </div>
            <el-table :data="filteredPermissions" empty-text="暂无权限数据">
              <el-table-column label="编码" min-width="170">
                <template #default="{ row }">
                  <span class="table-code">{{ row.code }}</span>
                </template>
              </el-table-column>
              <el-table-column label="名称" min-width="140" prop="name" />
              <el-table-column label="模块" min-width="120" prop="moduleName" />
              <el-table-column label="类型" min-width="100" prop="permissionType" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="角色授权">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>角色权限分配</h3>
              <span>将权限项绑定到角色</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="角色">
                  <el-select v-model="rolePermissionForm.roleId" placeholder="请选择角色">
                    <el-option
                      v-for="item in roles"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="权限">
                  <el-select v-model="rolePermissionForm.permissionId" placeholder="请选择权限">
                    <el-option
                      v-for="item in permissions"
                      :key="item.id"
                      :label="`${item.name} (${item.code})`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitRolePermission">分配权限</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>角色授权关系</h3>
              <span>角色与权限的绑定明细</span>
            </div>
            <el-table :data="rolePermissions" empty-text="暂无角色授权关系">
              <el-table-column label="角色" min-width="140">
                <template #default="{ row }">
                  {{ roleNameMap[row.roleId] ?? row.roleId }}
                </template>
              </el-table-column>
              <el-table-column label="权限" min-width="180">
                <template #default="{ row }">
                  {{ permissionCodeMap[row.permissionId] ?? row.permissionId }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="数据范围">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>数据权限规则</h3>
              <span>按角色定义可见数据范围</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="角色">
                  <el-select v-model="dataScopeForm.roleId" placeholder="请选择角色">
                    <el-option
                      v-for="item in roles"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="范围编码">
                  <el-input v-model="dataScopeForm.scopeCode" placeholder="例如 DEPT_ONLY" />
                </el-form-item>
                <el-form-item label="范围名称">
                  <el-input v-model="dataScopeForm.scopeName" placeholder="例如 部门范围" />
                </el-form-item>
                <el-form-item label="规则">
                  <el-input v-model="dataScopeForm.scopeRule" placeholder="例如 department:self" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitDataScope">保存范围</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>数据范围列表</h3>
              <span>角色绑定的数据权限规则</span>
            </div>
            <el-table :data="dataScopes" empty-text="暂无数据范围规则">
              <el-table-column label="角色" min-width="140">
                <template #default="{ row }">
                  {{ roleNameMap[row.roleId] ?? row.roleId }}
                </template>
              </el-table-column>
              <el-table-column label="编码" min-width="140">
                <template #default="{ row }">
                  <span class="table-code">{{ row.scopeCode }}</span>
                </template>
              </el-table-column>
              <el-table-column label="名称" min-width="140" prop="scopeName" />
              <el-table-column label="规则" min-width="180" prop="scopeRule" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createDataScope,
  createPermission,
  createRolePermission,
  fetchDataScopes,
  fetchPermissions,
  fetchRolePermissions,
  type DataScope,
  type Permission,
  type RolePermission
} from '../../api/permission';
import { fetchRoles, type Role } from '../../api/role';

const keyword = ref('');
const permissions = ref<Permission[]>([]);
const rolePermissions = ref<RolePermission[]>([]);
const dataScopes = ref<DataScope[]>([]);
const roles = ref<Role[]>([]);

const permissionForm = reactive({
  code: '',
  name: '',
  moduleName: '',
  permissionType: 'ACTION'
});

const rolePermissionForm = reactive({
  roleId: undefined as number | undefined,
  permissionId: undefined as number | undefined
});

const dataScopeForm = reactive({
  roleId: undefined as number | undefined,
  scopeCode: '',
  scopeName: '',
  scopeRule: ''
});

const filteredPermissions = computed(() => {
  const normalized = keyword.value.trim().toLowerCase();
  return permissions.value.filter((item) =>
    !normalized
    || item.code.toLowerCase().includes(normalized)
    || item.name.toLowerCase().includes(normalized)
    || item.moduleName.toLowerCase().includes(normalized)
  );
});

const roleNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(roles.value.map((item) => [item.id, item.name]))
);

const permissionCodeMap = computed<Record<number, string>>(() =>
  Object.fromEntries(permissions.value.map((item) => [item.id, item.code]))
);

async function loadData() {
  const [permissionList, rolePermissionList, dataScopeList, roleList] = await Promise.all([
    fetchPermissions(),
    fetchRolePermissions(),
    fetchDataScopes(),
    fetchRoles()
  ]);
  permissions.value = permissionList;
  rolePermissions.value = rolePermissionList;
  dataScopes.value = dataScopeList;
  roles.value = roleList;
}

async function submitPermission() {
  if (!permissionForm.code || !permissionForm.name || !permissionForm.moduleName) {
    ElMessage.warning('请填写完整权限信息');
    return;
  }
  await createPermission(permissionForm);
  permissionForm.code = '';
  permissionForm.name = '';
  permissionForm.moduleName = '';
  permissionForm.permissionType = 'ACTION';
  ElMessage.success('权限已保存');
  await loadData();
}

async function submitRolePermission() {
  if (!rolePermissionForm.roleId || !rolePermissionForm.permissionId) {
    ElMessage.warning('请选择角色和权限');
    return;
  }
  await createRolePermission({
    roleId: rolePermissionForm.roleId,
    permissionId: rolePermissionForm.permissionId
  });
  rolePermissionForm.roleId = undefined;
  rolePermissionForm.permissionId = undefined;
  ElMessage.success('角色权限已分配');
  await loadData();
}

async function submitDataScope() {
  if (!dataScopeForm.roleId || !dataScopeForm.scopeCode || !dataScopeForm.scopeName || !dataScopeForm.scopeRule) {
    ElMessage.warning('请填写完整数据范围信息');
    return;
  }
  await createDataScope({
    roleId: dataScopeForm.roleId,
    scopeCode: dataScopeForm.scopeCode,
    scopeName: dataScopeForm.scopeName,
    scopeRule: dataScopeForm.scopeRule
  });
  dataScopeForm.roleId = undefined;
  dataScopeForm.scopeCode = '';
  dataScopeForm.scopeName = '';
  dataScopeForm.scopeRule = '';
  ElMessage.success('数据范围已保存');
  await loadData();
}

onMounted(loadData);
</script>
