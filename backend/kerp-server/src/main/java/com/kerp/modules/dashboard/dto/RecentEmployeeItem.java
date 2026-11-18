package com.kerp.modules.dashboard.dto;

public record RecentEmployeeItem(
        String employeeNo,
        String fullName,
        String departmentName,
        String hireDate
) {
}

