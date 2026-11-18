package com.kerp.modules.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AssetCreateRequest(
        @NotBlank(message = "Asset no is required")
        @Size(max = 64, message = "Asset no is too long")
        String assetNo,
        @NotNull(message = "Category id is required")
        Long categoryId,
        @NotBlank(message = "Asset name is required")
        @Size(max = 128, message = "Asset name is too long")
        String assetName,
        Long ownerEmployeeId,
        @Size(max = 128, message = "Storage location is too long")
        String storageLocation,
        @NotBlank(message = "Asset status is required")
        @Size(max = 32, message = "Asset status is too long")
        String assetStatus,
        LocalDate purchaseDate
) {
}
