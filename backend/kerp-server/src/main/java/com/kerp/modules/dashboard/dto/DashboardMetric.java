package com.kerp.modules.dashboard.dto;

public record DashboardMetric(
        String label,
        String value,
        String accent,
        String hint
) {
}

