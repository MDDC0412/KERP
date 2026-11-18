package com.kerp.modules.procurement.service;

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

import java.util.List;

public interface ProcurementService {

    Supplier createSupplier(SupplierCreateRequest request);

    List<Supplier> listSuppliers();

    PurchaseOrder createPurchaseOrder(PurchaseOrderCreateRequest request);

    List<PurchaseOrder> listPurchaseOrders();

    PurchaseReceipt createPurchaseReceipt(PurchaseReceiptCreateRequest request);

    List<PurchaseReceipt> listPurchaseReceipts();

    List<InventoryStock> listInventoryStocks();

    StockOutbound createStockOutbound(StockOutboundCreateRequest request);

    List<StockOutbound> listStockOutbounds();

    StockTransfer createStockTransfer(StockTransferCreateRequest request);

    List<StockTransfer> listStockTransfers();

    StockCheck createStockCheck(StockCheckCreateRequest request);

    List<StockCheck> listStockChecks();
}
