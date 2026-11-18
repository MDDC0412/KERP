package com.kerp.modules.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShiftCreateRequest(
        @NotBlank(message = "Shift code is required")
        @Size(max = 64, message = "Shift code is too long")
        String code,
        @NotBlank(message = "Shift name is required")
        @Size(max = 128, message = "Shift name is too long")
        String name,
        @NotBlank(message = "Start time is required")
        @Size(max = 8, message = "Start time is too long")
        String startTime,
        @NotBlank(message = "End time is required")
        @Size(max = 8, message = "End time is too long")
        String endTime,
        Integer restMinutes
) {
}
