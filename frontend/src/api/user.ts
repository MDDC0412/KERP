import http, { type ApiResponse } from './http';

export interface User {
  id: number;
  username: string;
  displayName: string;
  departmentName: string;
  email?: string;
  phone?: string;
  enabled: number;
  roles: string[];
}

export interface UserPayload {
  username: string;
  displayName: string;
  email?: string;
  phone?: string;
  departmentId?: number;
  initialPassword?: string;
  roleIds: number[];
}

export async function fetchUsers() {
  const response = await http.get<ApiResponse<User[]>>('/users');
  return response.data.data;
}

export async function createUser(payload: UserPayload) {
  const response = await http.post<ApiResponse<User>>('/users', payload);
  return response.data.data;
}

