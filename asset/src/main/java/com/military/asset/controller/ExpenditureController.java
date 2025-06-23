package com.military.asset.controller;

import com.military.asset.model.Expenditure;
import com.military.asset.service.ExpenditureService;
import com.military.asset.service.AssetService;
import com.military.asset.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {
    private final ExpenditureService expenditureService;
    private final AssetService assetService;
    private final BaseService baseService;
    public ExpenditureController(ExpenditureService expenditureService, AssetService assetService, BaseService baseService) {
        this.expenditureService = expenditureService;
        this.assetService = assetService;
        this.baseService = baseService;
    }

    @GetMapping
    public ResponseEntity<List<Expenditure>> getAllExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(expenditureService.findByFilters(baseId, assetTypeId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expenditure> getExpenditureById(@PathVariable Long id) {
        return expenditureService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Expenditure> createExpenditure(@RequestBody ExpenditureRequest request) {
        Expenditure expenditure = new Expenditure();
        expenditure.setAsset(assetService.findById(request.getAssetId()).orElseThrow());
        expenditure.setBase(baseService.findById(request.getBaseId()).orElseThrow());
        expenditure.setQuantity(request.getQuantity());
        expenditure.setReason(request.getReason());
        expenditure.setExpenditureDate(request.getExpenditureDate());
        // Optionally set approvedBy if needed
        return ResponseEntity.ok(expenditureService.save(expenditure));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expenditure> updateExpenditure(@PathVariable Long id, @RequestBody ExpenditureRequest request) {
        return expenditureService.findById(id)
            .map(existing -> {
                existing.setAsset(assetService.findById(request.getAssetId()).orElseThrow());
                existing.setBase(baseService.findById(request.getBaseId()).orElseThrow());
                existing.setQuantity(request.getQuantity());
                existing.setReason(request.getReason());
                existing.setExpenditureDate(request.getExpenditureDate());
                // Optionally set approvedBy if needed
                return ResponseEntity.ok(expenditureService.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenditure(@PathVariable Long id) {
        expenditureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO for request/response
    public static class ExpenditureRequest {
        private Long assetId;
        private Long baseId;
        private Integer quantity;
        private String reason;
        private java.time.LocalDate expenditureDate;
        // Getters and setters
        public Long getAssetId() { return assetId; }
        public void setAssetId(Long assetId) { this.assetId = assetId; }
        public Long getBaseId() { return baseId; }
        public void setBaseId(Long baseId) { this.baseId = baseId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public java.time.LocalDate getExpenditureDate() { return expenditureDate; }
        public void setExpenditureDate(java.time.LocalDate expenditureDate) { this.expenditureDate = expenditureDate; }
    }
} 