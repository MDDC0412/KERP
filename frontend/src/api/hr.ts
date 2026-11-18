import http, { type ApiResponse } from './http';

export interface EmployeeChange {
  id: number;
  employeeId: number;
  changeType: string;
  beforeValue?: string;
  afterValue?: string;
  effectiveDate: string;
  remark?: string;
}

export interface OnboardingPlan {
  id: number;
  employeeId: number;
  planDate: string;
  ownerUserId?: number | null;
  checklistJson: string;
  status: string;
}

export interface OffboardingPlan {
  id: number;
  employeeId: number;
  planDate: string;
  lastWorkingDate: string;
  reason?: string;
  status: string;
}

export interface EmployeeChangePayload {
  employeeId: number;
  changeType: string;
  beforeValue?: string;
  afterValue?: string;
  effectiveDate: string;
  remark?: string;
}

export interface OnboardingPayload {
  employeeId: number;
  planDate: string;
  ownerUserId?: number;
  checklistJson: string;
  status: string;
}

export interface OffboardingPayload {
  employeeId: number;
  planDate: string;
  lastWorkingDate: string;
  reason?: string;
  status: string;
}

export async function fetchEmployeeChanges() {
  const response = await http.get<ApiResponse<EmployeeChange[]>>('/hr/changes');
  return response.data.data;
}

export async function createEmployeeChange(payload: EmployeeChangePayload) {
  const response = await http.post<ApiResponse<EmployeeChange>>('/hr/changes', payload);
  return response.data.data;
}

export async function fetchOnboardingPlans() {
  const response = await http.get<ApiResponse<OnboardingPlan[]>>('/hr/onboarding');
  return response.data.data;
}

export async function createOnboarding(payload: OnboardingPayload) {
  const response = await http.post<ApiResponse<OnboardingPlan>>('/hr/onboarding', payload);
  return response.data.data;
}

export async function fetchOffboardingPlans() {
  const response = await http.get<ApiResponse<OffboardingPlan[]>>('/hr/offboarding');
  return response.data.data;
}

export async function createOffboarding(payload: OffboardingPayload) {
  const response = await http.post<ApiResponse<OffboardingPlan>>('/hr/offboarding', payload);
  return response.data.data;
}
