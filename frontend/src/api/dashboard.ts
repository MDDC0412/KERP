import http, { type ApiResponse } from './http';

export interface DashboardMetric {
  label: string;
  value: string;
  accent: string;
  hint: string;
}

export interface DistributionItem {
  label: string;
  value: number;
}

export interface RecentEmployeeItem {
  employeeNo: string;
  fullName: string;
  departmentName: string;
  hireDate: string;
}

export interface DashboardOverview {
  metrics: DashboardMetric[];
  departmentDistribution: DistributionItem[];
  roleDistribution: DistributionItem[];
  recentEmployees: RecentEmployeeItem[];
}

export async function fetchDashboardOverview() {
  const response = await http.get<ApiResponse<DashboardOverview>>('/dashboard/overview');
  return response.data.data;
}

