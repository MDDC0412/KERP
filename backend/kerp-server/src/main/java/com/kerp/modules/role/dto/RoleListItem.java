package com.kerp.modules.role.dto;

public record RoleListItem(
        Long id,
        String code,
        String name,
        String description,
        Integer enabled,
        long userCount
) {
}

