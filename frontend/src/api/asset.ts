import http, { type ApiResponse } from './http';

export interface AssetCategory {
  id: number;
  code: string;
  name: string;
  depreciationYears: number;
}

export interface AssetItem {
  id: number;
  assetNo: string;
  categoryId: number;
  assetName: string;
  ownerEmployeeId?: number | null;
  storageLocation?: string;
  assetStatus: string;
  purchaseDate?: string;
}

export interface AssetCategoryPayload {
  code: string;
  name: string;
  depreciationYears?: number;
}

export interface AssetPayload {
  assetNo: string;
  categoryId: number;
  assetName: string;
  ownerEmployeeId?: number;
  storageLocation?: string;
  assetStatus: string;
  purchaseDate?: string;
}

export async function fetchAssetCategories() {
  const response = await http.get<ApiResponse<AssetCategory[]>>('/assets/categories');
  return response.data.data;
}

export async function createAssetCategory(payload: AssetCategoryPayload) {
  const response = await http.post<ApiResponse<AssetCategory>>('/assets/categories', payload);
  return response.data.data;
}

export async function fetchAssets() {
  const response = await http.get<ApiResponse<AssetItem[]>>('/assets');
  return response.data.data;
}

export async function createAsset(payload: AssetPayload) {
  const response = await http.post<ApiResponse<AssetItem>>('/assets', payload);
  return response.data.data;
}
