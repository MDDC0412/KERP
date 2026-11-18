package com.kerp.modules.permission.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.common.security.RequireRole;
import com.kerp.modules.permission.dto.DataScopeCreateRequest;
import com.kerp.modules.permission.dto.PermissionCreateRequest;
import com.kerp.modules.permission.dto.RolePermissionAssignRequest;
import com.kerp.modules.permission.entity.DataScope;
import com.kerp.modules.permission.entity.Permission;
import com.kerp.modules.permission.entity.RolePermission;
import com.kerp.modules.permission.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequireRole("ADMIN")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions")
    public ApiResponse<List<Permission>> listPermissions() {
        return ApiResponse.success(permissionService.listPermissions());
    }

    @PostMapping("/permissions")
    public ApiResponse<Permission> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        return ApiResponse.success(permissionService.createPermission(request));
    }

    @GetMapping("/role-permissions")
    public ApiResponse<List<RolePermission>> listRolePermissions() {
        return ApiResponse.success(permissionService.listRolePermissions());
    }

    @PostMapping("/role-permissions")
    public ApiResponse<RolePermission> assignRolePermission(@Valid @RequestBody RolePermissionAssignRequest request) {
        return ApiResponse.success(permissionService.assignRolePermission(request));
    }

    @GetMapping("/data-scopes")
    public ApiResponse<List<DataScope>> listDataScopes() {
        return ApiResponse.success(permissionService.listDataScopes());
    }

    @PostMapping("/data-scopes")
    public ApiResponse<DataScope> createDataScope(@Valid @RequestBody DataScopeCreateRequest request) {
        return ApiResponse.success(permissionService.createDataScope(request));
    }
}
