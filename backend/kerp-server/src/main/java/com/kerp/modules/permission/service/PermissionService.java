package com.kerp.modules.permission.service;

import com.kerp.modules.permission.dto.DataScopeCreateRequest;
import com.kerp.modules.permission.dto.PermissionCreateRequest;
import com.kerp.modules.permission.dto.RolePermissionAssignRequest;
import com.kerp.modules.permission.entity.DataScope;
import com.kerp.modules.permission.entity.Permission;
import com.kerp.modules.permission.entity.RolePermission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(PermissionCreateRequest request);

    List<Permission> listPermissions();

    RolePermission assignRolePermission(RolePermissionAssignRequest request);

    List<RolePermission> listRolePermissions();

    DataScope createDataScope(DataScopeCreateRequest request);

    List<DataScope> listDataScopes();
}
