import http, { type ApiResponse } from './http';

export interface Department {
  id: number;
  code: string;
  name: string;
  parentId?: number | null;
  sort: number;
  enabled: number;
}

export interface DepartmentPayload {
  code: string;
  name: string;
  parentId?: number | null;
  sort?: number;
}

export async function fetchDepartments() {
  const response = await http.get<ApiResponse<Department[]>>('/departments');
  return response.data.data;
}

export async function createDepartment(payload: DepartmentPayload) {
  const response = await http.post<ApiResponse<Department>>('/departments', payload);
  return response.data.data;
}

