package com.kerp.modules.procurement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockOutboundCreateRequest(
        @NotBlank(message = "Outbound no is required")
        @Size(max = 64, message = "Outbound no is too long")
        String outboundNo,
        @NotNull(message = "Stock is required")
        Long stockId,
        @NotNull(message = "Outbound quantity is required")
        @DecimalMin(value = "0.01", message = "Outbound quantity must be greater than zero")
        BigDecimal outboundQty,
        @NotNull(message = "Outbound date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate outboundDate,
        @NotBlank(message = "Outbound by is required")
        @Size(max = 64, message = "Outbound by is too long")
        String outboundBy,
        @Size(max = 255, message = "Purpose is too long")
        String purpose,
        @Size(max = 32, message = "Status is too long")
        String status
) {
}
