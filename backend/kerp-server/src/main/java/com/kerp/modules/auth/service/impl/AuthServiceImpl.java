package com.kerp.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.auth.dto.LoginRequest;
import com.kerp.modules.auth.dto.LoginResponse;
import com.kerp.modules.auth.service.AuthService;
import com.kerp.modules.permission.entity.Permission;
import com.kerp.modules.permission.entity.RolePermission;
import com.kerp.modules.permission.mapper.PermissionMapper;
import com.kerp.modules.permission.mapper.RolePermissionMapper;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.mapper.RoleMapper;
import com.kerp.modules.user.entity.User;
import com.kerp.modules.user.entity.UserRole;
import com.kerp.modules.user.mapper.UserMapper;
import com.kerp.modules.user.mapper.UserRoleMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UserMapper userMapper,
                           UserRoleMapper userRoleMapper,
                           RoleMapper roleMapper,
                           RolePermissionMapper rolePermissionMapper,
                           PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.username())
        );
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid username or password");
        }
        if (!Integer.valueOf(1).equals(user.getEnabled())) {
            throw new BusinessException("Current account is disabled");
        }

        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId())
        );
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();

        List<Role> roles = roleIds.isEmpty()
                ? List.of()
                : roleMapper.selectBatchIds(roleIds);
        Map<Long, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, item -> item));

        List<RolePermission> rolePermissions = roleIds.isEmpty()
                ? List.of()
                : rolePermissionMapper.selectList(
                        new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds)
                );
        Set<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());
        List<Permission> permissions = permissionIds.isEmpty()
                ? List.of()
                : permissionMapper.selectBatchIds(permissionIds);

        return new LoginResponse(
                "local-" + user.getId() + "-" + LocalDateTime.now(),
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                roleIds.stream()
                        .map(roleMap::get)
                        .filter(item -> item != null)
                        .map(Role::getCode)
                        .toList(),
                permissions.stream().map(Permission::getCode).sorted().toList()
        );
    }
}
