package com.kerp.modules.organization.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.organization.dto.DepartmentCreateRequest;
import com.kerp.modules.organization.entity.Department;
import com.kerp.modules.organization.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ApiResponse<List<Department>> listDepartments() {
        return ApiResponse.success(departmentService.listDepartments());
    }

    @PostMapping
    public ApiResponse<Department> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        return ApiResponse.success(departmentService.createDepartment(request));
    }
}

