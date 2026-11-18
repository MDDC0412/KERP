package com.kerp.modules.approval.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.common.security.RequirePermission;
import com.kerp.modules.approval.dto.ApprovalRequestCreateRequest;
import com.kerp.modules.approval.dto.ApprovalTaskActionRequest;
import com.kerp.modules.approval.dto.ApprovalTaskItem;
import com.kerp.modules.approval.entity.ApprovalRequest;
import com.kerp.modules.approval.service.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ApiResponse<List<ApprovalRequest>> listApprovals(@RequestParam(required = false) String type) {
        return ApiResponse.success(approvalService.listApprovals(type));
    }

    @PostMapping
    public ApiResponse<ApprovalRequest> createApproval(@Valid @RequestBody ApprovalRequestCreateRequest request) {
        return ApiResponse.success(approvalService.createApproval(request));
    }

    @GetMapping("/tasks/todo")
    @RequirePermission("APPROVAL_APPROVE")
    public ApiResponse<List<ApprovalTaskItem>> listTodoTasks() {
        return ApiResponse.success(approvalService.listTodoTasks());
    }

    @PostMapping("/tasks/{taskId}/actions")
    @RequirePermission("APPROVAL_APPROVE")
    public ApiResponse<ApprovalRequest> handleTask(@PathVariable Long taskId,
                                                   @Valid @RequestBody ApprovalTaskActionRequest request) {
        return ApiResponse.success(approvalService.handleTask(taskId, request));
    }
}
