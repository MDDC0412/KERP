package com.kerp.modules.asset.controller;

import com.kerp.common.api.ApiResponse;
import com.kerp.modules.asset.dto.AssetCategoryCreateRequest;
import com.kerp.modules.asset.dto.AssetCreateRequest;
import com.kerp.modules.asset.entity.AssetCategory;
import com.kerp.modules.asset.entity.AssetItem;
import com.kerp.modules.asset.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<AssetCategory>> listCategories() {
        return ApiResponse.success(assetService.listCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<AssetCategory> createCategory(@Valid @RequestBody AssetCategoryCreateRequest request) {
        return ApiResponse.success(assetService.createCategory(request));
    }

    @GetMapping
    public ApiResponse<List<AssetItem>> listAssets() {
        return ApiResponse.success(assetService.listAssets());
    }

    @PostMapping
    public ApiResponse<AssetItem> createAsset(@Valid @RequestBody AssetCreateRequest request) {
        return ApiResponse.success(assetService.createAsset(request));
    }
}
