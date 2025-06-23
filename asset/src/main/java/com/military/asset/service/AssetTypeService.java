package com.military.asset.service;

import com.military.asset.model.AssetType;
import com.military.asset.repository.AssetTypeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AssetTypeService {
    private final AssetTypeRepository assetTypeRepository;
    public AssetTypeService(AssetTypeRepository assetTypeRepository) { this.assetTypeRepository = assetTypeRepository; }

    public List<AssetType> findAll() { return assetTypeRepository.findAll(); }
    public Optional<AssetType> findById(Long id) { return assetTypeRepository.findById(id); }
    public AssetType save(AssetType assetType) { return assetTypeRepository.save(assetType); }
    public void deleteById(Long id) { assetTypeRepository.deleteById(id); }
} 