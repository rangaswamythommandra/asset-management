package com.military.asset.service;

import com.military.asset.model.AuditLog;
import com.military.asset.model.User;
import com.military.asset.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    public AuditLogService(AuditLogRepository auditLogRepository) { this.auditLogRepository = auditLogRepository; }

    public List<AuditLog> findAll() { return auditLogRepository.findAllWithDetails(); }
    public Optional<AuditLog> findById(Long id) { return auditLogRepository.findById(id); }
    public AuditLog save(AuditLog auditLog) { return auditLogRepository.save(auditLog); }
    public void deleteById(Long id) { auditLogRepository.deleteById(id); }
    
    public List<AuditLog> findByFilters(Long baseId, Long assetTypeId, Long userId, String dateFrom, String dateTo) {
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        
        // Parse date strings to LocalDateTime
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            LocalDate fromDate = LocalDate.parse(dateFrom);
            fromDateTime = fromDate.atStartOfDay();
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            LocalDate toDate = LocalDate.parse(dateTo);
            toDateTime = toDate.atTime(LocalTime.MAX); // End of day
        }
        
        // For now, we'll implement basic filtering without complex combinations
        // since the repository methods are limited. In a production system,
        // you'd want to use a more flexible query approach.
        
        if (userId != null) {
            // Filter by user - this would need a repository method
            // For now, return all and filter in service
            List<AuditLog> allLogs = auditLogRepository.findAll();
            return allLogs.stream()
                .filter(log -> log.getUser().getId().equals(userId))
                .toList();
        }
        
        // Apply other filters based on provided parameters
        if (baseId != null && assetTypeId != null && fromDateTime != null && toDateTime != null) {
            // All filters: base, asset type, and date range
            return auditLogRepository.findByBaseIdAndAssetTypeIdAndTimestampBetween(baseId, assetTypeId, fromDateTime, toDateTime);
        } else if (baseId != null && assetTypeId != null) {
            // Base and asset type filters
            return auditLogRepository.findByBaseIdAndAssetTypeId(baseId, assetTypeId);
        } else if (baseId != null && fromDateTime != null && toDateTime != null) {
            // Base and date range filters
            return auditLogRepository.findByUserBaseIdAndTimestampBetween(baseId, fromDateTime, toDateTime);
        } else if (assetTypeId != null && fromDateTime != null && toDateTime != null) {
            // Asset type and date range filters
            return auditLogRepository.findByAssetTypeIdAndTimestampBetween(assetTypeId, fromDateTime, toDateTime);
        } else if (baseId != null) {
            // Only base filter
            return auditLogRepository.findByUserBaseId(baseId);
        } else if (assetTypeId != null) {
            // Only asset type filter
            return auditLogRepository.findByAssetTypeId(assetTypeId);
        } else if (fromDateTime != null && toDateTime != null) {
            // Only date range filter
            return auditLogRepository.findByTimestampBetween(fromDateTime, toDateTime);
        } else {
            // No filters - return all
            return auditLogRepository.findAllWithDetails();
        }
    }

    public void logTransaction(String action, String entity, String details, User user, Long entityId) {
        try {
            System.out.println(">>> AuditLogService.logTransaction called! User: " + (user != null ? user.getId() : "null"));
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setEntity(entity);
            auditLog.setDetails(details);
            auditLog.setUser(user);
            auditLog.setTimestamp(LocalDateTime.now());
            auditLog.setEntityId(entityId);
            auditLogRepository.save(auditLog);
            System.out.println(">>> Audit log saved!");
        } catch (Exception e) {
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }
} 