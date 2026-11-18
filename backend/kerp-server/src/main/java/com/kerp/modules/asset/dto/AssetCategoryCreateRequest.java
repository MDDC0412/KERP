package com.kerp.modules.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssetCategoryCreateRequest(
        @NotBlank(message = "Category code is required")
        @Size(max = 64, message = "Category code is too long")
        String code,
        @NotBlank(message = "Category name is required")
        @Size(max = 128, message = "Category name is too long")
        String name,
        Integer depreciationYears
) {
}
