package com.kerp.modules.integration.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.integration.dto.IntegrationOverviewResponse;
import com.kerp.modules.integration.service.IntegrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping("/reservations")
    public ApiResponse<IntegrationOverviewResponse> getOverview() {
        return ApiResponse.success(integrationService.getOverview());
    }
}
