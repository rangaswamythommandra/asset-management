package com.military.asset.repository;

import com.military.asset.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    long countByBaseId(Long baseId);
    long countByAssetTypeId(Long assetTypeId);
    long countByBaseIdAndAssetTypeId(Long baseId, Long assetTypeId);
    
    List<Asset> findByBaseId(Long baseId);
    List<Asset> findByAssetTypeId(Long assetTypeId);
    List<Asset> findByBaseIdAndAssetTypeId(Long baseId, Long assetTypeId);
} 