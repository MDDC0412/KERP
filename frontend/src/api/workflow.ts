import http, { type ApiResponse } from './http';

export interface FormDefinition {
  id: number;
  code: string;
  name: string;
  category: string;
  versionNo: number;
  schemaJson: string;
  enabled: number;
}

export interface ProcessDefinition {
  id: number;
  code: string;
  name: string;
  formCode: string;
  approvalType: string;
  configJson: string;
  enabled: number;
}

export interface FormPayload {
  code: string;
  name: string;
  category: string;
  versionNo?: number;
  schemaJson: string;
}

export interface ProcessPayload {
  code: string;
  name: string;
  formCode: string;
  approvalType: string;
  configJson: string;
}

export async function fetchForms() {
  const response = await http.get<ApiResponse<FormDefinition[]>>('/forms');
  return response.data.data;
}

export async function createForm(payload: FormPayload) {
  const response = await http.post<ApiResponse<FormDefinition>>('/forms', payload);
  return response.data.data;
}

export async function fetchWorkflows() {
  const response = await http.get<ApiResponse<ProcessDefinition[]>>('/workflows');
  return response.data.data;
}

export async function createWorkflow(payload: ProcessPayload) {
  const response = await http.post<ApiResponse<ProcessDefinition>>('/workflows', payload);
  return response.data.data;
}
