package com.kerp.modules.integration.dto;

import java.util.List;

public record IntegrationOverviewResponse(
        List<String> architecturePrinciples,
        List<String> noPreparationNeeded,
        List<IntegrationProviderItem> providers
) {
}
