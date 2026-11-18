import http, { type ApiResponse } from './http';

export interface Employee {
  id: number;
  employeeNo: string;
  fullName: string;
  departmentId: number;
  phone?: string;
  email?: string;
  hireDate?: string;
  status: number;
}

export interface EmployeePayload {
  employeeNo: string;
  fullName: string;
  departmentId: number;
  phone?: string;
  email?: string;
  hireDate?: string;
}

export async function fetchEmployees() {
  const response = await http.get<ApiResponse<Employee[]>>('/employees');
  return response.data.data;
}

export async function createEmployee(payload: EmployeePayload) {
  const response = await http.post<ApiResponse<Employee>>('/employees', payload);
  return response.data.data;
}

