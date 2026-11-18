package com.kerp.modules.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleCreateRequest(
        @NotBlank(message = "角色编码不能为空")
        @Size(max = 64, message = "角色编码长度不能超过 64")
        String code,
        @NotBlank(message = "角色名称不能为空")
        @Size(max = 128, message = "角色名称长度不能超过 128")
        String name,
        @Size(max = 255, message = "角色描述长度不能超过 255")
        String description
) {
}

