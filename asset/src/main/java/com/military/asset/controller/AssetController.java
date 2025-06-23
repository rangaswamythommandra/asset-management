package com.military.asset.controller;

import com.military.asset.model.Asset;
import com.military.asset.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetService assetService;
    public AssetController(AssetService assetService) { this.assetService = assetService; }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(assetService.findByFilters(baseId, assetTypeId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long id) {
        return assetService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) { return ResponseEntity.ok(assetService.save(asset)); }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        return assetService.findById(id)
            .map(existing -> { asset.setId(id); return ResponseEntity.ok(assetService.save(asset)); })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 