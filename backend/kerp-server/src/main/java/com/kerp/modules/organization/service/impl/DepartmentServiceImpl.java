package com.kerp.modules.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.organization.dto.DepartmentCreateRequest;
import com.kerp.modules.organization.entity.Department;
import com.kerp.modules.organization.mapper.DepartmentMapper;
import com.kerp.modules.organization.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @Override
    public Department createDepartment(DepartmentCreateRequest request) {
        long duplicateCount = departmentMapper.selectCount(
                new LambdaQueryWrapper<Department>().eq(Department::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("部门编码已存在");
        }

        if (request.parentId() != null) {
            Department parent = departmentMapper.selectById(request.parentId());
            if (parent == null) {
                throw new BusinessException("上级部门不存在");
            }
        }

        Department department = new Department();
        department.setCode(request.code());
        department.setName(request.name());
        department.setParentId(request.parentId());
        department.setSort(request.sort() == null ? 100 : request.sort());
        department.setEnabled(1);
        departmentMapper.insert(department);
        return department;
    }

    @Override
    public List<Department> listDepartments() {
        return departmentMapper.selectList(
                new LambdaQueryWrapper<Department>()
                        .orderByAsc(Department::getSort)
                        .orderByAsc(Department::getId)
        );
    }
}

