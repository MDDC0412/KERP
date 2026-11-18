package com.kerp.modules.employee.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.employee.dto.EmployeeCreateRequest;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ApiResponse<List<Employee>> listEmployees() {
        return ApiResponse.success(employeeService.listEmployees());
    }

    @PostMapping
    public ApiResponse<Employee> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ApiResponse.success(employeeService.createEmployee(request));
    }
}

