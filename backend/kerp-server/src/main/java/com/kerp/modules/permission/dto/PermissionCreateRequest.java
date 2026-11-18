package com.kerp.modules.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionCreateRequest(
        @NotBlank(message = "Permission code is required")
        @Size(max = 64, message = "Permission code is too long")
        String code,
        @NotBlank(message = "Permission name is required")
        @Size(max = 128, message = "Permission name is too long")
        String name,
        @NotBlank(message = "Module name is required")
        @Size(max = 64, message = "Module name is too long")
        String moduleName,
        @NotBlank(message = "Permission type is required")
        @Size(max = 32, message = "Permission type is too long")
        String permissionType
) {
}
