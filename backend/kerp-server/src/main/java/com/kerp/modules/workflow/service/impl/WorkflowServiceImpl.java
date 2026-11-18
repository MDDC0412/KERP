package com.kerp.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.workflow.dto.FormDefinitionCreateRequest;
import com.kerp.modules.workflow.dto.ProcessDefinitionCreateRequest;
import com.kerp.modules.workflow.entity.FormDefinition;
import com.kerp.modules.workflow.entity.ProcessDefinition;
import com.kerp.modules.workflow.mapper.FormDefinitionMapper;
import com.kerp.modules.workflow.mapper.ProcessDefinitionMapper;
import com.kerp.modules.workflow.service.WorkflowService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final FormDefinitionMapper formDefinitionMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;

    public WorkflowServiceImpl(FormDefinitionMapper formDefinitionMapper,
                               ProcessDefinitionMapper processDefinitionMapper) {
        this.formDefinitionMapper = formDefinitionMapper;
        this.processDefinitionMapper = processDefinitionMapper;
    }

    @Override
    public FormDefinition createForm(FormDefinitionCreateRequest request) {
        long duplicateCount = formDefinitionMapper.selectCount(
                new LambdaQueryWrapper<FormDefinition>().eq(FormDefinition::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Form code already exists");
        }

        FormDefinition formDefinition = new FormDefinition();
        formDefinition.setCode(request.code());
        formDefinition.setName(request.name());
        formDefinition.setCategory(request.category());
        formDefinition.setVersionNo(request.versionNo() == null ? 1 : request.versionNo());
        formDefinition.setSchemaJson(request.schemaJson());
        formDefinition.setEnabled(1);
        formDefinitionMapper.insert(formDefinition);
        return formDefinition;
    }

    @Override
    public List<FormDefinition> listForms() {
        return formDefinitionMapper.selectList(
                new LambdaQueryWrapper<FormDefinition>()
                        .orderByAsc(FormDefinition::getCategory)
                        .orderByAsc(FormDefinition::getCode)
        );
    }

    @Override
    public ProcessDefinition createProcess(ProcessDefinitionCreateRequest request) {
        long duplicateCount = processDefinitionMapper.selectCount(
                new LambdaQueryWrapper<ProcessDefinition>().eq(ProcessDefinition::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Process code already exists");
        }

        FormDefinition formDefinition = formDefinitionMapper.selectOne(
                new LambdaQueryWrapper<FormDefinition>().eq(FormDefinition::getCode, request.formCode())
        );
        if (formDefinition == null) {
            throw new BusinessException("Form definition does not exist");
        }

        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setCode(request.code());
        processDefinition.setName(request.name());
        processDefinition.setFormCode(request.formCode());
        processDefinition.setApprovalType(request.approvalType());
        processDefinition.setConfigJson(request.configJson());
        processDefinition.setEnabled(1);
        processDefinitionMapper.insert(processDefinition);
        return processDefinition;
    }

    @Override
    public List<ProcessDefinition> listProcesses() {
        return processDefinitionMapper.selectList(
                new LambdaQueryWrapper<ProcessDefinition>()
                        .orderByAsc(ProcessDefinition::getApprovalType)
                        .orderByAsc(ProcessDefinition::getCode)
        );
    }
}
