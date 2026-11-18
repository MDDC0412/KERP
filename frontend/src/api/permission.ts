import http, { type ApiResponse } from './http';

export interface Permission {
  id: number;
  code: string;
  name: string;
  moduleName: string;
  permissionType: string;
}

export interface RolePermission {
  id: number;
  roleId: number;
  permissionId: number;
}

export interface DataScope {
  id: number;
  roleId: number;
  scopeCode: string;
  scopeName: string;
  scopeRule: string;
}

export interface PermissionPayload {
  code: string;
  name: string;
  moduleName: string;
  permissionType: string;
}

export interface RolePermissionPayload {
  roleId: number;
  permissionId: number;
}

export interface DataScopePayload {
  roleId: number;
  scopeCode: string;
  scopeName: string;
  scopeRule: string;
}

export async function fetchPermissions() {
  const response = await http.get<ApiResponse<Permission[]>>('/permissions');
  return response.data.data;
}

export async function createPermission(payload: PermissionPayload) {
  const response = await http.post<ApiResponse<Permission>>('/permissions', payload);
  return response.data.data;
}

export async function fetchRolePermissions() {
  const response = await http.get<ApiResponse<RolePermission[]>>('/role-permissions');
  return response.data.data;
}

export async function createRolePermission(payload: RolePermissionPayload) {
  const response = await http.post<ApiResponse<RolePermission>>('/role-permissions', payload);
  return response.data.data;
}

export async function fetchDataScopes() {
  const response = await http.get<ApiResponse<DataScope[]>>('/data-scopes');
  return response.data.data;
}

export async function createDataScope(payload: DataScopePayload) {
  const response = await http.post<ApiResponse<DataScope>>('/data-scopes', payload);
  return response.data.data;
}
