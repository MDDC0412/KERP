package com.kerp.modules.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalRequestCreateRequest(
        @NotBlank(message = "Request no is required")
        @Size(max = 64, message = "Request no is too long")
        String requestNo,
        @NotBlank(message = "Approval type is required")
        @Size(max = 32, message = "Approval type is too long")
        String approvalType,
        @NotBlank(message = "Process code is required")
        @Size(max = 64, message = "Process code is too long")
        String processCode,
        @NotNull(message = "Employee id is required")
        Long employeeId,
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title is too long")
        String title,
        @NotBlank(message = "Form data is required")
        String formDataJson
) {
}
