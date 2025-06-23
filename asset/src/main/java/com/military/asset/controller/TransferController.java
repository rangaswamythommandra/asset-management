package com.military.asset.controller;

import com.military.asset.model.Transfer;
import com.military.asset.model.Asset;
import com.military.asset.model.Base;
import com.military.asset.model.User;
import com.military.asset.service.TransferService;
import com.military.asset.service.AssetService;
import com.military.asset.service.BaseService;
import com.military.asset.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;
    private final AssetService assetService;
    private final BaseService baseService;
    private final UserService userService;

    public TransferController(TransferService transferService, AssetService assetService, 
                            BaseService baseService, UserService userService) {
        this.transferService = transferService;
        this.assetService = assetService;
        this.baseService = baseService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> getAllTransfers(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(transferService.findByFilters(baseId, assetTypeId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transfer> getTransferById(@PathVariable Long id) {
        return transferService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transfer> createTransfer(@RequestBody TransferRequest request, Authentication authentication) {
        try {
            Transfer transfer = new Transfer();
            transfer.setAsset(assetService.findById(request.getAssetId()).orElseThrow());
            transfer.setFromBase(baseService.findById(request.getFromBaseId()).orElseThrow());
            transfer.setToBase(baseService.findById(request.getToBaseId()).orElseThrow());
            transfer.setDate(LocalDate.parse(request.getTransferDate()));
            transfer.setReason(request.getReason());
            
            // Set the current user as createdBy
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username).orElseThrow();
            transfer.setCreatedBy(currentUser);
            
            return ResponseEntity.ok(transferService.save(transfer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transfer> updateTransfer(@PathVariable Long id, @RequestBody TransferRequest request) {
        Optional<Transfer> existingOpt = transferService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Transfer existing = existingOpt.get();
            existing.setAsset(assetService.findById(request.getAssetId()).orElseThrow());
            existing.setFromBase(baseService.findById(request.getFromBaseId()).orElseThrow());
            existing.setToBase(baseService.findById(request.getToBaseId()).orElseThrow());
            existing.setDate(LocalDate.parse(request.getTransferDate()));
            existing.setReason(request.getReason());
            
            Transfer saved = transferService.save(existing);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        transferService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Transfer> approveTransfer(@PathVariable Long id, Authentication authentication) {
        Optional<Transfer> existingOpt = transferService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Transfer approvedTransfer = transferService.approveTransfer(id, userOpt.get());
            if (approvedTransfer != null) {
                return ResponseEntity.ok(approvedTransfer);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Transfer> rejectTransfer(@PathVariable Long id, Authentication authentication) {
        Optional<Transfer> existingOpt = transferService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Transfer rejectedTransfer = transferService.rejectTransfer(id, userOpt.get());
            if (rejectedTransfer != null) {
                return ResponseEntity.ok(rejectedTransfer);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO for request/response
    public static class TransferRequest {
        private Long assetId;
        private Long fromBaseId;
        private Long toBaseId;
        private String transferDate;
        private String reason;

        // Getters and setters
        public Long getAssetId() { return assetId; }
        public void setAssetId(Long assetId) { this.assetId = assetId; }
        
        public Long getFromBaseId() { return fromBaseId; }
        public void setFromBaseId(Long fromBaseId) { this.fromBaseId = fromBaseId; }
        
        public Long getToBaseId() { return toBaseId; }
        public void setToBaseId(Long toBaseId) { this.toBaseId = toBaseId; }
        
        public String getTransferDate() { return transferDate; }
        public void setTransferDate(String transferDate) { this.transferDate = transferDate; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class RejectRequest {
        private String reason;

        // Getters and setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
} 