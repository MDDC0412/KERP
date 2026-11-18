package com.kerp.modules.audit.service;

import com.kerp.modules.audit.dto.AuditLogCreateRequest;
import com.kerp.modules.audit.entity.AuditLog;

import java.util.List;

public interface AuditService {

    AuditLog createAuditLog(AuditLogCreateRequest request);

    List<AuditLog> listAuditLogs();
}
