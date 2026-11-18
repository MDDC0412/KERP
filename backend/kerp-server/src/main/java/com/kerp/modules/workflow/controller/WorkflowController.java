package com.kerp.modules.workflow.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.common.security.RequirePermission;
import com.kerp.modules.workflow.dto.FormDefinitionCreateRequest;
import com.kerp.modules.workflow.dto.ProcessDefinitionCreateRequest;
import com.kerp.modules.workflow.entity.FormDefinition;
import com.kerp.modules.workflow.entity.ProcessDefinition;
import com.kerp.modules.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/forms")
    public ApiResponse<List<FormDefinition>> listForms() {
        return ApiResponse.success(workflowService.listForms());
    }

    @PostMapping("/forms")
    @RequirePermission("WORKFLOW_MANAGE")
    public ApiResponse<FormDefinition> createForm(@Valid @RequestBody FormDefinitionCreateRequest request) {
        return ApiResponse.success(workflowService.createForm(request));
    }

    @GetMapping("/workflows")
    public ApiResponse<List<ProcessDefinition>> listProcesses() {
        return ApiResponse.success(workflowService.listProcesses());
    }

    @PostMapping("/workflows")
    @RequirePermission("WORKFLOW_MANAGE")
    public ApiResponse<ProcessDefinition> createProcess(@Valid @RequestBody ProcessDefinitionCreateRequest request) {
        return ApiResponse.success(workflowService.createProcess(request));
    }
}
