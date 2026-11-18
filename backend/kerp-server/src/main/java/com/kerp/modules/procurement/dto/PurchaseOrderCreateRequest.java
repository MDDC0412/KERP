package com.kerp.modules.procurement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseOrderCreateRequest(
        @NotBlank(message = "Order no is required")
        @Size(max = 64, message = "Order no is too long")
        String orderNo,
        @NotNull(message = "Supplier is required")
        Long supplierId,
        @NotBlank(message = "SKU code is required")
        @Size(max = 64, message = "SKU code is too long")
        String skuCode,
        @NotBlank(message = "SKU name is required")
        @Size(max = 128, message = "SKU name is too long")
        String skuName,
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
        BigDecimal quantity,
        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than zero")
        BigDecimal unitPrice,
        @NotBlank(message = "Warehouse name is required")
        @Size(max = 128, message = "Warehouse name is too long")
        String warehouseName,
        @NotNull(message = "Order date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate orderDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expectedDate,
        @Size(max = 32, message = "Status is too long")
        String status,
        @Size(max = 255, message = "Remark is too long")
        String remark
) {
}
