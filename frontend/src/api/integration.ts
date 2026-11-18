import http, { type ApiResponse } from './http';

export interface IntegrationProvider {
  code: string;
  name: string;
  category: string;
  status: 'ENABLED' | 'RESERVED';
  summary: string;
  enabledCapabilities: string[];
  reservedCapabilities: string[];
  futureRequirements: string[];
}

export interface IntegrationOverview {
  architecturePrinciples: string[];
  noPreparationNeeded: string[];
  providers: IntegrationProvider[];
}

export async function fetchIntegrationOverview() {
  const response = await http.get<ApiResponse<IntegrationOverview>>('/integrations/reservations');
  return response.data.data;
}
