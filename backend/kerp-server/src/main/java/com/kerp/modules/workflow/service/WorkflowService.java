package com.kerp.modules.workflow.service;

import com.kerp.modules.workflow.dto.FormDefinitionCreateRequest;
import com.kerp.modules.workflow.dto.ProcessDefinitionCreateRequest;
import com.kerp.modules.workflow.entity.FormDefinition;
import com.kerp.modules.workflow.entity.ProcessDefinition;

import java.util.List;

public interface WorkflowService {

    FormDefinition createForm(FormDefinitionCreateRequest request);

    List<FormDefinition> listForms();

    ProcessDefinition createProcess(ProcessDefinitionCreateRequest request);

    List<ProcessDefinition> listProcesses();
}
