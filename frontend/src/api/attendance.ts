import http, { type ApiResponse } from './http';

export interface Shift {
  id: number;
  code: string;
  name: string;
  startTime: string;
  endTime: string;
  restMinutes: number;
}

export interface AttendanceGroup {
  id: number;
  code: string;
  name: string;
  shiftId: number;
  managerUserId?: number | null;
}

export interface Schedule {
  id: number;
  employeeId: number;
  attendanceGroupId: number;
  shiftId: number;
  scheduleDate: string;
}

export interface ClockRecord {
  id: number;
  employeeId: number;
  clockDate: string;
  clockInTime?: string;
  clockOutTime?: string;
  source: string;
  status: string;
}

export interface MonthlySummary {
  id: number;
  employeeId: number;
  summaryMonth: string;
  shouldAttendDays: number;
  actualAttendDays: number;
  lateCount: number;
  leaveHours: number;
  overtimeHours: number;
}

export interface ShiftPayload {
  code: string;
  name: string;
  startTime: string;
  endTime: string;
  restMinutes?: number;
}

export interface GroupPayload {
  code: string;
  name: string;
  shiftId: number;
  managerUserId?: number;
}

export interface SchedulePayload {
  employeeId: number;
  attendanceGroupId: number;
  shiftId: number;
  scheduleDate: string;
}

export interface ClockRecordPayload {
  employeeId: number;
  clockDate: string;
  clockInTime?: string;
  clockOutTime?: string;
  source: string;
  status: string;
}

export interface MonthlySummaryPayload {
  employeeId: number;
  summaryMonth: string;
  shouldAttendDays: number;
  actualAttendDays: number;
  lateCount: number;
  leaveHours: number;
  overtimeHours: number;
}

export interface MonthlySummaryGeneratePayload {
  summaryMonth: string;
  employeeId?: number;
}

export async function fetchShifts() {
  const response = await http.get<ApiResponse<Shift[]>>('/attendance/shifts');
  return response.data.data;
}

export async function createShift(payload: ShiftPayload) {
  const response = await http.post<ApiResponse<Shift>>('/attendance/shifts', payload);
  return response.data.data;
}

export async function fetchAttendanceGroups() {
  const response = await http.get<ApiResponse<AttendanceGroup[]>>('/attendance/groups');
  return response.data.data;
}

export async function createAttendanceGroup(payload: GroupPayload) {
  const response = await http.post<ApiResponse<AttendanceGroup>>('/attendance/groups', payload);
  return response.data.data;
}

export async function fetchSchedules() {
  const response = await http.get<ApiResponse<Schedule[]>>('/attendance/schedules');
  return response.data.data;
}

export async function createSchedule(payload: SchedulePayload) {
  const response = await http.post<ApiResponse<Schedule>>('/attendance/schedules', payload);
  return response.data.data;
}

export async function fetchClockRecords() {
  const response = await http.get<ApiResponse<ClockRecord[]>>('/attendance/clock-records');
  return response.data.data;
}

export async function createClockRecord(payload: ClockRecordPayload) {
  const response = await http.post<ApiResponse<ClockRecord>>('/attendance/clock-records', payload);
  return response.data.data;
}

export async function fetchMonthlySummaries() {
  const response = await http.get<ApiResponse<MonthlySummary[]>>('/attendance/monthly-summaries');
  return response.data.data;
}

export async function createMonthlySummary(payload: MonthlySummaryPayload) {
  const response = await http.post<ApiResponse<MonthlySummary>>('/attendance/monthly-summaries', payload);
  return response.data.data;
}

export async function generateMonthlySummaries(payload: MonthlySummaryGeneratePayload) {
  const response = await http.post<ApiResponse<MonthlySummary[]>>('/attendance/monthly-summaries/generate', payload);
  return response.data.data;
}
