package com.kerp.modules.user.dto;

import java.util.List;

public record UserListItem(
        Long id,
        String username,
        String displayName,
        String departmentName,
        String email,
        String phone,
        Integer enabled,
        List<String> roles
) {
}

