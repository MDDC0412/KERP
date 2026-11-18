package com.kerp.modules.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kerp.common.model.BaseEntity;

@TableName("adm_asset_category")
public class AssetCategory extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private Integer depreciationYears;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDepreciationYears() {
        return depreciationYears;
    }

    public void setDepreciationYears(Integer depreciationYears) {
        this.depreciationYears = depreciationYears;
    }
}
