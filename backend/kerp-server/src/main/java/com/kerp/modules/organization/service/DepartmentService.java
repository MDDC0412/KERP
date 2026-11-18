package com.kerp.modules.organization.service;

import com.kerp.modules.organization.dto.DepartmentCreateRequest;
import com.kerp.modules.organization.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(DepartmentCreateRequest request);

    List<Department> listDepartments();
}

