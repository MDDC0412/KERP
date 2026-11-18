package com.kerp.modules.permission.dto;

import jakarta.validation.constraints.NotNull;

public record RolePermissionAssignRequest(
        @NotNull(message = "Role id is required")
        Long roleId,
        @NotNull(message = "Permission id is required")
        Long permissionId
) {
}
