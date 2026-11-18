package com.kerp.modules.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record OffboardingCreateRequest(
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotNull(message = "Plan date is required")
        LocalDate planDate,
        @NotNull(message = "Last working date is required")
        LocalDate lastWorkingDate,
        @Size(max = 255, message = "Reason is too long")
        String reason,
        @NotBlank(message = "Status is required")
        String status
) {
}
