package com.kerp.modules.asset.service;

import com.kerp.modules.asset.dto.AssetCategoryCreateRequest;
import com.kerp.modules.asset.dto.AssetCreateRequest;
import com.kerp.modules.asset.entity.AssetCategory;
import com.kerp.modules.asset.entity.AssetItem;

import java.util.List;

public interface AssetService {

    AssetCategory createCategory(AssetCategoryCreateRequest request);

    List<AssetCategory> listCategories();

    AssetItem createAsset(AssetCreateRequest request);

    List<AssetItem> listAssets();
}
