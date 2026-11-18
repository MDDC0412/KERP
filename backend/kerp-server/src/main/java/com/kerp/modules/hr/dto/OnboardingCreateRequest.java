package com.kerp.modules.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record OnboardingCreateRequest(
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotNull(message = "Plan date is required")
        LocalDate planDate,
        Long ownerUserId,
        @NotBlank(message = "Checklist is required")
        String checklistJson,
        @NotBlank(message = "Status is required")
        String status
) {
}
