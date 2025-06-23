package com.military.asset.service;

import com.military.asset.model.Asset;
import com.military.asset.repository.AssetRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    public AssetService(AssetRepository assetRepository) { this.assetRepository = assetRepository; }

    public List<Asset> findAll() { return assetRepository.findAll(); }
    public Optional<Asset> findById(Long id) { return assetRepository.findById(id); }
    public Asset save(Asset asset) { return assetRepository.save(asset); }
    public void deleteById(Long id) { assetRepository.deleteById(id); }
    
    public long countByFilters(Long baseId, Long assetTypeId) {
        if (baseId != null && assetTypeId != null) {
            return assetRepository.countByBaseIdAndAssetTypeId(baseId, assetTypeId);
        } else if (baseId != null) {
            return assetRepository.countByBaseId(baseId);
        } else if (assetTypeId != null) {
            return assetRepository.countByAssetTypeId(assetTypeId);
        } else {
            return assetRepository.count();
        }
    }
    
    public List<Asset> findByFilters(Long baseId, Long assetTypeId, String dateFrom, String dateTo) {
        if (baseId != null && assetTypeId != null) {
            return assetRepository.findByBaseIdAndAssetTypeId(baseId, assetTypeId);
        } else if (baseId != null) {
            return assetRepository.findByBaseId(baseId);
        } else if (assetTypeId != null) {
            return assetRepository.findByAssetTypeId(assetTypeId);
        } else {
            return assetRepository.findAll();
        }
    }
} 