package com.kerp.modules.procurement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kerp.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("inv_stock_check")
public class StockCheck extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String checkNo;
    private Long stockId;
    private String skuCode;
    private String skuName;
    private String warehouseName;
    private BigDecimal systemQty;
    private BigDecimal actualQty;
    private BigDecimal varianceQty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;
    private String checkedBy;
    private String status;
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
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

    public BigDecimal getSystemQty() {
        return systemQty;
    }

    public void setSystemQty(BigDecimal systemQty) {
        this.systemQty = systemQty;
    }

    public BigDecimal getActualQty() {
        return actualQty;
    }

    public void setActualQty(BigDecimal actualQty) {
        this.actualQty = actualQty;
    }

    public BigDecimal getVarianceQty() {
        return varianceQty;
    }

    public void setVarianceQty(BigDecimal varianceQty) {
        this.varianceQty = varianceQty;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(String checkedBy) {
        this.checkedBy = checkedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
