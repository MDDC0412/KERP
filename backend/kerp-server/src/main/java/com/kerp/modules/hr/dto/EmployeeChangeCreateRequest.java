package com.kerp.modules.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeChangeCreateRequest(
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotBlank(message = "Change type is required")
        @Size(max = 32, message = "Change type is too long")
        String changeType,
        @Size(max = 255, message = "Before value is too long")
        String beforeValue,
        @Size(max = 255, message = "After value is too long")
        String afterValue,
        @NotNull(message = "Effective date is required")
        LocalDate effectiveDate,
        @Size(max = 255, message = "Remark is too long")
        String remark
) {
}
