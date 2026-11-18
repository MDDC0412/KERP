package com.kerp.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.organization.entity.Department;
import com.kerp.modules.organization.mapper.DepartmentMapper;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.mapper.RoleMapper;
import com.kerp.modules.user.dto.UserCreateRequest;
import com.kerp.modules.user.dto.UserListItem;
import com.kerp.modules.user.entity.User;
import com.kerp.modules.user.entity.UserRole;
import com.kerp.modules.user.mapper.UserMapper;
import com.kerp.modules.user.mapper.UserRoleMapper;
import com.kerp.modules.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final DepartmentMapper departmentMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper,
                           UserRoleMapper userRoleMapper,
                           RoleMapper roleMapper,
                           DepartmentMapper departmentMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    @Transactional
    public User createUser(UserCreateRequest request) {
        long duplicateCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.username())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("登录名已存在");
        }

        Department department = null;
        if (request.departmentId() != null) {
            department = departmentMapper.selectById(request.departmentId());
            if (department == null) {
                throw new BusinessException("所属部门不存在");
            }
        }

        List<Role> roles = roleMapper.selectBatchIds(request.roleIds());
        if (roles.size() != request.roleIds().size()) {
            throw new BusinessException("提交的角色中存在无效角色");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setDisplayName(request.displayName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setDepartmentId(department == null ? null : department.getId());
        user.setEnabled(1);
        user.setPasswordHash(passwordEncoder.encode(
                request.initialPassword() == null || request.initialPassword().isBlank()
                        ? "ChangeMe123!"
                        : request.initialPassword()
        ));
        userMapper.insert(user);

        for (Long roleId : request.roleIds()) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }

        return user;
    }

    @Override
    public List<UserListItem> listUsers() {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().orderByDesc(User::getId)
        );
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, List<Long>> roleIdsByUser = userRoles.stream()
                .collect(Collectors.groupingBy(
                        UserRole::getUserId,
                        Collectors.mapping(UserRole::getRoleId, Collectors.toList())
                ));

        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getId));
        Map<Long, String> roleNameMap = roles.stream()
                .collect(Collectors.toMap(Role::getId, Role::getName));

        Map<Long, String> departmentNameMap = new HashMap<>();
        departmentMapper.selectList(new LambdaQueryWrapper<Department>().orderByAsc(Department::getId))
                .forEach(item -> departmentNameMap.put(item.getId(), item.getName()));

        return users.stream()
                .map(user -> {
                    List<String> roleNames = roleIdsByUser.getOrDefault(user.getId(), List.of()).stream()
                            .map(roleNameMap::get)
                            .filter(Objects::nonNull)
                            .toList();
                    return new UserListItem(
                            user.getId(),
                            user.getUsername(),
                            user.getDisplayName(),
                            departmentNameMap.getOrDefault(user.getDepartmentId(), "未分配部门"),
                            user.getEmail(),
                            user.getPhone(),
                            user.getEnabled(),
                            roleNames
                    );
                })
                .toList();
    }
}

