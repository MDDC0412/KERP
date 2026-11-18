<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>员工档案</h2>
        <p>维护员工主数据，用于审批、考勤和账号关联。</p>
      </div>
      <div class="page-actions">
        <el-button @click="reload">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="员工总数" :value="employees.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="已分配部门" :value="assignedDepartmentCount" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="已填邮箱" :value="employeesWithEmail" />
      </el-card>
    </div>

    <div class="panel-grid">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增员工</h3>
          <span>建立人员基础档案</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="员工编号">
              <el-input v-model="form.employeeNo" placeholder="例如 E0001" />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="form.fullName" placeholder="请输入员工姓名" />
            </el-form-item>
            <el-form-item label="所属部门">
              <el-select v-model="form.departmentId" placeholder="请选择部门">
                <el-option
                  v-for="item in departments"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="例如 13800000000" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="例如 hr@company.com" />
            </el-form-item>
            <el-form-item label="入职日期">
              <el-date-picker
                v-model="form.hireDate"
                placeholder="选择日期"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitEmployee">创建员工档案</el-button>
        </el-form>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>员工列表</h3>
          <div class="toolbar-row">
            <el-input
              v-model="keyword"
              clearable
              placeholder="搜索编号、姓名、手机号、邮箱"
              style="width: 260px"
            />
            <el-select v-model="departmentFilter" clearable placeholder="全部部门" style="width: 180px">
              <el-option
                v-for="item in departments"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <span class="text-muted">共 {{ filteredEmployees.length }} 条</span>
          </div>
        </div>
        <el-table :data="filteredEmployees" empty-text="暂无员工数据">
          <el-table-column label="员工编号" min-width="130">
            <template #default="{ row }">
              <span class="table-code">{{ row.employeeNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="姓名" min-width="120" prop="fullName" />
          <el-table-column label="所属部门" min-width="150">
            <template #default="{ row }">
              {{ departmentNameMap[row.departmentId] ?? `部门#${row.departmentId}` }}
            </template>
          </el-table-column>
          <el-table-column label="手机号" min-width="140" prop="phone" />
          <el-table-column label="邮箱" min-width="200" prop="email" />
          <el-table-column label="入职日期" min-width="120" prop="hireDate" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import { createEmployee, fetchEmployees, type Employee } from '../../api/employee';
import { fetchDepartments, type Department } from '../../api/organization';

const employees = ref<Employee[]>([]);
const departments = ref<Department[]>([]);
const keyword = ref('');
const departmentFilter = ref<number | undefined>(undefined);
const form = reactive({
  employeeNo: '',
  fullName: '',
  departmentId: undefined as number | undefined,
  phone: '',
  email: '',
  hireDate: ''
});

const departmentNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(departments.value.map((item) => [item.id, item.name]))
);

const assignedDepartmentCount = computed(() =>
  employees.value.filter((item) => item.departmentId != null).length
);

const employeesWithEmail = computed(() =>
  employees.value.filter((item) => item.email).length
);

const filteredEmployees = computed(() => {
  const normalized = keyword.value.trim().toLowerCase();
  return employees.value.filter((item) => {
    const matchesKeyword = !normalized
      || item.employeeNo.toLowerCase().includes(normalized)
      || item.fullName.toLowerCase().includes(normalized)
      || (item.phone ?? '').toLowerCase().includes(normalized)
      || (item.email ?? '').toLowerCase().includes(normalized);

    const matchesDepartment = !departmentFilter.value || item.departmentId === departmentFilter.value;
    return matchesKeyword && matchesDepartment;
  });
});

async function loadEmployees() {
  employees.value = await fetchEmployees();
}

async function loadDepartments() {
  departments.value = await fetchDepartments();
}

async function reload() {
  await Promise.all([loadDepartments(), loadEmployees()]);
}

async function submitEmployee() {
  if (!form.employeeNo || !form.fullName || !form.departmentId) {
    ElMessage.warning('请填写员工编号、姓名和所属部门');
    return;
  }

  await createEmployee({
    employeeNo: form.employeeNo,
    fullName: form.fullName,
    departmentId: form.departmentId,
    phone: form.phone || undefined,
    email: form.email || undefined,
    hireDate: form.hireDate || undefined
  });

  form.employeeNo = '';
  form.fullName = '';
  form.departmentId = undefined;
  form.phone = '';
  form.email = '';
  form.hireDate = '';
  ElMessage.success('员工档案已创建');
  await loadEmployees();
}

onMounted(reload);
</script>
