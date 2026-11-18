package com.kerp.common.security;

import java.util.List;

public record CurrentUser(
        Long userId,
        String username,
        String displayName,
        List<String> roleCodes,
        List<String> permissionCodes
) {

    public boolean hasRole(String roleCode) {
        return roleCodes != null && roleCodes.contains(roleCode);
    }

    public boolean hasPermission(String permissionCode) {
        return permissionCodes != null && permissionCodes.contains(permissionCode);
    }
}
