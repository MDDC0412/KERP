package com.kerp.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuditLogCreateRequest(
        @NotBlank(message = "Module name is required")
        @Size(max = 64, message = "Module name is too long")
        String moduleName,
        @NotBlank(message = "Action name is required")
        @Size(max = 64, message = "Action name is too long")
        String actionName,
        @NotBlank(message = "Operator name is required")
        @Size(max = 64, message = "Operator name is too long")
        String operatorName,
        @NotBlank(message = "Target ref is required")
        @Size(max = 128, message = "Target ref is too long")
        String targetRef,
        @NotBlank(message = "Result status is required")
        @Size(max = 32, message = "Result status is too long")
        String resultStatus,
        @Size(max = 255, message = "Detail is too long")
        String detail
) {
}
