package com.kerp.modules.dashboard.dto;

import java.util.List;

public record DashboardOverviewResponse(
        List<DashboardMetric> metrics,
        List<DistributionItem> departmentDistribution,
        List<DistributionItem> roleDistribution,
        List<RecentEmployeeItem> recentEmployees
) {
}

