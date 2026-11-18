package com.kerp.modules.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DataScopeCreateRequest(
        @NotNull(message = "Role id is required")
        Long roleId,
        @NotBlank(message = "Scope code is required")
        @Size(max = 64, message = "Scope code is too long")
        String scopeCode,
        @NotBlank(message = "Scope name is required")
        @Size(max = 128, message = "Scope name is too long")
        String scopeName,
        @NotBlank(message = "Scope rule is required")
        @Size(max = 255, message = "Scope rule is too long")
        String scopeRule
) {
}
