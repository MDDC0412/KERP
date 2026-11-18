package com.kerp.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kerp.common.exception.BusinessException;
import com.kerp.modules.asset.dto.AssetCategoryCreateRequest;
import com.kerp.modules.asset.dto.AssetCreateRequest;
import com.kerp.modules.asset.entity.AssetCategory;
import com.kerp.modules.asset.entity.AssetItem;
import com.kerp.modules.asset.mapper.AssetCategoryMapper;
import com.kerp.modules.asset.mapper.AssetItemMapper;
import com.kerp.modules.asset.service.AssetService;
import com.kerp.modules.employee.entity.Employee;
import com.kerp.modules.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetCategoryMapper assetCategoryMapper;
    private final AssetItemMapper assetItemMapper;
    private final EmployeeMapper employeeMapper;

    public AssetServiceImpl(AssetCategoryMapper assetCategoryMapper,
                            AssetItemMapper assetItemMapper,
                            EmployeeMapper employeeMapper) {
        this.assetCategoryMapper = assetCategoryMapper;
        this.assetItemMapper = assetItemMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public AssetCategory createCategory(AssetCategoryCreateRequest request) {
        long duplicateCount = assetCategoryMapper.selectCount(
                new LambdaQueryWrapper<AssetCategory>().eq(AssetCategory::getCode, request.code())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Asset category code already exists");
        }
        AssetCategory assetCategory = new AssetCategory();
        assetCategory.setCode(request.code());
        assetCategory.setName(request.name());
        assetCategory.setDepreciationYears(request.depreciationYears() == null ? 3 : request.depreciationYears());
        assetCategoryMapper.insert(assetCategory);
        return assetCategory;
    }

    @Override
    public List<AssetCategory> listCategories() {
        return assetCategoryMapper.selectList(new LambdaQueryWrapper<AssetCategory>().orderByAsc(AssetCategory::getCode));
    }

    @Override
    public AssetItem createAsset(AssetCreateRequest request) {
        long duplicateCount = assetItemMapper.selectCount(
                new LambdaQueryWrapper<AssetItem>().eq(AssetItem::getAssetNo, request.assetNo())
        );
        if (duplicateCount > 0) {
            throw new BusinessException("Asset no already exists");
        }
        AssetCategory assetCategory = assetCategoryMapper.selectById(request.categoryId());
        if (assetCategory == null) {
            throw new BusinessException("Asset category does not exist");
        }
        if (request.ownerEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(request.ownerEmployeeId());
            if (employee == null) {
                throw new BusinessException("Owner employee does not exist");
            }
        }
        AssetItem assetItem = new AssetItem();
        assetItem.setAssetNo(request.assetNo());
        assetItem.setCategoryId(request.categoryId());
        assetItem.setAssetName(request.assetName());
        assetItem.setOwnerEmployeeId(request.ownerEmployeeId());
        assetItem.setStorageLocation(request.storageLocation());
        assetItem.setAssetStatus(request.assetStatus());
        assetItem.setPurchaseDate(request.purchaseDate());
        assetItemMapper.insert(assetItem);
        return assetItem;
    }

    @Override
    public List<AssetItem> listAssets() {
        return assetItemMapper.selectList(new LambdaQueryWrapper<AssetItem>().orderByDesc(AssetItem::getId));
    }
}
