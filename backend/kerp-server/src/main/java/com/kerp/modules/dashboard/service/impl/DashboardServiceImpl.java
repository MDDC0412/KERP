package com.kerp.modules.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.modules.dashboard.dto.DashboardMetric;
import com.kerp.modules.dashboard.dto.DashboardOverviewResponse;
import com.kerp.modules.dashboard.dto.DistributionItem;
import com.kerp.modules.dashboard.dto.RecentEmployeeItem;
import com.kerp.modules.dashboard.service.DashboardService;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.mapper.EmployeeMapper;
import com.kerp.modules.organization.entity.Department;
import com.kerp.modules.organization.mapper.DepartmentMapper;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.mapper.RoleMapper;
import com.kerp.modules.user.entity.UserRole;
import com.kerp.modules.user.mapper.UserMapper;
import com.kerp.modules.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public DashboardServiceImpl(DepartmentMapper departmentMapper,
                                EmployeeMapper employeeMapper,
                                UserMapper userMapper,
                                RoleMapper roleMapper,
                                UserRoleMapper userRoleMapper) {
        this.departmentMapper = departmentMapper;
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public DashboardOverviewResponse getOverview() {
        long departmentCount = departmentMapper.selectCount(new LambdaQueryWrapper<>());
        long employeeCount = employeeMapper.selectCount(new LambdaQueryWrapper<>());
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<>());
        long roleCount = roleMapper.selectCount(new LambdaQueryWrapper<>());

        List<Department> departments = departmentMapper.selectList(
                new LambdaQueryWrapper<Department>().orderByAsc(Department::getSort).orderByAsc(Department::getId)
        );
        List<Employee> employees = employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>().orderByDesc(Employee::getId)
        );
        Map<Long, String> departmentNameMap = departments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));

        List<DashboardMetric> metrics = List.of(
                new DashboardMetric("组织单元", String.valueOf(departmentCount), "amber", "部门与组织树节点"),
                new DashboardMetric("员工档案", String.valueOf(employeeCount), "teal", "已入库人员记录"),
                new DashboardMetric("系统账号", String.valueOf(userCount), "blue", "可登录后台的管理账号"),
                new DashboardMetric("角色模板", String.valueOf(roleCount), "rose", "权限角色与职责模板")
        );

        List<DistributionItem> departmentDistribution = departments.stream()
                .map(department -> new DistributionItem(
                        department.getName(),
                        employees.stream().filter(item -> department.getId().equals(item.getDepartmentId())).count()
                ))
                .toList();

        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getId));
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<>());
        List<DistributionItem> roleDistribution = roles.stream()
                .map(role -> new DistributionItem(
                        role.getName(),
                        userRoles.stream().filter(item -> role.getId().equals(item.getRoleId())).count()
                ))
                .toList();

        List<RecentEmployeeItem> recentEmployees = employees.stream()
                .limit(6)
                .map(item -> new RecentEmployeeItem(
                        item.getEmployeeNo(),
                        item.getFullName(),
                        departmentNameMap.getOrDefault(item.getDepartmentId(), "未分配部门"),
                        item.getHireDate() == null ? "-" : item.getHireDate().toString()
                ))
                .toList();

        return new DashboardOverviewResponse(metrics, departmentDistribution, roleDistribution, recentEmployees);
    }
}

