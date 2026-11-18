package com.kerp.modules.approval.service;

import com.kerp.modules.approval.dto.ApprovalRequestCreateRequest;
import com.kerp.modules.approval.dto.ApprovalTaskActionRequest;
import com.kerp.modules.approval.dto.ApprovalTaskItem;
import com.kerp.modules.approval.entity.ApprovalRequest;

import java.util.List;

public interface ApprovalService {

    ApprovalRequest createApproval(ApprovalRequestCreateRequest request);

    List<ApprovalRequest> listApprovals(String approvalType);

    List<ApprovalTaskItem> listTodoTasks();

    ApprovalRequest handleTask(Long taskId, ApprovalTaskActionRequest request);
}
