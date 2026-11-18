import http, { type ApiResponse } from './http';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginResult {
  accessToken: string;
  userId: number;
  username: string;
  displayName: string;
  roles: string[];
  permissions: string[];
}

export async function login(payload: LoginPayload) {
  const response = await http.post<ApiResponse<LoginResult>>('/auth/login', payload);
  return response.data.data;
}
