package com.military.asset.controller;

import com.military.asset.model.AuditLog;
import com.military.asset.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;
    public AuditLogController(AuditLogService auditLogService) { this.auditLogService = auditLogService; }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(auditLogService.findByFilters(baseId, assetTypeId, userId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        return auditLogService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
} 
 