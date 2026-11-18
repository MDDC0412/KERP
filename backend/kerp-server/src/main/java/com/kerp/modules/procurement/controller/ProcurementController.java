package com.kerp.modules.procurement.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.common.security.RequirePermission;
import com.kerp.modules.procurement.dto.PurchaseOrderCreateRequest;
import com.kerp.modules.procurement.dto.PurchaseReceiptCreateRequest;
import com.kerp.modules.procurement.dto.StockCheckCreateRequest;
import com.kerp.modules.procurement.dto.StockOutboundCreateRequest;
import com.kerp.modules.procurement.dto.StockTransferCreateRequest;
import com.kerp.modules.procurement.dto.SupplierCreateRequest;
import com.kerp.modules.procurement.entity.InventoryStock;
import com.kerp.modules.procurement.entity.PurchaseOrder;
import com.kerp.modules.procurement.entity.PurchaseReceipt;
import com.kerp.modules.procurement.entity.StockCheck;
import com.kerp.modules.procurement.entity.StockOutbound;
import com.kerp.modules.procurement.entity.StockTransfer;
import com.kerp.modules.procurement.entity.Supplier;
import com.kerp.modules.procurement.service.ProcurementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/procurement")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @GetMapping("/suppliers")
    public ApiResponse<List<Supplier>> listSuppliers() {
        return ApiResponse.success(procurementService.listSuppliers());
    }

    @PostMapping("/suppliers")
    @RequirePermission("PROCUREMENT_MANAGE")
    public ApiResponse<Supplier> createSupplier(@Valid @RequestBody SupplierCreateRequest request) {
        return ApiResponse.success(procurementService.createSupplier(request));
    }

    @GetMapping("/orders")
    public ApiResponse<List<PurchaseOrder>> listPurchaseOrders() {
        return ApiResponse.success(procurementService.listPurchaseOrders());
    }

    @PostMapping("/orders")
    @RequirePermission("PROCUREMENT_MANAGE")
    public ApiResponse<PurchaseOrder> createPurchaseOrder(@Valid @RequestBody PurchaseOrderCreateRequest request) {
        return ApiResponse.success(procurementService.createPurchaseOrder(request));
    }

    @GetMapping("/receipts")
    public ApiResponse<List<PurchaseReceipt>> listPurchaseReceipts() {
        return ApiResponse.success(procurementService.listPurchaseReceipts());
    }

    @PostMapping("/receipts")
    @RequirePermission("INVENTORY_MANAGE")
    public ApiResponse<PurchaseReceipt> createPurchaseReceipt(@Valid @RequestBody PurchaseReceiptCreateRequest request) {
        return ApiResponse.success(procurementService.createPurchaseReceipt(request));
    }

    @GetMapping("/stocks")
    public ApiResponse<List<InventoryStock>> listInventoryStocks() {
        return ApiResponse.success(procurementService.listInventoryStocks());
    }

    @GetMapping("/outbounds")
    public ApiResponse<List<StockOutbound>> listStockOutbounds() {
        return ApiResponse.success(procurementService.listStockOutbounds());
    }

    @PostMapping("/outbounds")
    @RequirePermission("INVENTORY_MANAGE")
    public ApiResponse<StockOutbound> createStockOutbound(@Valid @RequestBody StockOutboundCreateRequest request) {
        return ApiResponse.success(procurementService.createStockOutbound(request));
    }

    @GetMapping("/transfers")
    public ApiResponse<List<StockTransfer>> listStockTransfers() {
        return ApiResponse.success(procurementService.listStockTransfers());
    }

    @PostMapping("/transfers")
    @RequirePermission("INVENTORY_MANAGE")
    public ApiResponse<StockTransfer> createStockTransfer(@Valid @RequestBody StockTransferCreateRequest request) {
        return ApiResponse.success(procurementService.createStockTransfer(request));
    }

    @GetMapping("/checks")
    public ApiResponse<List<StockCheck>> listStockChecks() {
        return ApiResponse.success(procurementService.listStockChecks());
    }

    @PostMapping("/checks")
    @RequirePermission("INVENTORY_MANAGE")
    public ApiResponse<StockCheck> createStockCheck(@Valid @RequestBody StockCheckCreateRequest request) {
        return ApiResponse.success(procurementService.createStockCheck(request));
    }
}
