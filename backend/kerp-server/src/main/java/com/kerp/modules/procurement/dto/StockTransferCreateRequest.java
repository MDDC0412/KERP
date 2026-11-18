package com.kerp.modules.procurement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockTransferCreateRequest(
        @NotBlank(message = "Transfer no is required")
        @Size(max = 64, message = "Transfer no is too long")
        String transferNo,
        @NotNull(message = "Stock is required")
        Long stockId,
        @NotBlank(message = "Target warehouse is required")
        @Size(max = 128, message = "Target warehouse is too long")
        String toWarehouse,
        @NotNull(message = "Transfer quantity is required")
        @DecimalMin(value = "0.01", message = "Transfer quantity must be greater than zero")
        BigDecimal transferQty,
        @NotNull(message = "Transfer date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate transferDate,
        @NotBlank(message = "Transfer by is required")
        @Size(max = 64, message = "Transfer by is too long")
        String transferBy,
        @Size(max = 32, message = "Status is too long")
        String status
) {
}
