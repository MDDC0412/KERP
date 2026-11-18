package com.kerp.modules.auth.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        Long userId,
        String username,
        String displayName,
        List<String> roles,
        List<String> permissions
) {
}
