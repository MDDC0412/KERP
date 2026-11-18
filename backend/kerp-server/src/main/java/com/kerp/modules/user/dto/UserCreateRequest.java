package com.kerp.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserCreateRequest(
        @NotBlank(message = "登录名不能为空")
        @Size(max = 64, message = "登录名长度不能超过 64")
        String username,
        @NotBlank(message = "姓名不能为空")
        @Size(max = 64, message = "姓名长度不能超过 64")
        String displayName,
        @Size(max = 128, message = "邮箱长度不能超过 128")
        String email,
        @Size(max = 32, message = "手机号长度不能超过 32")
        String phone,
        Long departmentId,
        @Size(max = 64, message = "初始密码长度不能超过 64")
        String initialPassword,
        @NotEmpty(message = "至少选择一个角色")
        List<Long> roleIds
) {
}

