package com.kerp.modules.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AttendanceGroupCreateRequest(
        @NotBlank(message = "Group code is required")
        @Size(max = 64, message = "Group code is too long")
        String code,
        @NotBlank(message = "Group name is required")
        @Size(max = 128, message = "Group name is too long")
        String name,
        @NotNull(message = "Shift id is required")
        Long shiftId,
        Long managerUserId
) {
}
