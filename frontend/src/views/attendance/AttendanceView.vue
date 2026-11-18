<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>考勤管理</h2>
        <p>统一管理班次、考勤组、排班、打卡和月汇总。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="info-card" shadow="never">
        <el-statistic title="班次数" :value="shifts.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="考勤组" :value="groups.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="排班数" :value="schedules.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="异常打卡" :value="abnormalClockCount" />
      </el-card>
    </div>

    <el-tabs>
      <el-tab-pane label="基础设置">
        <div class="panel-grid panel-grid--equal">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增班次</h3>
              <span>用于排班和考勤组配置</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="班次编码">
                  <el-input v-model="shiftForm.code" placeholder="例如 DAY" />
                </el-form-item>
                <el-form-item label="班次名称">
                  <el-input v-model="shiftForm.name" placeholder="例如 白班" />
                </el-form-item>
                <el-form-item label="开始时间">
                  <el-time-picker
                    v-model="shiftTime.start"
                    format="HH:mm"
                    placeholder="选择时间"
                    value-format="HH:mm"
                  />
                </el-form-item>
                <el-form-item label="结束时间">
                  <el-time-picker
                    v-model="shiftTime.end"
                    format="HH:mm"
                    placeholder="选择时间"
                    value-format="HH:mm"
                  />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitShift">保存班次</el-button>
            </el-form>
          </el-card>

          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增考勤组</h3>
              <span>绑定默认班次和负责人</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="考勤组编码">
                  <el-input v-model="groupForm.code" placeholder="例如 HQ_DAY" />
                </el-form-item>
                <el-form-item label="考勤组名称">
                  <el-input v-model="groupForm.name" placeholder="例如 总部白班组" />
                </el-form-item>
                <el-form-item label="默认班次">
                  <el-select v-model="groupForm.shiftId" placeholder="请选择班次">
                    <el-option
                      v-for="item in shifts"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="负责人">
                  <el-select v-model="groupForm.managerUserId" clearable placeholder="可选">
                    <el-option
                      v-for="item in users"
                      :key="item.id"
                      :label="item.displayName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitGroup">保存考勤组</el-button>
            </el-form>
          </el-card>
        </div>

        <div class="panel-grid panel-grid--equal">
          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>班次列表</h3>
              <span>班次基础信息</span>
            </div>
            <el-table :data="shifts" empty-text="暂无班次">
              <el-table-column label="编码" min-width="120">
                <template #default="{ row }">
                  <span class="table-code">{{ row.code }}</span>
                </template>
              </el-table-column>
              <el-table-column label="名称" min-width="120" prop="name" />
              <el-table-column label="时间段" min-width="160">
                <template #default="{ row }">
                  {{ row.startTime }} - {{ row.endTime }}
                </template>
              </el-table-column>
              <el-table-column label="休息分钟" min-width="100" prop="restMinutes" />
            </el-table>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>考勤组列表</h3>
              <span>班次与负责人绑定</span>
            </div>
            <el-table :data="groups" empty-text="暂无考勤组">
              <el-table-column label="编码" min-width="120">
                <template #default="{ row }">
                  <span class="table-code">{{ row.code }}</span>
                </template>
              </el-table-column>
              <el-table-column label="名称" min-width="140" prop="name" />
              <el-table-column label="班次" min-width="120">
                <template #default="{ row }">
                  {{ shiftNameMap[row.shiftId] ?? row.shiftId }}
                </template>
              </el-table-column>
              <el-table-column label="负责人" min-width="120">
                <template #default="{ row }">
                  {{ userNameMap[row.managerUserId ?? -1] ?? '-' }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="排班与打卡">
        <div class="panel-grid panel-grid--equal">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增排班</h3>
              <span>指定员工日期班次</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="员工">
                  <el-select v-model="scheduleForm.employeeId" placeholder="请选择员工">
                    <el-option
                      v-for="item in employees"
                      :key="item.id"
                      :label="item.fullName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="考勤组">
                  <el-select v-model="scheduleForm.attendanceGroupId" placeholder="请选择考勤组">
                    <el-option
                      v-for="item in groups"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="班次">
                  <el-select v-model="scheduleForm.shiftId" placeholder="请选择班次">
                    <el-option
                      v-for="item in shifts"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="日期">
                  <el-date-picker
                    v-model="scheduleForm.scheduleDate"
                    placeholder="选择日期"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitSchedule">保存排班</el-button>
            </el-form>
          </el-card>

          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>补录打卡</h3>
              <span>用于异常补录或测试验证</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="员工">
                  <el-select v-model="clockForm.employeeId" placeholder="请选择员工">
                    <el-option
                      v-for="item in employees"
                      :key="item.id"
                      :label="item.fullName"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="打卡日期">
                  <el-date-picker
                    v-model="clockForm.clockDate"
                    placeholder="选择日期"
                    type="date"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="来源">
                  <el-select v-model="clockForm.source">
                    <el-option label="设备" value="DEVICE" />
                    <el-option label="补录" value="MANUAL" />
                    <el-option label="移动端" value="MOBILE" />
                  </el-select>
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="clockForm.status">
                    <el-option label="正常" value="NORMAL" />
                    <el-option label="迟到" value="LATE" />
                    <el-option label="缺卡" value="MISSING" />
                  </el-select>
                </el-form-item>
                <el-form-item label="上班时间">
                  <el-time-picker
                    v-model="clockForm.clockInTime"
                    clearable
                    format="HH:mm"
                    placeholder="可选"
                    value-format="HH:mm"
                  />
                </el-form-item>
                <el-form-item label="下班时间">
                  <el-time-picker
                    v-model="clockForm.clockOutTime"
                    clearable
                    format="HH:mm"
                    placeholder="可选"
                    value-format="HH:mm"
                  />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitClockRecord">保存打卡</el-button>
            </el-form>
          </el-card>
        </div>

        <div class="panel-grid panel-grid--equal">
          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>排班列表</h3>
              <span>员工每日排班情况</span>
            </div>
            <el-table :data="schedules" empty-text="暂无排班">
              <el-table-column label="员工" min-width="120">
                <template #default="{ row }">
                  {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
                </template>
              </el-table-column>
              <el-table-column label="考勤组" min-width="120">
                <template #default="{ row }">
                  {{ groupNameMap[row.attendanceGroupId] ?? row.attendanceGroupId }}
                </template>
              </el-table-column>
              <el-table-column label="班次" min-width="120">
                <template #default="{ row }">
                  {{ shiftNameMap[row.shiftId] ?? row.shiftId }}
                </template>
              </el-table-column>
              <el-table-column label="日期" min-width="120" prop="scheduleDate" />
            </el-table>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>打卡记录</h3>
              <span>含正常与异常状态</span>
            </div>
            <el-table :data="clockRecords" empty-text="暂无打卡记录">
              <el-table-column label="员工" min-width="120">
                <template #default="{ row }">
                  {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
                </template>
              </el-table-column>
              <el-table-column label="日期" min-width="120" prop="clockDate" />
              <el-table-column label="来源" min-width="100" prop="source" />
              <el-table-column label="上班" min-width="100" prop="clockInTime" />
              <el-table-column label="下班" min-width="100" prop="clockOutTime" />
              <el-table-column label="状态" min-width="100">
                <template #default="{ row }">
                  <el-tag :type="clockStatusType(row.status)" effect="plain">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="月汇总">
        <el-card class="table-card" shadow="never">
          <div class="card-title">
            <h3>月度汇总</h3>
            <div class="toolbar-row">
              <el-date-picker
                v-model="summaryMonth"
                placeholder="选择月份"
                type="month"
                value-format="YYYY-MM"
              />
              <el-button type="primary" @click="generateSummary">生成月汇总</el-button>
            </div>
          </div>
          <el-table :data="summaries" empty-text="暂无月汇总">
            <el-table-column label="员工" min-width="120">
              <template #default="{ row }">
                {{ employeeNameMap[row.employeeId] ?? row.employeeId }}
              </template>
            </el-table-column>
            <el-table-column label="月份" min-width="100" prop="summaryMonth" />
            <el-table-column label="应出勤" min-width="100" prop="shouldAttendDays" />
            <el-table-column label="实出勤" min-width="100" prop="actualAttendDays" />
            <el-table-column label="迟到次数" min-width="100" prop="lateCount" />
            <el-table-column label="请假小时" min-width="100" prop="leaveHours" />
            <el-table-column label="加班小时" min-width="100" prop="overtimeHours" />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createAttendanceGroup,
  createClockRecord,
  createSchedule,
  createShift,
  fetchAttendanceGroups,
  fetchClockRecords,
  fetchMonthlySummaries,
  fetchSchedules,
  fetchShifts,
  generateMonthlySummaries,
  type AttendanceGroup,
  type ClockRecord,
  type MonthlySummary,
  type Schedule,
  type Shift
} from '../../api/attendance';
import { fetchEmployees, type Employee } from '../../api/employee';
import { fetchUsers, type User } from '../../api/user';

const shifts = ref<Shift[]>([]);
const groups = ref<AttendanceGroup[]>([]);
const schedules = ref<Schedule[]>([]);
const clockRecords = ref<ClockRecord[]>([]);
const summaries = ref<MonthlySummary[]>([]);
const employees = ref<Employee[]>([]);
const users = ref<User[]>([]);
const summaryMonth = ref(new Date().toISOString().slice(0, 7));

const shiftForm = reactive({
  code: '',
  name: ''
});

const shiftTime = reactive({
  start: '09:00',
  end: '18:00'
});

const groupForm = reactive({
  code: '',
  name: '',
  shiftId: undefined as number | undefined,
  managerUserId: undefined as number | undefined
});

const scheduleForm = reactive({
  employeeId: undefined as number | undefined,
  attendanceGroupId: undefined as number | undefined,
  shiftId: undefined as number | undefined,
  scheduleDate: ''
});

const clockForm = reactive({
  employeeId: undefined as number | undefined,
  clockDate: '',
  clockInTime: '09:00',
  clockOutTime: '18:00',
  source: 'MANUAL',
  status: 'NORMAL'
});

const employeeNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(employees.value.map((item) => [item.id, item.fullName]))
);

const userNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(users.value.map((item) => [item.id, item.displayName]))
);

const groupNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(groups.value.map((item) => [item.id, item.name]))
);

const shiftNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(shifts.value.map((item) => [item.id, item.name]))
);

const abnormalClockCount = computed(() =>
  clockRecords.value.filter((item) => item.status !== 'NORMAL').length
);

function clockStatusType(status: string) {
  if (status === 'NORMAL') return 'success';
  if (status === 'LATE') return 'warning';
  if (status === 'MISSING') return 'danger';
  return 'info';
}

async function loadData() {
  const [shiftList, groupList, scheduleList, clockList, summaryList, employeeList, userList] =
    await Promise.all([
      fetchShifts(),
      fetchAttendanceGroups(),
      fetchSchedules(),
      fetchClockRecords(),
      fetchMonthlySummaries(),
      fetchEmployees(),
      fetchUsers()
    ]);

  shifts.value = shiftList;
  groups.value = groupList;
  schedules.value = scheduleList;
  clockRecords.value = clockList;
  summaries.value = summaryList;
  employees.value = employeeList;
  users.value = userList;
}

async function submitShift() {
  if (!shiftForm.code || !shiftForm.name || !shiftTime.start || !shiftTime.end) {
    ElMessage.warning('请填写完整班次信息');
    return;
  }

  await createShift({
    code: shiftForm.code,
    name: shiftForm.name,
    startTime: shiftTime.start,
    endTime: shiftTime.end
  });

  shiftForm.code = '';
  shiftForm.name = '';
  shiftTime.start = '09:00';
  shiftTime.end = '18:00';
  ElMessage.success('班次已保存');
  await loadData();
}

