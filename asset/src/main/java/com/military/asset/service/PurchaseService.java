package com.military.asset.service;

import com.military.asset.model.Purchase;
import com.military.asset.model.User;
import com.military.asset.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private UserService userService;

    public PurchaseService(PurchaseRepository purchaseRepository) { this.purchaseRepository = purchaseRepository; }

    public List<Purchase> findAll() { return purchaseRepository.findAll(); }
    public Optional<Purchase> findById(Long id) { return purchaseRepository.findById(id); }
    public Purchase save(Purchase purchase) {
        Purchase savedPurchase = purchaseRepository.save(purchase);
        
        // Log the transaction
        try {
            User user = userService.findByUsername(purchase.getCreatedBy().getUsername()).orElse(null);
            if (user != null) {
                auditLogService.logTransaction(
                    "CREATE_PURCHASE",
                    "Purchase",
                    "Created new purchase order for " + savedPurchase.getQuantity() + " " + 
                    savedPurchase.getAssetType().getName() + " at $" + savedPurchase.getUnitPrice() + " each",
                    user,
                    savedPurchase.getId()
                );
            }
        } catch (Exception e) {
            // Don't fail the main transaction if audit logging fails
            System.err.println("Failed to log purchase transaction: " + e.getMessage());
        }
        
        return savedPurchase;
    }
    public void deleteById(Long id) { purchaseRepository.deleteById(id); }
    
    public long countByFilters(Long baseId, Long assetTypeId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        
        // Parse date strings to LocalDate
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        
        // Apply filters based on provided parameters
        if (baseId != null && assetTypeId != null && fromDate != null && toDate != null) {
            return purchaseRepository.countByBaseIdAndAssetTypeIdAndDateBetween(baseId, assetTypeId, fromDate, toDate);
        } else if (baseId != null && fromDate != null && toDate != null) {
            return purchaseRepository.countByBaseIdAndDateBetween(baseId, fromDate, toDate);
        } else if (assetTypeId != null && fromDate != null && toDate != null) {
            return purchaseRepository.countByAssetTypeIdAndDateBetween(assetTypeId, fromDate, toDate);
        } else if (fromDate != null && toDate != null) {
            return purchaseRepository.countByDateBetween(fromDate, toDate);
        } else if (baseId != null && assetTypeId != null) {
            return purchaseRepository.countByBaseIdAndAssetTypeId(baseId, assetTypeId);
        } else if (baseId != null) {
            return purchaseRepository.countByBaseId(baseId);
        } else if (assetTypeId != null) {
            return purchaseRepository.countByAssetTypeId(assetTypeId);
        } else {
            return purchaseRepository.count();
        }
    }
    
    public List<Purchase> findByFilters(Long baseId, Long assetTypeId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        
        // Parse date strings to LocalDate
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        
        // Apply filters based on provided parameters using methods that fetch related entities
        if (baseId != null && assetTypeId != null && fromDate != null && toDate != null) {
            return purchaseRepository.findByBaseIdAndAssetTypeIdAndDateBetweenWithRelatedEntities(baseId, assetTypeId, fromDate, toDate);
        } else if (baseId != null && fromDate != null && toDate != null) {
            return purchaseRepository.findByBaseIdAndDateBetweenWithRelatedEntities(baseId, fromDate, toDate);
        } else if (assetTypeId != null && fromDate != null && toDate != null) {
            return purchaseRepository.findByAssetTypeIdAndDateBetweenWithRelatedEntities(assetTypeId, fromDate, toDate);
        } else if (fromDate != null && toDate != null) {
            return purchaseRepository.findByDateBetweenWithRelatedEntities(fromDate, toDate);
        } else if (baseId != null && assetTypeId != null) {
            return purchaseRepository.findByBaseIdAndAssetTypeIdWithRelatedEntities(baseId, assetTypeId);
        } else if (baseId != null) {
            return purchaseRepository.findByBaseIdWithRelatedEntities(baseId);
        } else if (assetTypeId != null) {
            return purchaseRepository.findByAssetTypeIdWithRelatedEntities(assetTypeId);
        } else {
            return purchaseRepository.findAllWithRelatedEntities();
        }
    }
} 