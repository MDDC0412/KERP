package com.kerp.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kerp.modules.workflow.entity.ProcessDefinition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessDefinitionMapper extends BaseMapper<ProcessDefinition> {
}
