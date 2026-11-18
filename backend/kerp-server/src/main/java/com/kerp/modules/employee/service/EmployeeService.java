package com.kerp.modules.employee.service;

import com.kerp.modules.employee.dto.EmployeeCreateRequest;
import com.kerp.modules.employee.entity.Employee;

import java.util.List;

public interface EmployeeService {

    Employee createEmployee(EmployeeCreateRequest request);

    List<Employee> listEmployees();
}

