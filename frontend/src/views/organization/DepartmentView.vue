<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>组织单元</h2>
        <p>管理企业部门结构、层级和排序。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadDepartments">刷新列表</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="部门总数" :value="departments.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="一级部门" :value="rootDepartmentCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="下级部门" :value="departments.length - rootDepartmentCount" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增部门</h3>
          <span>创建组织层级节点</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="部门编码">
              <el-input v-model="form.code" placeholder="例如 HR 或 FIN" />
            </el-form-item>
            <el-form-item label="部门名称">
              <el-input v-model="form.name" placeholder="例如 人力资源部" />
            </el-form-item>
            <el-form-item label="上级部门">
              <el-select v-model="form.parentId" clearable placeholder="无上级部门">
                <el-option
                  v-for="item in departments"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="排序值">
              <el-input-number v-model="form.sort" :min="1" style="width: 100%" />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitDepartment">创建部门</el-button>
        </el-form>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>部门列表</h3>
          <div class="toolbar-row">
            <el-input v-model="keyword" clearable placeholder="搜索编码或名称" style="width: 220px" />
            <el-select v-model="structureFilter" style="width: 160px">
              <el-option label="全部层级" value="all" />
              <el-option label="一级部门" value="root" />
              <el-option label="下级部门" value="child" />
            </el-select>
            <span class="text-muted">共 {{ filteredDepartments.length }} 条</span>
          </div>
        </div>
        <el-table :data="filteredDepartments" empty-text="暂无部门数据">
          <el-table-column label="ID" min-width="70" prop="id" />
          <el-table-column label="编码" min-width="120">
            <template #default="{ row }">
              <span class="table-code">{{ row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column label="名称" min-width="160" prop="name" />
          <el-table-column label="上级部门" min-width="150">
            <template #default="{ row }">
              {{ parentNameMap[row.parentId ?? -1] ?? '-' }}
            </template>
          </el-table-column>
          <el-table-column label="层级" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.parentId == null ? 'primary' : 'info'" effect="plain">
                {{ row.parentId == null ? '一级' : '下级' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="排序" min-width="90" prop="sort" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import { createDepartment, fetchDepartments, type Department } from '../../api/organization';

const departments = ref<Department[]>([]);
const keyword = ref('');
const structureFilter = ref<'all' | 'root' | 'child'>('all');
const form = reactive({
  code: '',
  name: '',
  parentId: undefined as number | undefined,
  sort: 100
});

const parentNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(departments.value.map((item) => [item.id, item.name]))
);

const rootDepartmentCount = computed(() =>
  departments.value.filter((item) => item.parentId == null).length
);

const filteredDepartments = computed(() => {
  const normalized = keyword.value.trim().toLowerCase();
  return departments.value.filter((item) => {
    const matchesKeyword = !normalized
      || item.code.toLowerCase().includes(normalized)
      || item.name.toLowerCase().includes(normalized);

    const matchesStructure = structureFilter.value === 'all'
      || (structureFilter.value === 'root' && item.parentId == null)
      || (structureFilter.value === 'child' && item.parentId != null);

    return matchesKeyword && matchesStructure;
  });
});

async function loadDepartments() {
  departments.value = await fetchDepartments();
}

async function submitDepartment() {
  if (!form.code || !form.name) {
    ElMessage.warning('请填写部门编码和名称');
    return;
  }

  await createDepartment({
    code: form.code,
    name: form.name,
    parentId: form.parentId,
    sort: form.sort
  });

  form.code = '';
  form.name = '';
  form.parentId = undefined;
  form.sort = 100;
  ElMessage.success('部门已创建');
  await loadDepartments();
}

onMounted(loadDepartments);
</script>
