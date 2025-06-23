package com.military.asset.service;

import com.military.asset.model.Transfer;
import com.military.asset.model.User;
import com.military.asset.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransferService {
    private final TransferRepository transferRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private UserService userService;

    public TransferService(TransferRepository transferRepository) { this.transferRepository = transferRepository; }

    public List<Transfer> findAll() { return transferRepository.findAll(); }
    public Optional<Transfer> findById(Long id) { return transferRepository.findById(id); }
    public Transfer save(Transfer transfer) {
        Transfer savedTransfer = transferRepository.save(transfer);
        
        // Log the transaction
        try {
            User user = userService.findByUsername(transfer.getCreatedBy().getUsername()).orElse(null);
            if (user != null) {
                auditLogService.logTransaction(
                    "CREATE_TRANSFER",
                    "Transfer",
                    "Created transfer request for " + savedTransfer.getAsset().getAssetType().getName() + 
                    " (" + savedTransfer.getAsset().getSerialNumber() + ") from " + 
                    savedTransfer.getFromBase().getName() + " to " + savedTransfer.getToBase().getName(),
                    user,
                    savedTransfer.getId()
                );
            }
        } catch (Exception e) {
            System.err.println("Failed to log transfer transaction: " + e.getMessage());
        }
        
        return savedTransfer;
    }
    public void deleteById(Long id) { transferRepository.deleteById(id); }
    
    public long countByFilters(Long baseId, Long assetTypeId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        if (baseId != null && assetTypeId != null && fromDate != null && toDate != null) {
            return transferRepository.findByFromBaseIdAndAssetAssetTypeIdAndDateBetweenWithRelatedEntities(baseId, assetTypeId, fromDate, toDate).size();
        } else if (baseId != null && fromDate != null && toDate != null) {
            return transferRepository.findByFromBaseIdAndDateBetweenWithRelatedEntities(baseId, fromDate, toDate).size();
        } else if (assetTypeId != null && fromDate != null && toDate != null) {
            return transferRepository.findByAssetAssetTypeIdAndDateBetweenWithRelatedEntities(assetTypeId, fromDate, toDate).size();
        } else if (fromDate != null && toDate != null) {
            return transferRepository.findByDateBetweenWithRelatedEntities(fromDate, toDate).size();
        } else if (baseId != null && assetTypeId != null) {
            return transferRepository.findByFromBaseIdAndAssetAssetTypeIdWithRelatedEntities(baseId, assetTypeId).size();
        } else if (baseId != null) {
            return transferRepository.findByFromBaseIdWithRelatedEntities(baseId).size();
        } else if (assetTypeId != null) {
            return transferRepository.findByAssetAssetTypeIdWithRelatedEntities(assetTypeId).size();
        } else {
            return transferRepository.count();
        }
    }
    
    public List<Transfer> findByFilters(Long baseId, Long assetTypeId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        if (baseId != null && assetTypeId != null && fromDate != null && toDate != null) {
            return transferRepository.findByFromBaseIdAndAssetAssetTypeIdAndDateBetweenWithRelatedEntities(baseId, assetTypeId, fromDate, toDate);
        } else if (baseId != null && fromDate != null && toDate != null) {
            return transferRepository.findByFromBaseIdAndDateBetweenWithRelatedEntities(baseId, fromDate, toDate);
        } else if (assetTypeId != null && fromDate != null && toDate != null) {
            return transferRepository.findByAssetAssetTypeIdAndDateBetweenWithRelatedEntities(assetTypeId, fromDate, toDate);
        } else if (fromDate != null && toDate != null) {
            return transferRepository.findByDateBetweenWithRelatedEntities(fromDate, toDate);
        } else if (baseId != null && assetTypeId != null) {
            return transferRepository.findByFromBaseIdAndAssetAssetTypeIdWithRelatedEntities(baseId, assetTypeId);
        } else if (baseId != null) {
            return transferRepository.findByFromBaseIdWithRelatedEntities(baseId);
        } else if (assetTypeId != null) {
            return transferRepository.findByAssetAssetTypeIdWithRelatedEntities(assetTypeId);
        } else {
            return transferRepository.findAllWithRelatedEntities();
        }
    }
    
    public Transfer approveTransfer(Long id, User approvedBy) {
        Optional<Transfer> transferOpt = findById(id);
        if (transferOpt.isPresent()) {
            Transfer transfer = transferOpt.get();
            transfer.setStatus(Transfer.TransferStatus.APPROVED);
            transfer.setApprovedBy(approvedBy);
            
            Transfer savedTransfer = transferRepository.save(transfer);
            
            // Log the approval
            try {
                auditLogService.logTransaction(
                    "APPROVE_TRANSFER",
                    "Transfer",
                    "Approved transfer of " + savedTransfer.getAsset().getAssetType().getName() + 
                    " (" + savedTransfer.getAsset().getSerialNumber() + ") from " + 
                    savedTransfer.getFromBase().getName() + " to " + savedTransfer.getToBase().getName(),
                    approvedBy,
                    savedTransfer.getId()
                );
            } catch (Exception e) {
                System.err.println("Failed to log transfer approval: " + e.getMessage());
            }
            
            return savedTransfer;
        }
        return null;
    }
    
    public Transfer rejectTransfer(Long id, User rejectedBy) {
        Optional<Transfer> transferOpt = findById(id);
        if (transferOpt.isPresent()) {
            Transfer transfer = transferOpt.get();
            transfer.setStatus(Transfer.TransferStatus.REJECTED);
            transfer.setApprovedBy(rejectedBy);
            
            Transfer savedTransfer = transferRepository.save(transfer);
            
            // Log the rejection
            try {
                auditLogService.logTransaction(
                    "REJECT_TRANSFER",
                    "Transfer",
                    "Rejected transfer of " + savedTransfer.getAsset().getAssetType().getName() + 
                    " (" + savedTransfer.getAsset().getSerialNumber() + ") from " + 
                    savedTransfer.getFromBase().getName() + " to " + savedTransfer.getToBase().getName(),
                    rejectedBy,
                    savedTransfer.getId()
                );
            } catch (Exception e) {
                System.err.println("Failed to log transfer rejection: " + e.getMessage());
            }
            
            return savedTransfer;
        }
        return null;
    }
} 