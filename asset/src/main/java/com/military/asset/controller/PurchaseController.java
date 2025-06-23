package com.military.asset.controller;

import com.military.asset.model.Purchase;
import com.military.asset.model.AssetType;
import com.military.asset.model.Base;
import com.military.asset.model.User;
import com.military.asset.service.PurchaseService;
import com.military.asset.service.AssetTypeService;
import com.military.asset.service.BaseService;
import com.military.asset.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final AssetTypeService assetTypeService;
    private final BaseService baseService;
    private final UserService userService;

    public PurchaseController(PurchaseService purchaseService, AssetTypeService assetTypeService, 
                            BaseService baseService, UserService userService) {
        this.purchaseService = purchaseService;
        this.assetTypeService = assetTypeService;
        this.baseService = baseService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(purchaseService.findByFilters(baseId, assetTypeId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
        return purchaseService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(@RequestBody PurchaseRequest request, Authentication authentication) {
        try {
            Purchase purchase = new Purchase();
            purchase.setAssetType(assetTypeService.findById(request.getAssetTypeId()).orElseThrow());
            purchase.setBase(baseService.findById(request.getBaseId()).orElseThrow());
            purchase.setQuantity(request.getQuantity());
            purchase.setUnitPrice(request.getUnitPrice());
            purchase.setTotalAmount(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            purchase.setSupplier(request.getSupplier());
            purchase.setDescription(request.getDescription());
            purchase.setDate(LocalDate.parse(request.getPurchaseDate()));
            
            // Set the current user as createdBy
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username).orElseThrow();
            purchase.setCreatedBy(currentUser);
            
            return ResponseEntity.ok(purchaseService.save(purchase));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Long id, @RequestBody PurchaseRequest request) {
        Optional<Purchase> existingOpt = purchaseService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Purchase existing = existingOpt.get();
            existing.setAssetType(assetTypeService.findById(request.getAssetTypeId()).orElseThrow());
            existing.setBase(baseService.findById(request.getBaseId()).orElseThrow());
            existing.setQuantity(request.getQuantity());
            existing.setUnitPrice(request.getUnitPrice());
            existing.setTotalAmount(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            existing.setSupplier(request.getSupplier());
            existing.setDescription(request.getDescription());
            existing.setDate(LocalDate.parse(request.getPurchaseDate()));
            
            Purchase saved = purchaseService.save(existing);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO for request/response
    public static class PurchaseRequest {
        private Long assetTypeId;
        private Long baseId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String purchaseDate;
        private String supplier;
        private String description;

        // Getters and setters
        public Long getAssetTypeId() { return assetTypeId; }
        public void setAssetTypeId(Long assetTypeId) { this.assetTypeId = assetTypeId; }
        
        public Long getBaseId() { return baseId; }
        public void setBaseId(Long baseId) { this.baseId = baseId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public String getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
        
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
} 