async function submitGroup() {
  if (!groupForm.code || !groupForm.name || !groupForm.shiftId) {
    ElMessage.warning('请填写完整考勤组信息');
    return;
  }

  await createAttendanceGroup({
    code: groupForm.code,
    name: groupForm.name,
    shiftId: groupForm.shiftId,
    managerUserId: groupForm.managerUserId
  });

  groupForm.code = '';
  groupForm.name = '';
  groupForm.shiftId = undefined;
  groupForm.managerUserId = undefined;
  ElMessage.success('考勤组已保存');
  await loadData();
}

async function submitSchedule() {
  if (!scheduleForm.employeeId || !scheduleForm.attendanceGroupId || !scheduleForm.shiftId || !scheduleForm.scheduleDate) {
    ElMessage.warning('请填写完整排班信息');
    return;
  }

  await createSchedule({
    employeeId: scheduleForm.employeeId,
    attendanceGroupId: scheduleForm.attendanceGroupId,
    shiftId: scheduleForm.shiftId,
    scheduleDate: scheduleForm.scheduleDate
  });

  scheduleForm.employeeId = undefined;
  scheduleForm.attendanceGroupId = undefined;
  scheduleForm.shiftId = undefined;
  scheduleForm.scheduleDate = '';
  ElMessage.success('排班已保存');
  await loadData();
}

async function submitClockRecord() {
  if (!clockForm.employeeId || !clockForm.clockDate || !clockForm.source || !clockForm.status) {
    ElMessage.warning('请填写完整打卡信息');
    return;
  }

  await createClockRecord({
    employeeId: clockForm.employeeId,
    clockDate: clockForm.clockDate,
    clockInTime: clockForm.clockInTime || undefined,
    clockOutTime: clockForm.clockOutTime || undefined,
    source: clockForm.source,
    status: clockForm.status
  });

  clockForm.employeeId = undefined;
  clockForm.clockDate = '';
  clockForm.clockInTime = '09:00';
  clockForm.clockOutTime = '18:00';
  clockForm.source = 'MANUAL';
  clockForm.status = 'NORMAL';
  ElMessage.success('打卡记录已保存');
  await loadData();
}

async function generateSummary() {
  if (!summaryMonth.value) {
    ElMessage.warning('请选择月份');
    return;
  }

  await generateMonthlySummaries({ summaryMonth: summaryMonth.value });
  ElMessage.success('月汇总已生成');
  await loadData();
}

onMounted(loadData);
</script>
