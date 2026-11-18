import http, { type ApiResponse } from './http';

export interface ApprovalRequest {
  id: number;
  requestNo: string;
  approvalType: string;
  processCode: string;
  employeeId: number;
  title: string;
  formDataJson: string;
  status: string;
  submittedAt?: string;
  approvedAt?: string;
}

export interface ApprovalTask {
  id: number;
  requestId: number;
  requestNo: string;
  approvalType: string;
  title: string;
  nodeName: string;
  assigneeUserId: number;
  assigneeDisplayName: string;
  actionStatus: string;
  submittedAt: string;
}

export interface ApprovalPayload {
  requestNo: string;
  approvalType: string;
  processCode: string;
  employeeId: number;
  title: string;
  formDataJson: string;
}

export interface ApprovalTaskActionPayload {
  action: 'APPROVE' | 'REJECT';
  comment?: string;
}

export async function fetchApprovals(type?: string) {
  const response = await http.get<ApiResponse<ApprovalRequest[]>>('/approvals', {
    params: type ? { type } : undefined
  });
  return response.data.data;
}

export async function createApproval(payload: ApprovalPayload) {
  const response = await http.post<ApiResponse<ApprovalRequest>>('/approvals', payload);
  return response.data.data;
}

export async function fetchTodoApprovalTasks() {
  const response = await http.get<ApiResponse<ApprovalTask[]>>('/approvals/tasks/todo');
  return response.data.data;
}

export async function handleApprovalTask(taskId: number, payload: ApprovalTaskActionPayload) {
  const response = await http.post<ApiResponse<ApprovalRequest>>(`/approvals/tasks/${taskId}/actions`, payload);
  return response.data.data;
}
