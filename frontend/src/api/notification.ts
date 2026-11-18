import http, { type ApiResponse } from './http';

export interface NotificationMessage {
  id: number;
  title: string;
  content: string;
  targetType: string;
  targetValue: string;
  channel: string;
  readStatus: number;
}

export interface NotificationPayload {
  title: string;
  content: string;
  targetType: string;
  targetValue: string;
}

export async function fetchNotifications() {
  const response = await http.get<ApiResponse<NotificationMessage[]>>('/notifications');
  return response.data.data;
}

export async function createNotification(payload: NotificationPayload) {
  const response = await http.post<ApiResponse<NotificationMessage>>('/notifications', payload);
  return response.data.data;
}

export async function markAllNotificationsRead() {
  const response = await http.post<ApiResponse<null>>('/notifications/mark-all-read');
  return response.data.success;
}
