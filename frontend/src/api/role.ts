import http, { type ApiResponse } from './http';

export interface Role {
  id: number;
  code: string;
  name: string;
  description?: string;
  enabled: number;
  userCount: number;
}

export interface RolePayload {
  code: string;
  name: string;
  description?: string;
}

export async function fetchRoles() {
  const response = await http.get<ApiResponse<Role[]>>('/roles');
  return response.data.data;
}

export async function createRole(payload: RolePayload) {
  const response = await http.post<ApiResponse<Role>>('/roles', payload);
  return response.data.data;
}

