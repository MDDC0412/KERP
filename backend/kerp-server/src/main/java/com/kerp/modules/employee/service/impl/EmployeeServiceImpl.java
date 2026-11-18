package com.kerp.modules.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.employee.dto.EmployeeCreateRequest;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.mapper.EmployeeMapper;
import com.kerp.modules.employee.service.EmployeeService;
import com.kerp.modules.organization.entity.Department;
import com.kerp.modules.organization.mapper.DepartmentMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper, DepartmentMapper departmentMapper) {
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public Employee createEmployee(EmployeeCreateRequest request) {
        long duplicateCount = employeeMapper.selectCount(
                new LambdaQueryWrapper<Employee>().eq(Employee::getEmployeeNo, request.employeeNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("员工编号已存在");
        }

        Department department = departmentMapper.selectById(request.departmentId());
        if (department == null) {
            throw new BusinessException("所属部门不存在");
        }

        Employee employee = new Employee();
        employee.setEmployeeNo(request.employeeNo());
        employee.setFullName(request.fullName());
        employee.setDepartmentId(request.departmentId());
        employee.setPhone(request.phone());
        employee.setEmail(request.email());
        employee.setHireDate(request.hireDate() == null ? LocalDate.now() : request.hireDate());
        employee.setStatus(1);
        employeeMapper.insert(employee);
        return employee;
    }

    @Override
    public List<Employee> listEmployees() {
        return employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .orderByDesc(Employee::getId)
        );
    }
}

