package com.kerp.modules.procurement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kerp.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("inv_stock_outbound")
public class StockOutbound extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String outboundNo;
    private Long stockId;
    private String skuCode;
    private String skuName;
    private String warehouseName;
    private BigDecimal outboundQty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outboundDate;
    private String outboundBy;
    private String purpose;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutboundNo() {
        return outboundNo;
    }

    public void setOutboundNo(String outboundNo) {
        this.outboundNo = outboundNo;
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

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public BigDecimal getOutboundQty() {
        return outboundQty;
    }

    public void setOutboundQty(BigDecimal outboundQty) {
        this.outboundQty = outboundQty;
    }

    public LocalDate getOutboundDate() {
        return outboundDate;
    }

    public void setOutboundDate(LocalDate outboundDate) {
        this.outboundDate = outboundDate;
    }

    public String getOutboundBy() {
        return outboundBy;
    }

    public void setOutboundBy(String outboundBy) {
        this.outboundBy = outboundBy;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
