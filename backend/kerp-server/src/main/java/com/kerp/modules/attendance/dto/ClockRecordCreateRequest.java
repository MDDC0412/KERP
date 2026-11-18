package com.kerp.modules.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClockRecordCreateRequest(
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotNull(message = "Clock date is required")
        LocalDate clockDate,
        LocalDateTime clockInTime,
        LocalDateTime clockOutTime,
        @NotBlank(message = "Clock source is required")
        @Size(max = 32, message = "Clock source is too long")
        String source,
        @NotBlank(message = "Clock status is required")
        @Size(max = 32, message = "Clock status is too long")
        String status
) {
}
