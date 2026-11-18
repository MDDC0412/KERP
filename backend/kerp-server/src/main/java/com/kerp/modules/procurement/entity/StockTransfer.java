package com.kerp.modules.procurement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kerp.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("inv_stock_transfer")
public class StockTransfer extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String transferNo;
    private Long stockId;
    private String skuCode;
    private String skuName;
    private String fromWarehouse;
    private String toWarehouse;
    private BigDecimal transferQty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transferDate;
    private String transferBy;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransferNo() {
        return transferNo;
    }

    public void setTransferNo(String transferNo) {
        this.transferNo = transferNo;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getFromWarehouse() {
        return fromWarehouse;
    }

    public void setFromWarehouse(String fromWarehouse) {
        this.fromWarehouse = fromWarehouse;
    }

    public String getToWarehouse() {
        return toWarehouse;
    }

    public void setToWarehouse(String toWarehouse) {
        this.toWarehouse = toWarehouse;
    }

    public BigDecimal getTransferQty() {
        return transferQty;
    }

    public void setTransferQty(BigDecimal transferQty) {
        this.transferQty = transferQty;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getTransferBy() {
        return transferBy;
    }

    public void setTransferBy(String transferBy) {
        this.transferBy = transferBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
