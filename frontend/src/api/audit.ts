import http, { type ApiResponse } from './http';

export interface AuditLog {
  id: number;
  moduleName: string;
  actionName: string;
  operatorName: string;
  targetRef: string;
  resultStatus: string;
  detail?: string;
}

export interface AuditPayload {
  moduleName: string;
  actionName: string;
  operatorName: string;
  targetRef: string;
  resultStatus: string;
  detail?: string;
}

export async function fetchAuditLogs() {
  const response = await http.get<ApiResponse<AuditLog[]>>('/audit-logs');
  return response.data.data;
}

export async function createAuditLog(payload: AuditPayload) {
  const response = await http.post<ApiResponse<AuditLog>>('/audit-logs', payload);
  return response.data.data;
}
