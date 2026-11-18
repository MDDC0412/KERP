package com.kerp.modules.procurement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseReceiptCreateRequest(
        @NotBlank(message = "Receipt no is required")
        @Size(max = 64, message = "Receipt no is too long")
        String receiptNo,
        @NotNull(message = "Purchase order is required")
        Long orderId,
        @NotNull(message = "Received quantity is required")
        @DecimalMin(value = "0.01", message = "Received quantity must be greater than zero")
        BigDecimal receivedQty,
        @NotBlank(message = "Warehouse name is required")
        @Size(max = 128, message = "Warehouse name is too long")
        String warehouseName,
        @NotNull(message = "Received date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate receivedDate,
        @NotBlank(message = "Received by is required")
        @Size(max = 64, message = "Received by is too long")
        String receivedBy,
        @Size(max = 32, message = "Status is too long")
        String status
) {
}
