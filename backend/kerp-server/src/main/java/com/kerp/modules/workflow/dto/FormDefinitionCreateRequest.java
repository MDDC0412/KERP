package com.kerp.modules.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FormDefinitionCreateRequest(
        @NotBlank(message = "Form code is required")
        @Size(max = 64, message = "Form code is too long")
        String code,
        @NotBlank(message = "Form name is required")
        @Size(max = 128, message = "Form name is too long")
        String name,
        @NotBlank(message = "Form category is required")
        @Size(max = 64, message = "Form category is too long")
        String category,
        Integer versionNo,
        @NotBlank(message = "Form schema is required")
        String schemaJson
) {
}
