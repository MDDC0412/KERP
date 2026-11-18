package com.kerp.modules.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank(message = "员工编号不能为空")
        @Size(max = 64, message = "员工编号长度不能超过 64")
        String employeeNo,
        @NotBlank(message = "员工姓名不能为空")
        @Size(max = 64, message = "员工姓名长度不能超过 64")
        String fullName,
        @NotNull(message = "所属部门不能为空")
        Long departmentId,
        @Size(max = 32, message = "手机号长度不能超过 32")
        String phone,
        @Email(message = "邮箱格式不正确")
        @Size(max = 128, message = "邮箱长度不能超过 128")
        String email,
        LocalDate hireDate
) {
}

