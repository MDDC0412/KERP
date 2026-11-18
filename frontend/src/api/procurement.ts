import http, { type ApiResponse } from './http';

export interface Supplier {
  id: number;
  code: string;
  name: string;
  contactPerson?: string;
  contactPhone?: string;
  paymentTerms?: string;
  status: string;
}

export interface PurchaseOrder {
  id: number;
  orderNo: string;
  supplierId: number;
  skuCode: string;
  skuName: string;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  warehouseName: string;
  orderDate: string;
  expectedDate?: string;
  status: string;
  remark?: string;
}

export interface PurchaseReceipt {
  id: number;
  receiptNo: string;
  orderId: number;
  supplierId: number;
  skuCode: string;
  skuName: string;
  receivedQty: number;
  warehouseName: string;
  receivedDate: string;
  receivedBy: string;
  status: string;
}

export interface InventoryStock {
  id: number;
  skuCode: string;
  skuName: string;
  warehouseName: string;
  supplierId?: number | null;
  onHandQty: number;
  reservedQty: number;
  availableQty: number;
  lastReceiptDate?: string;
}

export interface StockOutbound {
  id: number;
  outboundNo: string;
  stockId: number;
  skuCode: string;
  skuName: string;
  warehouseName: string;
  outboundQty: number;
  outboundDate: string;
  outboundBy: string;
  purpose?: string;
  status: string;
}

export interface StockTransfer {
  id: number;
  transferNo: string;
  stockId: number;
  skuCode: string;
  skuName: string;
  fromWarehouse: string;
  toWarehouse: string;
  transferQty: number;
  transferDate: string;
  transferBy: string;
  status: string;
}

export interface StockCheck {
  id: number;
  checkNo: string;
  stockId: number;
  skuCode: string;
  skuName: string;
  warehouseName: string;
  systemQty: number;
  actualQty: number;
  varianceQty: number;
  checkDate: string;
  checkedBy: string;
  status: string;
  remark?: string;
}

export interface SupplierPayload {
  code: string;
  name: string;
  contactPerson?: string;
  contactPhone?: string;
  paymentTerms?: string;
  status?: string;
}

export interface PurchaseOrderPayload {
  orderNo: string;
  supplierId: number;
  skuCode: string;
  skuName: string;
  quantity: number;
  unitPrice: number;
  warehouseName: string;
  orderDate: string;
  expectedDate?: string;
  status?: string;
  remark?: string;
}

export interface PurchaseReceiptPayload {
  receiptNo: string;
  orderId: number;
  receivedQty: number;
  warehouseName: string;
  receivedDate: string;
  receivedBy: string;
  status?: string;
}

export interface StockOutboundPayload {
  outboundNo: string;
  stockId: number;
  outboundQty: number;
  outboundDate: string;
  outboundBy: string;
  purpose?: string;
  status?: string;
}

export interface StockTransferPayload {
  transferNo: string;
  stockId: number;
  toWarehouse: string;
  transferQty: number;
  transferDate: string;
  transferBy: string;
  status?: string;
}

export interface StockCheckPayload {
  checkNo: string;
  stockId: number;
  actualQty: number;
  checkDate: string;
  checkedBy: string;
  status?: string;
  remark?: string;
}

export async function fetchSuppliers() {
  const response = await http.get<ApiResponse<Supplier[]>>('/procurement/suppliers');
  return response.data.data;
}

export async function createSupplier(payload: SupplierPayload) {
  const response = await http.post<ApiResponse<Supplier>>('/procurement/suppliers', payload);
  return response.data.data;
}

export async function fetchPurchaseOrders() {
  const response = await http.get<ApiResponse<PurchaseOrder[]>>('/procurement/orders');
  return response.data.data;
}

export async function createPurchaseOrder(payload: PurchaseOrderPayload) {
  const response = await http.post<ApiResponse<PurchaseOrder>>('/procurement/orders', payload);
  return response.data.data;
}

export async function fetchPurchaseReceipts() {
  const response = await http.get<ApiResponse<PurchaseReceipt[]>>('/procurement/receipts');
  return response.data.data;
}

export async function createPurchaseReceipt(payload: PurchaseReceiptPayload) {
  const response = await http.post<ApiResponse<PurchaseReceipt>>('/procurement/receipts', payload);
  return response.data.data;
}

export async function fetchInventoryStocks() {
  const response = await http.get<ApiResponse<InventoryStock[]>>('/procurement/stocks');
  return response.data.data;
}

export async function fetchStockOutbounds() {
  const response = await http.get<ApiResponse<StockOutbound[]>>('/procurement/outbounds');
  return response.data.data;
}

export async function createStockOutbound(payload: StockOutboundPayload) {
  const response = await http.post<ApiResponse<StockOutbound>>('/procurement/outbounds', payload);
  return response.data.data;
}

export async function fetchStockTransfers() {
  const response = await http.get<ApiResponse<StockTransfer[]>>('/procurement/transfers');
  return response.data.data;
}

export async function createStockTransfer(payload: StockTransferPayload) {
  const response = await http.post<ApiResponse<StockTransfer>>('/procurement/transfers', payload);
  return response.data.data;
}

export async function fetchStockChecks() {
  const response = await http.get<ApiResponse<StockCheck[]>>('/procurement/checks');
  return response.data.data;
}

export async function createStockCheck(payload: StockCheckPayload) {
  const response = await http.post<ApiResponse<StockCheck>>('/procurement/checks', payload);
  return response.data.data;
}
