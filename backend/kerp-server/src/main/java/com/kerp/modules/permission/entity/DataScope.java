package com.kerp.modules.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kerp.common.model.BaseEntity;

@TableName("sys_data_scope")
public class DataScope extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private String scopeCode;
    private String scopeName;
    private String scopeRule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getScopeCode() {
        return scopeCode;
    }

    public void setScopeCode(String scopeCode) {
        this.scopeCode = scopeCode;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getScopeRule() {
        return scopeRule;
    }

    public void setScopeRule(String scopeRule) {
        this.scopeRule = scopeRule;
    }
}
