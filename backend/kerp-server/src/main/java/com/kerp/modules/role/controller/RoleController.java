package com.kerp.modules.role.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.role.dto.RoleCreateRequest;
import com.kerp.modules.role.dto.RoleListItem;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<List<RoleListItem>> listRoles() {
        return ApiResponse.success(roleService.listRoles());
    }

    @PostMapping
    public ApiResponse<Role> createRole(@Valid @RequestBody RoleCreateRequest request) {
        return ApiResponse.success(roleService.createRole(request));
    }
}

