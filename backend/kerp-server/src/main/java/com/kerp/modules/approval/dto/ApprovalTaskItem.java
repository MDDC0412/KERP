package com.kerp.modules.approval.dto;

public record ApprovalTaskItem(
        Long id,
        Long requestId,
        String requestNo,
        String approvalType,
        String title,
        String nodeName,
        Long assigneeUserId,
        String assigneeDisplayName,
        String actionStatus,
        String submittedAt
) {
}
