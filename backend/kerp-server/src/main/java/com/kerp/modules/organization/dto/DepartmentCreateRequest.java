package com.kerp.modules.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentCreateRequest(
        @NotBlank(message = "部门编码不能为空")
        @Size(max = 64, message = "部门编码长度不能超过 64")
        String code,
        @NotBlank(message = "部门名称不能为空")
        @Size(max = 128, message = "部门名称长度不能超过 128")
        String name,
        Long parentId,
        Integer sort
) {
}

