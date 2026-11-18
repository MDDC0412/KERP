package com.kerp.modules.procurement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
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
import com.kerp.modules.procurement.mapper.InventoryStockMapper;
import com.kerp.modules.procurement.mapper.PurchaseOrderMapper;
import com.kerp.modules.procurement.mapper.PurchaseReceiptMapper;
import com.kerp.modules.procurement.mapper.StockCheckMapper;
import com.kerp.modules.procurement.mapper.StockOutboundMapper;
import com.kerp.modules.procurement.mapper.StockTransferMapper;
import com.kerp.modules.procurement.mapper.SupplierMapper;
import com.kerp.modules.procurement.service.ProcurementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProcurementServiceImpl implements ProcurementService {

    private final SupplierMapper supplierMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseReceiptMapper purchaseReceiptMapper;
    private final InventoryStockMapper inventoryStockMapper;
    private final StockOutboundMapper stockOutboundMapper;
    private final StockTransferMapper stockTransferMapper;
    private final StockCheckMapper stockCheckMapper;

    public ProcurementServiceImpl(SupplierMapper supplierMapper,
                                  PurchaseOrderMapper purchaseOrderMapper,
                                  PurchaseReceiptMapper purchaseReceiptMapper,
                                  InventoryStockMapper inventoryStockMapper,
                                  StockOutboundMapper stockOutboundMapper,
                                  StockTransferMapper stockTransferMapper,
                                  StockCheckMapper stockCheckMapper) {
        this.supplierMapper = supplierMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseReceiptMapper = purchaseReceiptMapper;
        this.inventoryStockMapper = inventoryStockMapper;
        this.stockOutboundMapper = stockOutboundMapper;
        this.stockTransferMapper = stockTransferMapper;
        this.stockCheckMapper = stockCheckMapper;
    }

    @Override
    public Supplier createSupplier(SupplierCreateRequest request) {
        long duplicateCount = supplierMapper.selectCount(
                new LambdaQueryWrapper<Supplier>().eq(Supplier::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Supplier code already exists");
        }
        Supplier supplier = new Supplier();
        supplier.setCode(request.code());
        supplier.setName(request.name());
        supplier.setContactPerson(request.contactPerson());
        supplier.setContactPhone(request.contactPhone());
        supplier.setPaymentTerms(request.paymentTerms());
        supplier.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status());
        supplierMapper.insert(supplier);
        return supplier;
    }

    @Override
    public List<Supplier> listSuppliers() {
        return supplierMapper.selectList(new LambdaQueryWrapper<Supplier>().orderByDesc(Supplier::getId));
    }

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrderCreateRequest request) {
        long duplicateCount = purchaseOrderMapper.selectCount(
                new LambdaQueryWrapper<PurchaseOrder>().eq(PurchaseOrder::getOrderNo, request.orderNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Purchase order no already exists");
        }
        Supplier supplier = supplierMapper.selectById(request.supplierId());
        if (supplier == null) {
            throw new BusinessException("Supplier does not exist");
        }
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderNo(request.orderNo());
        purchaseOrder.setSupplierId(request.supplierId());
        purchaseOrder.setSkuCode(request.skuCode());
        purchaseOrder.setSkuName(request.skuName());
        purchaseOrder.setQuantity(request.quantity());
        purchaseOrder.setUnitPrice(request.unitPrice());
        purchaseOrder.setTotalAmount(request.quantity().multiply(request.unitPrice()));
        purchaseOrder.setWarehouseName(request.warehouseName());
        purchaseOrder.setOrderDate(request.orderDate());
        purchaseOrder.setExpectedDate(request.expectedDate());
        purchaseOrder.setStatus(request.status() == null || request.status().isBlank() ? "CREATED" : request.status());
        purchaseOrder.setRemark(request.remark());
        purchaseOrderMapper.insert(purchaseOrder);
        return purchaseOrder;
    }

    @Override
    public List<PurchaseOrder> listPurchaseOrders() {
        return purchaseOrderMapper.selectList(new LambdaQueryWrapper<PurchaseOrder>().orderByDesc(PurchaseOrder::getId));
    }

    @Override
    @Transactional
    public PurchaseReceipt createPurchaseReceipt(PurchaseReceiptCreateRequest request) {
        long duplicateCount = purchaseReceiptMapper.selectCount(
                new LambdaQueryWrapper<PurchaseReceipt>().eq(PurchaseReceipt::getReceiptNo, request.receiptNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Purchase receipt no already exists");
        }
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectById(request.orderId());
        if (purchaseOrder == null) {
            throw new BusinessException("Purchase order does not exist");
        }

        BigDecimal receivedBefore = purchaseReceiptMapper.selectList(
                new LambdaQueryWrapper<PurchaseReceipt>().eq(PurchaseReceipt::getOrderId, request.orderId())
        ).stream()
                .map(PurchaseReceipt::getReceivedQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receivedAfter = receivedBefore.add(request.receivedQty());
        if (receivedAfter.compareTo(purchaseOrder.getQuantity()) > 0) {
            throw new BusinessException("Received quantity exceeds purchase order quantity");
        }

        PurchaseReceipt purchaseReceipt = new PurchaseReceipt();
        purchaseReceipt.setReceiptNo(request.receiptNo());
        purchaseReceipt.setOrderId(request.orderId());
        purchaseReceipt.setSupplierId(purchaseOrder.getSupplierId());
        purchaseReceipt.setSkuCode(purchaseOrder.getSkuCode());
        purchaseReceipt.setSkuName(purchaseOrder.getSkuName());
        purchaseReceipt.setReceivedQty(request.receivedQty());
        purchaseReceipt.setWarehouseName(request.warehouseName());
        purchaseReceipt.setReceivedDate(request.receivedDate());
        purchaseReceipt.setReceivedBy(request.receivedBy());
        purchaseReceipt.setStatus(request.status() == null || request.status().isBlank() ? "RECEIVED" : request.status());
        purchaseReceiptMapper.insert(purchaseReceipt);

        InventoryStock inventoryStock = inventoryStockMapper.selectOne(
                new LambdaQueryWrapper<InventoryStock>()
                        .eq(InventoryStock::getSkuCode, purchaseOrder.getSkuCode())
                        .eq(InventoryStock::getWarehouseName, request.warehouseName())
                        .eq(InventoryStock::getSupplierId, purchaseOrder.getSupplierId())
        );

        if (inventoryStock == null) {
            inventoryStock = new InventoryStock();
            inventoryStock.setSkuCode(purchaseOrder.getSkuCode());
            inventoryStock.setSkuName(purchaseOrder.getSkuName());
            inventoryStock.setWarehouseName(request.warehouseName());
            inventoryStock.setSupplierId(purchaseOrder.getSupplierId());
            inventoryStock.setReservedQty(BigDecimal.ZERO);
            inventoryStock.setOnHandQty(request.receivedQty());
            inventoryStock.setAvailableQty(request.receivedQty());
            inventoryStock.setLastReceiptDate(request.receivedDate());
            inventoryStockMapper.insert(inventoryStock);
        } else {
            BigDecimal reservedQty = inventoryStock.getReservedQty() == null ? BigDecimal.ZERO : inventoryStock.getReservedQty();
            BigDecimal onHandQty = inventoryStock.getOnHandQty() == null ? BigDecimal.ZERO : inventoryStock.getOnHandQty();
            inventoryStock.setOnHandQty(onHandQty.add(request.receivedQty()));
            inventoryStock.setAvailableQty(inventoryStock.getOnHandQty().subtract(reservedQty));
            inventoryStock.setLastReceiptDate(request.receivedDate());
            inventoryStockMapper.updateById(inventoryStock);
        }

        purchaseOrder.setStatus(
                receivedAfter.compareTo(purchaseOrder.getQuantity()) == 0 ? "RECEIVED" : "PARTIAL_RECEIVED"
        );
        purchaseOrderMapper.updateById(purchaseOrder);

        return purchaseReceipt;
    }

    @Override
    public List<PurchaseReceipt> listPurchaseReceipts() {
        return purchaseReceiptMapper.selectList(new LambdaQueryWrapper<PurchaseReceipt>().orderByDesc(PurchaseReceipt::getId));
    }

    @Override
    public List<InventoryStock> listInventoryStocks() {
        return inventoryStockMapper.selectList(new LambdaQueryWrapper<InventoryStock>().orderByDesc(InventoryStock::getId));
    }

    @Override
    @Transactional
    public StockOutbound createStockOutbound(StockOutboundCreateRequest request) {
        long duplicateCount = stockOutboundMapper.selectCount(
                new LambdaQueryWrapper<StockOutbound>().eq(StockOutbound::getOutboundNo, request.outboundNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Stock outbound no already exists");
        }

        InventoryStock stock = requireStock(request.stockId());
        BigDecimal availableQty = defaultZero(stock.getAvailableQty());
        if (availableQty.compareTo(request.outboundQty()) < 0) {
            throw new BusinessException("Outbound quantity exceeds available stock");
        }

        StockOutbound stockOutbound = new StockOutbound();
        stockOutbound.setOutboundNo(request.outboundNo());
        stockOutbound.setStockId(stock.getId());
        stockOutbound.setSkuCode(stock.getSkuCode());
        stockOutbound.setSkuName(stock.getSkuName());
        stockOutbound.setWarehouseName(stock.getWarehouseName());
        stockOutbound.setOutboundQty(request.outboundQty());
        stockOutbound.setOutboundDate(request.outboundDate());
        stockOutbound.setOutboundBy(request.outboundBy());
        stockOutbound.setPurpose(request.purpose());
        stockOutbound.setStatus(defaultStatus(request.status(), "COMPLETED"));
        stockOutboundMapper.insert(stockOutbound);

        stock.setOnHandQty(defaultZero(stock.getOnHandQty()).subtract(request.outboundQty()));
        stock.setAvailableQty(stock.getOnHandQty().subtract(defaultZero(stock.getReservedQty())));
        inventoryStockMapper.updateById(stock);

        return stockOutbound;
    }

    @Override
    public List<StockOutbound> listStockOutbounds() {
        return stockOutboundMapper.selectList(new LambdaQueryWrapper<StockOutbound>().orderByDesc(StockOutbound::getId));
    }

    @Override
    @Transactional
    public StockTransfer createStockTransfer(StockTransferCreateRequest request) {
        long duplicateCount = stockTransferMapper.selectCount(
                new LambdaQueryWrapper<StockTransfer>().eq(StockTransfer::getTransferNo, request.transferNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Stock transfer no already exists");
        }

        InventoryStock sourceStock = requireStock(request.stockId());
        BigDecimal availableQty = defaultZero(sourceStock.getAvailableQty());
        if (availableQty.compareTo(request.transferQty()) < 0) {
            throw new BusinessException("Transfer quantity exceeds available stock");
        }
        if (sourceStock.getWarehouseName().equalsIgnoreCase(request.toWarehouse())) {
            throw new BusinessException("Target warehouse must be different from source warehouse");
        }

        StockTransfer stockTransfer = new StockTransfer();
        stockTransfer.setTransferNo(request.transferNo());
        stockTransfer.setStockId(sourceStock.getId());
        stockTransfer.setSkuCode(sourceStock.getSkuCode());
        stockTransfer.setSkuName(sourceStock.getSkuName());
        stockTransfer.setFromWarehouse(sourceStock.getWarehouseName());
        stockTransfer.setToWarehouse(request.toWarehouse());
        stockTransfer.setTransferQty(request.transferQty());
        stockTransfer.setTransferDate(request.transferDate());
        stockTransfer.setTransferBy(request.transferBy());
        stockTransfer.setStatus(defaultStatus(request.status(), "COMPLETED"));
        stockTransferMapper.insert(stockTransfer);

        sourceStock.setOnHandQty(defaultZero(sourceStock.getOnHandQty()).subtract(request.transferQty()));
        sourceStock.setAvailableQty(sourceStock.getOnHandQty().subtract(defaultZero(sourceStock.getReservedQty())));
        inventoryStockMapper.updateById(sourceStock);

        InventoryStock targetStock = inventoryStockMapper.selectOne(
                new LambdaQueryWrapper<InventoryStock>()
                        .eq(InventoryStock::getSkuCode, sourceStock.getSkuCode())
                        .eq(InventoryStock::getWarehouseName, request.toWarehouse())
                        .eq(InventoryStock::getSupplierId, sourceStock.getSupplierId())
        );

        if (targetStock == null) {
            targetStock = new InventoryStock();
            targetStock.setSkuCode(sourceStock.getSkuCode());
            targetStock.setSkuName(sourceStock.getSkuName());
            targetStock.setWarehouseName(request.toWarehouse());
            targetStock.setSupplierId(sourceStock.getSupplierId());
            targetStock.setReservedQty(BigDecimal.ZERO);
            targetStock.setOnHandQty(request.transferQty());
            targetStock.setAvailableQty(request.transferQty());
            targetStock.setLastReceiptDate(request.transferDate());
            inventoryStockMapper.insert(targetStock);
        } else {
            targetStock.setOnHandQty(defaultZero(targetStock.getOnHandQty()).add(request.transferQty()));
            targetStock.setAvailableQty(targetStock.getOnHandQty().subtract(defaultZero(targetStock.getReservedQty())));
            targetStock.setLastReceiptDate(request.transferDate());
            inventoryStockMapper.updateById(targetStock);
        }

        return stockTransfer;
    }

    @Override
    public List<StockTransfer> listStockTransfers() {
        return stockTransferMapper.selectList(new LambdaQueryWrapper<StockTransfer>().orderByDesc(StockTransfer::getId));
    }

    @Override
    @Transactional
    public StockCheck createStockCheck(StockCheckCreateRequest request) {
        long duplicateCount = stockCheckMapper.selectCount(
                new LambdaQueryWrapper<StockCheck>().eq(StockCheck::getCheckNo, request.checkNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Stock check no already exists");
        }

        InventoryStock stock = requireStock(request.stockId());
        BigDecimal systemQty = defaultZero(stock.getOnHandQty());
        BigDecimal varianceQty = request.actualQty().subtract(systemQty);

        StockCheck stockCheck = new StockCheck();
        stockCheck.setCheckNo(request.checkNo());
        stockCheck.setStockId(stock.getId());
        stockCheck.setSkuCode(stock.getSkuCode());
        stockCheck.setSkuName(stock.getSkuName());
        stockCheck.setWarehouseName(stock.getWarehouseName());
        stockCheck.setSystemQty(systemQty);
        stockCheck.setActualQty(request.actualQty());
        stockCheck.setVarianceQty(varianceQty);
        stockCheck.setCheckDate(request.checkDate());
        stockCheck.setCheckedBy(request.checkedBy());
        stockCheck.setStatus(defaultStatus(request.status(), "COUNTED"));
        stockCheck.setRemark(request.remark());
        stockCheckMapper.insert(stockCheck);

        stock.setOnHandQty(request.actualQty());
        stock.setAvailableQty(request.actualQty().subtract(defaultZero(stock.getReservedQty())));
        inventoryStockMapper.updateById(stock);

        return stockCheck;
    }

    @Override
    public List<StockCheck> listStockChecks() {
        return stockCheckMapper.selectList(new LambdaQueryWrapper<StockCheck>().orderByDesc(StockCheck::getId));
    }

    private InventoryStock requireStock(Long stockId) {
        InventoryStock stock = inventoryStockMapper.selectById(stockId);
        if (stock == null) {
            throw new BusinessException("Inventory stock does not exist");
        }
        return stock;
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String defaultStatus(String status, String fallback) {
        return status == null || status.isBlank() ? fallback : status;
    }
}
