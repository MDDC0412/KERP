package com.kerp.modules.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MonthlySummaryGenerateRequest(
        @NotBlank(message = "Summary month is required")
        @Size(min = 7, max = 7, message = "Summary month must be in yyyy-MM format")
        String summaryMonth,
        Long employeeId
) {
}
