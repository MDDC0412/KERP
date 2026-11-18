package com.kerp.modules.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.modules.audit.dto.AuditLogCreateRequest;
import com.kerp.modules.audit.entity.AuditLog;
import com.kerp.modules.audit.mapper.AuditLogMapper;
import com.kerp.modules.audit.service.AuditService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogMapper auditLogMapper;

    public AuditServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public AuditLog createAuditLog(AuditLogCreateRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setModuleName(request.moduleName());
        auditLog.setActionName(request.actionName());
        auditLog.setOperatorName(request.operatorName());
        auditLog.setTargetRef(request.targetRef());
        auditLog.setResultStatus(request.resultStatus());
        auditLog.setDetail(request.detail());
        auditLogMapper.insert(auditLog);
        return auditLog;
    }

    @Override
    public List<AuditLog> listAuditLogs() {
        return auditLogMapper.selectList(new LambdaQueryWrapper<AuditLog>().orderByDesc(AuditLog::getId));
    }
}
