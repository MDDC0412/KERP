package com.kerp.modules.integration.dto;

import java.util.List;

public record IntegrationProviderItem(
        String code,
        String name,
        String category,
        String status,
        String summary,
        List<String> enabledCapabilities,
        List<String> reservedCapabilities,
        List<String> futureRequirements
) {
}
