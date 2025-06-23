package com.military.asset.controller;

import com.military.asset.model.AssetType;
import com.military.asset.service.AssetTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asset-types")
public class AssetTypeController {
    private final AssetTypeService assetTypeService;
    public AssetTypeController(AssetTypeService assetTypeService) { this.assetTypeService = assetTypeService; }

    @GetMapping
    public ResponseEntity<List<AssetType>> getAllAssetTypes() { return ResponseEntity.ok(assetTypeService.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<AssetType> getAssetTypeById(@PathVariable Long id) {
        return assetTypeService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssetType> createAssetType(@RequestBody AssetType assetType) { return ResponseEntity.ok(assetTypeService.save(assetType)); }

    @PutMapping("/{id}")
    public ResponseEntity<AssetType> updateAssetType(@PathVariable Long id, @RequestBody AssetType assetType) {
        return assetTypeService.findById(id)
            .map(existing -> { assetType.setId(id); return ResponseEntity.ok(assetTypeService.save(assetType)); })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssetType(@PathVariable Long id) {
        assetTypeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 