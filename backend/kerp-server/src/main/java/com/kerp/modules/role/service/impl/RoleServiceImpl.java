package com.kerp.modules.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.role.dto.RoleCreateRequest;
import com.kerp.modules.role.dto.RoleListItem;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.mapper.RoleMapper;
import com.kerp.modules.role.service.RoleService;
import com.kerp.modules.user.entity.UserRole;
import com.kerp.modules.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleServiceImpl(RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public Role createRole(RoleCreateRequest request) {
        long codeCount = roleMapper.selectCount(
                new LambdaQueryWrapper<Role>().eq(Role::getCode, request.code())
        );
        if (codeCount > 0) {
            throw new BusinessException("角色编码已存在");
        }

        long nameCount = roleMapper.selectCount(
                new LambdaQueryWrapper<Role>().eq(Role::getName, request.name())
        );
        if (nameCount > 0) {
            throw new BusinessException("角色名称已存在");
        }

        Role role = new Role();
        role.setCode(request.code());
        role.setName(request.name());
        role.setDescription(request.description());
        role.setEnabled(1);
        roleMapper.insert(role);
        return role;
    }

    @Override
    public List<RoleListItem> listRoles() {
        List<Role> roles = roleMapper.selectList(
                new LambdaQueryWrapper<Role>().orderByAsc(Role::getId)
        );

        return roles.stream()
                .map(role -> new RoleListItem(
                        role.getId(),
                        role.getCode(),
                        role.getName(),
                        role.getDescription(),
                        role.getEnabled(),
                        userRoleMapper.selectCount(
                                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, role.getId())
                        )
                ))
                .toList();
    }
}

