package com.kerp.modules.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.permission.dto.DataScopeCreateRequest;
import com.kerp.modules.permission.dto.PermissionCreateRequest;
import com.kerp.modules.permission.dto.RolePermissionAssignRequest;
import com.kerp.modules.permission.entity.DataScope;
import com.kerp.modules.permission.entity.Permission;
import com.kerp.modules.permission.entity.RolePermission;
import com.kerp.modules.permission.mapper.DataScopeMapper;
import com.kerp.modules.permission.mapper.PermissionMapper;
import com.kerp.modules.permission.mapper.RolePermissionMapper;
import com.kerp.modules.permission.service.PermissionService;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final DataScopeMapper dataScopeMapper;
    private final RoleMapper roleMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper,
                                 RolePermissionMapper rolePermissionMapper,
                                 DataScopeMapper dataScopeMapper,
                                 RoleMapper roleMapper) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.dataScopeMapper = dataScopeMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public Permission createPermission(PermissionCreateRequest request) {
        long duplicateCount = permissionMapper.selectCount(
                new LambdaQueryWrapper<Permission>().eq(Permission::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Permission code already exists");
        }

        Permission permission = new Permission();
        permission.setCode(request.code());
        permission.setName(request.name());
        permission.setModuleName(request.moduleName());
        permission.setPermissionType(request.permissionType());
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    public List<Permission> listPermissions() {
        return permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .orderByAsc(Permission::getModuleName)
                        .orderByAsc(Permission::getCode)
        );
    }

    @Override
    public RolePermission assignRolePermission(RolePermissionAssignRequest request) {
        Role role = roleMapper.selectById(request.roleId());
        if (role == null) {
            throw new BusinessException("Role does not exist");
        }
        Permission permission = permissionMapper.selectById(request.permissionId());
        if (permission == null) {
            throw new BusinessException("Permission does not exist");
        }
        long duplicateCount = rolePermissionMapper.selectCount(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, request.roleId())
                        .eq(RolePermission::getPermissionId, request.permissionId())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Role permission already exists");
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(request.roleId());
        rolePermission.setPermissionId(request.permissionId());
        rolePermissionMapper.insert(rolePermission);
        return rolePermission;
    }

    @Override
    public List<RolePermission> listRolePermissions() {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .orderByAsc(RolePermission::getRoleId)
                        .orderByAsc(RolePermission::getPermissionId)
        );
    }

    @Override
    public DataScope createDataScope(DataScopeCreateRequest request) {
        Role role = roleMapper.selectById(request.roleId());
        if (role == null) {
            throw new BusinessException("Role does not exist");
        }
        long duplicateCount = dataScopeMapper.selectCount(
                new LambdaQueryWrapper<DataScope>()
                        .eq(DataScope::getRoleId, request.roleId())
                        .eq(DataScope::getScopeCode, request.scopeCode())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Data scope already exists");
        }

        DataScope dataScope = new DataScope();
        dataScope.setRoleId(request.roleId());
        dataScope.setScopeCode(request.scopeCode());
        dataScope.setScopeName(request.scopeName());
        dataScope.setScopeRule(request.scopeRule());
        dataScopeMapper.insert(dataScope);
        return dataScope;
    }

    @Override
    public List<DataScope> listDataScopes() {
        return dataScopeMapper.selectList(
                new LambdaQueryWrapper<DataScope>()
                        .orderByAsc(DataScope::getRoleId)
                        .orderByAsc(DataScope::getScopeCode)
        );
    }
}
