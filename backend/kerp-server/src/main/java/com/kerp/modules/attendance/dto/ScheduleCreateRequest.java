package com.kerp.modules.attendance.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScheduleCreateRequest(
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotNull(message = "Attendance group id is required")
        Long attendanceGroupId,
        @NotNull(message = "Shift id is required")
        Long shiftId,
        @NotNull(message = "Schedule date is required")
        LocalDate scheduleDate
) {
}
