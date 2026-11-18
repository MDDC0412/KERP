package com.kerp.modules.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.common.exception.ForbiddenException;
import com.kerp.common.security.AuthContextHolder;
import com.kerp.common.security.CurrentUser;
import com.kerp.modules.approval.dto.ApprovalRequestCreateRequest;
import com.kerp.modules.approval.dto.ApprovalTaskActionRequest;
import com.kerp.modules.approval.dto.ApprovalTaskItem;
import com.kerp.modules.approval.entity.ApprovalRequest;
import com.kerp.modules.approval.entity.ApprovalTask;
import com.kerp.modules.approval.mapper.ApprovalRequestMapper;
import com.kerp.modules.approval.mapper.ApprovalTaskMapper;
import com.kerp.modules.approval.service.ApprovalService;
import com.kerp.modules.audit.entity.AuditLog;
import com.kerp.modules.audit.mapper.AuditLogMapper;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.mapper.EmployeeMapper;
import com.kerp.modules.notification.entity.NotificationMessage;
import com.kerp.modules.notification.mapper.NotificationMessageMapper;
import com.kerp.modules.role.entity.Role;
import com.kerp.modules.role.mapper.RoleMapper;
import com.kerp.modules.user.entity.User;
import com.kerp.modules.user.entity.UserRole;
import com.kerp.modules.user.mapper.UserMapper;
import com.kerp.modules.user.mapper.UserRoleMapper;
import com.kerp.modules.workflow.entity.ProcessDefinition;
import com.kerp.modules.workflow.mapper.ProcessDefinitionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ApprovalRequestMapper approvalRequestMapper;
    private final ApprovalTaskMapper approvalTaskMapper;
    private final EmployeeMapper employeeMapper;
    private final ProcessDefinitionMapper processDefinitionMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final NotificationMessageMapper notificationMessageMapper;
    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    public ApprovalServiceImpl(ApprovalRequestMapper approvalRequestMapper,
                               ApprovalTaskMapper approvalTaskMapper,
                               EmployeeMapper employeeMapper,
                               ProcessDefinitionMapper processDefinitionMapper,
                               UserMapper userMapper,
                               RoleMapper roleMapper,
                               UserRoleMapper userRoleMapper,
                               NotificationMessageMapper notificationMessageMapper,
                               AuditLogMapper auditLogMapper,
                               ObjectMapper objectMapper) {
        this.approvalRequestMapper = approvalRequestMapper;
        this.approvalTaskMapper = approvalTaskMapper;
        this.employeeMapper = employeeMapper;
        this.processDefinitionMapper = processDefinitionMapper;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.notificationMessageMapper = notificationMessageMapper;
        this.auditLogMapper = auditLogMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ApprovalRequest createApproval(ApprovalRequestCreateRequest request) {
        long duplicateCount = approvalRequestMapper.selectCount(
                new LambdaQueryWrapper<ApprovalRequest>().eq(ApprovalRequest::getRequestNo, request.requestNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Approval request no already exists");
        }

        Employee employee = employeeMapper.selectById(request.employeeId());
        if (employee == null) {
            throw new BusinessException("Employee does not exist");
        }

        ProcessDefinition processDefinition = processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>().eq(ProcessDefinition::getCode, request.processCode())
        );
        if (processDefinition == null) {
            throw new BusinessException("Process definition does not exist");
        }

        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setRequestNo(request.requestNo());
        approvalRequest.setApprovalType(request.approvalType());
        approvalRequest.setProcessCode(request.processCode());
        approvalRequest.setEmployeeId(request.employeeId());
        approvalRequest.setTitle(request.title());
        approvalRequest.setFormDataJson(request.formDataJson());
        approvalRequest.setStatus("SUBMITTED");
        approvalRequest.setSubmittedAt(LocalDateTime.now());
        approvalRequestMapper.insert(approvalRequest);

        createPendingTask(approvalRequest, processDefinition, 0);
        writeAuditLog("APPROVAL", "CREATE", approvalRequest.getRequestNo(), "SUCCESS");
        return approvalRequest;
    }

    @Override
    public List<ApprovalRequest> listApprovals(String approvalType) {
        LambdaQueryWrapper<ApprovalRequest> queryWrapper = new LambdaQueryWrapper<ApprovalRequest>()
                .orderByDesc(ApprovalRequest::getId);
        if (approvalType != null && !approvalType.isBlank()) {
            queryWrapper.eq(ApprovalRequest::getApprovalType, approvalType);
        }
        return approvalRequestMapper.selectList(queryWrapper);
    }

    @Override
    public List<ApprovalTaskItem> listTodoTasks() {
        CurrentUser currentUser = AuthContextHolder.getRequired();
        List<ApprovalTask> tasks = approvalTaskMapper.selectList(
                new LambdaQueryWrapper<ApprovalTask>()
                        .eq(ApprovalTask::getAssigneeUserId, currentUser.userId())
                        .eq(ApprovalTask::getActionStatus, "PENDING")
                        .orderByAsc(ApprovalTask::getNodeOrder)
                        .orderByAsc(ApprovalTask::getId)
        );

        return tasks.stream()
                .map(task -> {
                    ApprovalRequest approvalRequest = approvalRequestMapper.selectById(task.getRequestId());
                    User assignee = userMapper.selectById(task.getAssigneeUserId());
                    return new ApprovalTaskItem(
                            task.getId(),
                            task.getRequestId(),
                            task.getRequestNo(),
                            approvalRequest == null ? "-" : approvalRequest.getApprovalType(),
                            approvalRequest == null ? "-" : approvalRequest.getTitle(),
                            task.getNodeName(),
                            task.getAssigneeUserId(),
                            assignee == null ? "-" : assignee.getDisplayName(),
                            task.getActionStatus(),
                            approvalRequest == null || approvalRequest.getSubmittedAt() == null
                                    ? "-"
                                    : approvalRequest.getSubmittedAt().format(DATE_TIME_FORMATTER)
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public ApprovalRequest handleTask(Long taskId, ApprovalTaskActionRequest request) {
        CurrentUser currentUser = AuthContextHolder.getRequired();
        ApprovalTask approvalTask = approvalTaskMapper.selectById(taskId);
        if (approvalTask == null) {
            throw new BusinessException("Approval task does not exist");
        }
        if (!"PENDING".equalsIgnoreCase(approvalTask.getActionStatus())) {
            throw new BusinessException("Approval task has already been processed");
        }
        if (!currentUser.hasRole("ADMIN") && !currentUser.userId().equals(approvalTask.getAssigneeUserId())) {
            throw new ForbiddenException("Current account cannot process this approval task");
        }

        String action = request.action().trim().toUpperCase(Locale.ROOT);
        if (!List.of("APPROVE", "REJECT").contains(action)) {
            throw new BusinessException("Unsupported approval action");
        }

        ApprovalRequest approvalRequest = approvalRequestMapper.selectById(approvalTask.getRequestId());
        if (approvalRequest == null) {
            throw new BusinessException("Approval request does not exist");
        }

        approvalTask.setActionStatus(action);
        approvalTask.setActionComment(request.comment());
        approvalTask.setActionAt(LocalDateTime.now());
        approvalTaskMapper.updateById(approvalTask);

        if ("REJECT".equals(action)) {
            approvalRequest.setStatus("REJECTED");
            approvalRequest.setApprovedAt(null);
            approvalRequestMapper.updateById(approvalRequest);
            notifyApplicant(approvalRequest, "审批已拒绝", "申请单 " + approvalRequest.getRequestNo() + " 已被拒绝。");
            writeAuditLog("APPROVAL", "REJECT", approvalRequest.getRequestNo(), "SUCCESS");
            return approvalRequest;
        }

        ProcessDefinition processDefinition = processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>().eq(ProcessDefinition::getCode, approvalRequest.getProcessCode())
        );
        List<String> nodes = processDefinition == null ? List.of("manager") : parseNodes(processDefinition.getConfigJson());

        if (approvalTask.getNodeOrder() + 1 < nodes.size()) {
            createPendingTask(approvalRequest, processDefinition, approvalTask.getNodeOrder() + 1);
            approvalRequest.setStatus("IN_REVIEW");
            approvalRequestMapper.updateById(approvalRequest);
        } else {
            approvalRequest.setStatus("APPROVED");
            approvalRequest.setApprovedAt(LocalDateTime.now());
            approvalRequestMapper.updateById(approvalRequest);
            notifyApplicant(approvalRequest, "审批已通过", "申请单 " + approvalRequest.getRequestNo() + " 已审批通过。");
        }

        writeAuditLog("APPROVAL", "APPROVE", approvalRequest.getRequestNo(), "SUCCESS");
        return approvalRequest;
    }

    private void createPendingTask(ApprovalRequest approvalRequest, ProcessDefinition processDefinition, int nodeOrder) {
        List<String> nodes = processDefinition == null ? List.of("manager") : parseNodes(processDefinition.getConfigJson());
        String nodeName = nodeOrder < nodes.size() ? nodes.get(nodeOrder) : "manager";
        Long assigneeUserId = resolveAssigneeUserId(nodeName);
        if (assigneeUserId == null) {
            throw new BusinessException("No available approver account was found for node " + nodeName);
        }

        ApprovalTask approvalTask = new ApprovalTask();
        approvalTask.setRequestId(approvalRequest.getId());
        approvalTask.setRequestNo(approvalRequest.getRequestNo());
        approvalTask.setNodeName(nodeName);
        approvalTask.setNodeOrder(nodeOrder);
        approvalTask.setAssigneeUserId(assigneeUserId);
        approvalTask.setActionStatus("PENDING");
        approvalTaskMapper.insert(approvalTask);
    }

    private List<String> parseNodes(String configJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(configJson);
            JsonNode nodesNode = rootNode.path("nodes");
            if (!nodesNode.isArray() || nodesNode.isEmpty()) {
                return List.of("manager");
            }
            List<String> nodes = new ArrayList<>();
            nodesNode.forEach(item -> nodes.add(item.asText("manager")));
            return nodes;
        } catch (Exception exception) {
            return List.of("manager");
        }
    }

    private Long resolveAssigneeUserId(String nodeName) {
        String normalized = nodeName == null ? "" : nodeName.toLowerCase(Locale.ROOT);
        if (normalized.contains("hr")) {
            Long assigneeUserId = findFirstUserByRoleCode("HR_MANAGER");
            if (assigneeUserId != null) {
                return assigneeUserId;
            }
        }
        if (normalized.contains("manager")) {
            Long assigneeUserId = findFirstUserByRoleCode("DEPT_MANAGER");
            if (assigneeUserId != null) {
                return assigneeUserId;
            }
        }
        if (normalized.contains("admin")) {
            Long assigneeUserId = findFirstUserByRoleCode("ADMIN");
            if (assigneeUserId != null) {
                return assigneeUserId;
            }
        }
        return findFirstUserByRoleCode("ADMIN");
    }

    private Long findFirstUserByRoleCode(String roleCode) {
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, roleCode));
        if (role == null) {
            return null;
        }

        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, role.getId())
        );
        for (UserRole userRole : userRoles) {
            User user = userMapper.selectById(userRole.getUserId());
            if (user != null && Integer.valueOf(1).equals(user.getEnabled())) {
                return user.getId();
            }
        }
        return null;
    }

    private void notifyApplicant(ApprovalRequest approvalRequest, String title, String content) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(title);
        notificationMessage.setContent(content);
        notificationMessage.setTargetType("EMPLOYEE");
        notificationMessage.setTargetValue(String.valueOf(approvalRequest.getEmployeeId()));
        notificationMessage.setChannel("IN_APP");
        notificationMessage.setReadStatus(0);
        notificationMessageMapper.insert(notificationMessage);
    }

    private void writeAuditLog(String moduleName, String actionName, String targetRef, String resultStatus) {
        CurrentUser currentUser = AuthContextHolder.getRequired();
        AuditLog auditLog = new AuditLog();
        auditLog.setModuleName(moduleName);
        auditLog.setActionName(actionName);
        auditLog.setOperatorName(currentUser.username());
        auditLog.setTargetRef(targetRef);
        auditLog.setResultStatus(resultStatus);
        auditLog.setDetail("Processed by " + currentUser.displayName());
        auditLogMapper.insert(auditLog);
    }
}
