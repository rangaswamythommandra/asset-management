package com.military.asset.controller;

import com.military.asset.service.AssetService;
import com.military.asset.service.PurchaseService;
import com.military.asset.service.TransferService;
import com.military.asset.service.AssignmentService;
import com.military.asset.service.ExpenditureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final AssetService assetService;
    private final PurchaseService purchaseService;
    private final TransferService transferService;
    private final AssignmentService assignmentService;
    private final ExpenditureService expenditureService;

    public DashboardController(AssetService assetService, PurchaseService purchaseService, 
                             TransferService transferService, AssignmentService assignmentService,
                             ExpenditureService expenditureService) {
        this.assetService = assetService;
        this.purchaseService = purchaseService;
        this.transferService = transferService;
        this.assignmentService = assignmentService;
        this.expenditureService = expenditureService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        // Calculate metrics based on filtered data
        long totalAssets = assetService.countByFilters(baseId, assetTypeId);
        long totalPurchases = purchaseService.countByFilters(baseId, assetTypeId, dateFrom, dateTo);
        long totalTransfers = transferService.countByFilters(baseId, assetTypeId, dateFrom, dateTo);
        long totalAssignments = assignmentService.countByFilters(baseId, assetTypeId, dateFrom, dateTo);
        long totalExpenditures = expenditureService.countByFilters(baseId, assetTypeId, dateFrom, dateTo);
        
        // Count only active assignments (not returned ones)
        long activeAssignments = assignmentService.countActiveAssignments(baseId, assetTypeId);

        // Calculate financial metrics (using counts as placeholders for now)
        double openingBalance = totalAssets * 1000.0; // Placeholder calculation
        double purchases = totalPurchases * 5000.0; // Placeholder calculation
        double transfersIn = totalTransfers * 2000.0; // Placeholder calculation
        double transfersOut = totalTransfers * 1500.0; // Placeholder calculation
        double assigned = activeAssignments; // Use actual count of active assignments
        double expended = totalExpenditures * 1200.0; // Placeholder calculation
        double closingBalance = openingBalance + purchases + transfersIn - transfersOut - assigned - expended;
        double netMovement = purchases + transfersIn - transfersOut;

        Map<String, Object> metrics = Map.of(
            "openingBalance", openingBalance,
            "closingBalance", closingBalance,
            "netMovement", netMovement,
            "purchases", purchases,
            "transfersIn", transfersIn,
            "transfersOut", transfersOut,
            "assigned", assigned,
            "expended", expended
        );

        return ResponseEntity.ok(metrics);
    }
} 