package com.kerp.modules.role.service;

import com.kerp.modules.role.dto.RoleCreateRequest;
import com.kerp.modules.role.dto.RoleListItem;
import com.kerp.modules.role.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(RoleCreateRequest request);

    List<RoleListItem> listRoles();
}

