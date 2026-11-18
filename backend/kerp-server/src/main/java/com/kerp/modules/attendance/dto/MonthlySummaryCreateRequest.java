package com.kerp.modules.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MonthlySummaryCreateRequest(
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotBlank(message = "Summary month is required")
        @Size(max = 7, message = "Summary month is too long")
        String summaryMonth,
        @NotNull(message = "Should attend days is required")
        BigDecimal shouldAttendDays,
        @NotNull(message = "Actual attend days is required")
        BigDecimal actualAttendDays,
        @NotNull(message = "Late count is required")
        Integer lateCount,
        @NotNull(message = "Leave hours is required")
        BigDecimal leaveHours,
        @NotNull(message = "Overtime hours is required")
        BigDecimal overtimeHours
) {
}
