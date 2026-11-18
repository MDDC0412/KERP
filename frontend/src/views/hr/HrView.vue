<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>组织人事</h2>
        <p>管理员工异动、入职计划和离职计划。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="异动记录" :value="changes.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="入职计划" :value="onboardingPlans.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="离职计划" :value="offboardingPlans.length" />
      </el-card>
    </div>

    <el-tabs>
      <el-tab-pane label="员工异动">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>记录异动</h3>
              <span>记录调岗、晋升、调薪等变更</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="员工">
                  <el-select v-model="changeForm.employeeId" placeholder="请选择员工">
                    <el-option
                      v-for="item in employees"
                      :key="item.id"
                      :label="item.fullName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="异动类型">
                  <el-select v-model="changeForm.changeType">
                    <el-option label="调岗" value="TRANSFER" />
                    <el-option label="晋升" value="PROMOTION" />
                    <el-option label="调薪" value="SALARY" />
                  </el-select>
                </el-form-item>
                <el-form-item label="变更前">
                  <el-input v-model="changeForm.beforeValue" />
                </el-form-item>
                <el-form-item label="变更后">
                  <el-input v-model="changeForm.afterValue" />
                </el-form-item>
                <el-form-item label="生效日期">
                  <el-date-picker
                    v-model="changeForm.effectiveDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="备注">
                  <el-input v-model="changeForm.remark" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitChange">保存异动</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>异动记录</h3>
              <span>员工关键履历变动</span>
            </div>
            <el-table :data="changes" empty-text="暂无异动记录">
              <el-table-column label="员工" min-width="120">
                <template #default="{ row }">
                  {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
                </template>
              </el-table-column>
              <el-table-column label="类型" min-width="120" prop="changeType" />
              <el-table-column label="变更前" min-width="140" prop="beforeValue" />
              <el-table-column label="变更后" min-width="140" prop="afterValue" />
              <el-table-column label="生效日期" min-width="120" prop="effectiveDate" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="入职计划">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增入职计划</h3>
              <span>跟踪入职交付清单</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="员工">
                  <el-select v-model="onboardingForm.employeeId" placeholder="请选择员工">
                    <el-option
                      v-for="item in employees"
                      :key="item.id"
                      :label="item.fullName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="计划日期">
                  <el-date-picker
                    v-model="onboardingForm.planDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="负责人">
                  <el-select v-model="onboardingForm.ownerUserId" clearable placeholder="可选">
                    <el-option
                      v-for="item in users"
                      :key="item.id"
                      :label="item.displayName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="onboardingForm.status">
                    <el-option label="待执行" value="PENDING" />
                    <el-option label="进行中" value="IN_PROGRESS" />
                    <el-option label="已完成" value="DONE" />
                  </el-select>
                </el-form-item>
                <el-form-item class="span-2" label="清单 JSON">
                  <el-input
                    v-model="onboardingForm.checklistJson"
                    :autosize="{ minRows: 4, maxRows: 6 }"
                    type="textarea"
                  />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitOnboarding">保存入职计划</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>入职计划列表</h3>
              <span>用于跟进账号、设备和培训交付</span>
            </div>
            <el-table :data="onboardingPlans" empty-text="暂无入职计划">
              <el-table-column label="员工" min-width="120">
                <template #default="{ row }">
                  {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
                </template>
              </el-table-column>
              <el-table-column label="计划日期" min-width="120" prop="planDate" />
              <el-table-column label="负责人" min-width="120">
                <template #default="{ row }">
                  {{ userNameMap[row.ownerUserId ?? -1] ?? '-' }}
                </template>
              </el-table-column>
              <el-table-column label="状态" min-width="120" prop="status" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="离职计划">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增离职计划</h3>
              <span>管理交接、回收与权限关闭</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="员工">
                  <el-select v-model="offboardingForm.employeeId" placeholder="请选择员工">
                    <el-option
                      v-for="item in employees"
                      :key="item.id"
                      :label="item.fullName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="计划日期">
                  <el-date-picker
                    v-model="offboardingForm.planDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="最后工作日">
                  <el-date-picker
                    v-model="offboardingForm.lastWorkingDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="offboardingForm.status">
                    <el-option label="待执行" value="PENDING" />
                    <el-option label="进行中" value="IN_PROGRESS" />
                    <el-option label="已完成" value="DONE" />
                  </el-select>
                </el-form-item>
                <el-form-item class="span-2" label="离职原因">
                  <el-input v-model="offboardingForm.reason" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitOffboarding">保存离职计划</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>离职计划列表</h3>
              <span>用于跟进离职交接和资产回收</span>
            </div>
            <el-table :data="offboardingPlans" empty-text="暂无离职计划">
              <el-table-column label="员工" min-width="120">
                <template #default="{ row }">
                  {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
                </template>
              </el-table-column>
              <el-table-column label="计划日期" min-width="120" prop="planDate" />
              <el-table-column label="最后工作日" min-width="120" prop="lastWorkingDate" />
              <el-table-column label="状态" min-width="120" prop="status" />
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
import { fetchEmployees, type Employee } from '../../api/employee';
import {
  createEmployeeChange,
  createOffboarding,
  createOnboarding,
  fetchEmployeeChanges,
  fetchOffboardingPlans,
  fetchOnboardingPlans,
  type EmployeeChange,
  type OffboardingPlan,
  type OnboardingPlan
} from '../../api/hr';
import { fetchUsers, type User } from '../../api/user';

const employees = ref<Employee[]>([]);
const users = ref<User[]>([]);
const changes = ref<EmployeeChange[]>([]);
const onboardingPlans = ref<OnboardingPlan[]>([]);
const offboardingPlans = ref<OffboardingPlan[]>([]);

const changeForm = reactive({
  employeeId: undefined as number | undefined,
  changeType: 'TRANSFER',
  beforeValue: '',
  afterValue: '',
  effectiveDate: '',
  remark: ''
});

const onboardingForm = reactive({
  employeeId: undefined as number | undefined,
  planDate: '',
  ownerUserId: undefined as number | undefined,
  checklistJson: '{"items":["账号开通","工位准备"]}',
  status: 'PENDING'
});

const offboardingForm = reactive({
  employeeId: undefined as number | undefined,
  planDate: '',
  lastWorkingDate: '',
  reason: '',
  status: 'PENDING'
});

const employeeNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(employees.value.map((item) => [item.id, item.fullName]))
);

const userNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(users.value.map((item) => [item.id, item.displayName]))
);

async function loadData() {
  const [employeeList, userList, changeList, onboardingList, offboardingList] = await Promise.all([
    fetchEmployees(),
    fetchUsers(),
    fetchEmployeeChanges(),
    fetchOnboardingPlans(),
    fetchOffboardingPlans()
  ]);
  employees.value = employeeList;
  users.value = userList;
  changes.value = changeList;
  onboardingPlans.value = onboardingList;
  offboardingPlans.value = offboardingList;
}

async function submitChange() {
  if (!changeForm.employeeId || !changeForm.effectiveDate) {
    ElMessage.warning('请选择员工并填写生效日期');
    return;
  }
  await createEmployeeChange({
    employeeId: changeForm.employeeId,
    changeType: changeForm.changeType,
    beforeValue: changeForm.beforeValue,
    afterValue: changeForm.afterValue,
    effectiveDate: changeForm.effectiveDate,
    remark: changeForm.remark
  });
  changeForm.employeeId = undefined;
  changeForm.changeType = 'TRANSFER';
  changeForm.beforeValue = '';
  changeForm.afterValue = '';
  changeForm.effectiveDate = '';
  changeForm.remark = '';
  ElMessage.success('异动记录已保存');
  await loadData();
}

async function submitOnboarding() {
  if (!onboardingForm.employeeId || !onboardingForm.planDate || !onboardingForm.checklistJson) {
    ElMessage.warning('请填写完整入职计划信息');
    return;
  }
  await createOnboarding({
    employeeId: onboardingForm.employeeId,
    planDate: onboardingForm.planDate,
    ownerUserId: onboardingForm.ownerUserId,
    checklistJson: onboardingForm.checklistJson,
    status: onboardingForm.status
  });
  onboardingForm.employeeId = undefined;
  onboardingForm.planDate = '';
  onboardingForm.ownerUserId = undefined;
  onboardingForm.checklistJson = '{"items":["账号开通","工位准备"]}';
  onboardingForm.status = 'PENDING';
  ElMessage.success('入职计划已保存');
  await loadData();
}

async function submitOffboarding() {
  if (!offboardingForm.employeeId || !offboardingForm.planDate || !offboardingForm.lastWorkingDate) {
    ElMessage.warning('请填写完整离职计划信息');
    return;
  }
  await createOffboarding({
    employeeId: offboardingForm.employeeId,
    planDate: offboardingForm.planDate,
    lastWorkingDate: offboardingForm.lastWorkingDate,
    reason: offboardingForm.reason || undefined,
    status: offboardingForm.status
  });
  offboardingForm.employeeId = undefined;
  offboardingForm.planDate = '';
  offboardingForm.lastWorkingDate = '';
  offboardingForm.reason = '';
  offboardingForm.status = 'PENDING';
  ElMessage.success('离职计划已保存');
  await loadData();
}

onMounted(loadData);
</script>
