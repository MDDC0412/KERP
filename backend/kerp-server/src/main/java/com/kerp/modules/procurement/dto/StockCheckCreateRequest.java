package com.kerp.modules.procurement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockCheckCreateRequest(
        @NotBlank(message = "Check no is required")
        @Size(max = 64, message = "Check no is too long")
        String checkNo,
        @NotNull(message = "Stock is required")
        Long stockId,
        @NotNull(message = "Actual quantity is required")
        @DecimalMin(value = "0.00", message = "Actual quantity must not be negative")
        BigDecimal actualQty,
        @NotNull(message = "Check date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate checkDate,
        @NotBlank(message = "Checked by is required")
        @Size(max = 64, message = "Checked by is too long")
        String checkedBy,
        @Size(max = 32, message = "Status is too long")
        String status,
        @Size(max = 255, message = "Remark is too long")
        String remark
) {
}
