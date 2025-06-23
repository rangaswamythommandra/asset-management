package com.military.asset.service;

import com.military.asset.model.Assignment;
import com.military.asset.model.User;
import com.military.asset.repository.AssignmentRepository;
import com.military.asset.service.AuditLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AuditLogService auditLogService;

    public AssignmentService(AssignmentRepository assignmentRepository, AuditLogService auditLogService) {
        this.assignmentRepository = assignmentRepository;
        this.auditLogService = auditLogService;
    }

    public List<Assignment> findAll() { return assignmentRepository.findAllWithDetails(); }
    public Optional<Assignment> findById(Long id) { return assignmentRepository.findById(id); }
    public Assignment save(Assignment assignment, User currentUser) {
        Assignment saved = assignmentRepository.save(assignment);
        auditLogService.logTransaction(
            "ASSIGN_ASSET",
            "Assignment",
            "Assigned asset with ID " + saved.getAsset().getId() + " to user " + saved.getAssignedTo().getUsername(),
            currentUser,
            saved.getId()
        );
        return saved;
    }
    public void deleteById(Long id) { assignmentRepository.deleteById(id); }
    
    public long countByFilters(Long baseId, Long assetId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        if (baseId != null && assetId != null && fromDate != null && toDate != null) {
            return assignmentRepository.findByAssetBaseIdAndAssetIdAndAssignedDateBetween(baseId, assetId, fromDate, toDate).size();
        } else if (baseId != null && fromDate != null && toDate != null) {
            return assignmentRepository.findByAssetBaseIdAndAssignedDateBetween(baseId, fromDate, toDate).size();
        } else if (assetId != null && fromDate != null && toDate != null) {
            return assignmentRepository.findByAssetIdAndAssignedDateBetween(assetId, fromDate, toDate).size();
        } else if (fromDate != null && toDate != null) {
            return assignmentRepository.findByAssignedDateBetween(fromDate, toDate).size();
        } else if (baseId != null && assetId != null) {
            return assignmentRepository.findByAssetBaseIdAndAssetId(baseId, assetId).size();
        } else if (baseId != null) {
            return assignmentRepository.findByAssetBaseId(baseId).size();
        } else if (assetId != null) {
            return assignmentRepository.findByAssetId(assetId).size();
        } else {
            return assignmentRepository.count();
        }
    }
    
    public List<Assignment> findByFilters(Long baseId, Long assetId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        if (baseId != null && assetId != null && fromDate != null && toDate != null) {
            return assignmentRepository.findByAssetBaseIdAndAssetIdAndAssignedDateBetween(baseId, assetId, fromDate, toDate);
        } else if (baseId != null && fromDate != null && toDate != null) {
            return assignmentRepository.findByAssetBaseIdAndAssignedDateBetween(baseId, fromDate, toDate);
        } else if (assetId != null && fromDate != null && toDate != null) {
            return assignmentRepository.findByAssetIdAndAssignedDateBetween(assetId, fromDate, toDate);
        } else if (fromDate != null && toDate != null) {
            return assignmentRepository.findByAssignedDateBetween(fromDate, toDate);
        } else if (baseId != null && assetId != null) {
            return assignmentRepository.findByAssetBaseIdAndAssetId(baseId, assetId);
        } else if (baseId != null) {
            return assignmentRepository.findByAssetBaseId(baseId);
        } else if (assetId != null) {
            return assignmentRepository.findByAssetId(assetId);
        } else {
            return assignmentRepository.findAllWithDetails();
        }
    }
    
    public long countActiveAssignments(Long baseId, Long assetTypeId) {
        List<Assignment> allAssignments = assignmentRepository.findAllWithDetails();
        return allAssignments.stream()
            .filter(assignment -> Assignment.AssignmentStatus.ACTIVE.equals(assignment.getStatus()))
            .filter(assignment -> baseId == null || assignment.getAsset().getBase().getId().equals(baseId))
            .filter(assignment -> assetTypeId == null || assignment.getAsset().getAssetType().getId().equals(assetTypeId))
            .count();
    }

    // For backward compatibility
    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
} 