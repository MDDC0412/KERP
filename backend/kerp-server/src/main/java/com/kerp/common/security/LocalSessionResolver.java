package com.kerp.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.UnauthorizedException;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class LocalSessionResolver {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("^local-(\\d+)-.+$");

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public LocalSessionResolver(UserMapper userMapper,
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

    public CurrentUser resolve(String rawAuthorization) {
        if (rawAuthorization == null || rawAuthorization.isBlank()) {
            throw new UnauthorizedException("Missing Authorization header");
        }
        String token = rawAuthorization.startsWith("Bearer ")
                ? rawAuthorization.substring(7).trim()
                : rawAuthorization.trim();
        Matcher matcher = TOKEN_PATTERN.matcher(token);
        if (!matcher.matches()) {
            throw new UnauthorizedException("Invalid local session token");
        }

        Long userId = Long.valueOf(matcher.group(1));
        User user = userMapper.selectById(userId);
        if (user == null || !Integer.valueOf(1).equals(user.getEnabled())) {
            throw new UnauthorizedException("Current user is unavailable");
        }

        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();
        List<Role> roles = roleIds.isEmpty() ? List.of() : roleMapper.selectBatchIds(roleIds);
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

        return new CurrentUser(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                roleIds.stream()
                        .map(roleMap::get)
                        .filter(item -> item != null)
                        .map(Role::getCode)
                        .toList(),
                permissions.stream()
                        .map(Permission::getCode)
                        .sorted()
                        .toList()
        );
    }
}
