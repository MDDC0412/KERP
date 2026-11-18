package com.kerp.modules.audit.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.audit.dto.AuditLogCreateRequest;
import com.kerp.modules.audit.entity.AuditLog;
import com.kerp.modules.audit.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ApiResponse<List<AuditLog>> listAuditLogs() {
        return ApiResponse.success(auditService.listAuditLogs());
    }

    @PostMapping
    public ApiResponse<AuditLog> createAuditLog(@Valid @RequestBody AuditLogCreateRequest request) {
        return ApiResponse.success(auditService.createAuditLog(request));
    }
}
