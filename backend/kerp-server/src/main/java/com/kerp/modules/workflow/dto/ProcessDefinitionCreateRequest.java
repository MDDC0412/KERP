package com.kerp.modules.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProcessDefinitionCreateRequest(
        @NotBlank(message = "Process code is required")
        @Size(max = 64, message = "Process code is too long")
        String code,
        @NotBlank(message = "Process name is required")
        @Size(max = 128, message = "Process name is too long")
        String name,
        @NotBlank(message = "Form code is required")
        @Size(max = 64, message = "Form code is too long")
        String formCode,
        @NotBlank(message = "Approval type is required")
        @Size(max = 32, message = "Approval type is too long")
        String approvalType,
        @NotBlank(message = "Process config is required")
        String configJson
) {
}
