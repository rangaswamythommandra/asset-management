package com.military.asset.controller;

import com.military.asset.model.Assignment;
import com.military.asset.model.Asset;
import com.military.asset.model.User;
import com.military.asset.service.AssignmentService;
import com.military.asset.service.AssetService;
import com.military.asset.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final AssetService assetService;
    private final UserService userService;

    public AssignmentController(AssignmentService assignmentService, AssetService assetService, 
                              UserService userService) {
        this.assignmentService = assignmentService;
        this.assetService = assetService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAllAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetTypeId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(assignmentService.findByFilters(baseId, assetTypeId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return assignmentService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody AssignmentRequest request, Authentication authentication) {
        try {
            Assignment assignment = new Assignment();
            assignment.setAsset(assetService.findById(request.getAssetId()).orElseThrow());
            assignment.setAssignedTo(userService.findById(request.getAssignedToId()).orElseThrow());
            assignment.setAssignedDate(LocalDate.parse(request.getAssignmentDate()));
            assignment.setNotes(request.getNotes());
            
            // Set the current user as assignedBy
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username).orElseThrow();
            assignment.setAssignedBy(currentUser);
            
            return ResponseEntity.ok(assignmentService.save(assignment, currentUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(@PathVariable Long id, @RequestBody AssignmentRequest request, Authentication authentication) {
        Optional<Assignment> existingOpt = assignmentService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Assignment existing = existingOpt.get();
            existing.setAsset(assetService.findById(request.getAssetId()).orElseThrow());
            existing.setAssignedTo(userService.findById(request.getAssignedToId()).orElseThrow());
            existing.setAssignedDate(LocalDate.parse(request.getAssignmentDate()));
            existing.setNotes(request.getNotes());
            
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username).orElseThrow();
            Assignment saved = assignmentService.save(existing, currentUser);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Assignment> returnAssignment(@PathVariable Long id, Authentication authentication) {
        Optional<Assignment> existingOpt = assignmentService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Assignment existing = existingOpt.get();
            existing.setStatus(Assignment.AssignmentStatus.RETURNED);
            existing.setReturnDate(LocalDate.now());
            
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username).orElseThrow();
            Assignment saved = assignmentService.save(existing, currentUser);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO for request/response
    public static class AssignmentRequest {
        private Long assetId;
        private Long assignedToId;
        private String assignmentDate;
        private String notes;

        // Getters and setters
        public Long getAssetId() { return assetId; }
        public void setAssetId(Long assetId) { this.assetId = assetId; }
        
        public Long getAssignedToId() { return assignedToId; }
        public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
        
        public String getAssignmentDate() { return assignmentDate; }
        public void setAssignmentDate(String assignmentDate) { this.assignmentDate = assignmentDate; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
} 