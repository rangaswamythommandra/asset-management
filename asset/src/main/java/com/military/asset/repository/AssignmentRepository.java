package com.military.asset.repository;

import com.military.asset.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    long countByAssetBaseId(Long baseId);
    long countByAssetAssetTypeId(Long assetTypeId);
    long countByAssetBaseIdAndAssetAssetTypeId(Long baseId, Long assetTypeId);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy")
    List<Assignment> findAllWithDetails();
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.base.id = :baseId")
    List<Assignment> findByAssetBaseId(@Param("baseId") Long baseId);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.assetType.id = :assetTypeId")
    List<Assignment> findByAssetAssetTypeId(@Param("assetTypeId") Long assetTypeId);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.base.id = :baseId AND ast.assetType.id = :assetTypeId")
    List<Assignment> findByAssetBaseIdAndAssetAssetTypeId(@Param("baseId") Long baseId, @Param("assetTypeId") Long assetTypeId);
    
    // Additional methods for service compatibility
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.id = :assetId")
    List<Assignment> findByAssetId(@Param("assetId") Long assetId);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.base.id = :baseId AND ast.id = :assetId")
    List<Assignment> findByAssetBaseIdAndAssetId(@Param("baseId") Long baseId, @Param("assetId") Long assetId);
    
    // Date filtering methods with JOIN FETCH
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE a.assignedDate BETWEEN :dateFrom AND :dateTo")
    List<Assignment> findByAssignedDateBetween(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.base.id = :baseId AND a.assignedDate BETWEEN :dateFrom AND :dateTo")
    List<Assignment> findByAssetBaseIdAndAssignedDateBetween(@Param("baseId") Long baseId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.id = :assetId AND a.assignedDate BETWEEN :dateFrom AND :dateTo")
    List<Assignment> findByAssetIdAndAssignedDateBetween(@Param("assetId") Long assetId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT a FROM Assignment a JOIN FETCH a.asset ast JOIN FETCH ast.assetType JOIN FETCH a.assignedTo JOIN FETCH a.assignedBy WHERE ast.base.id = :baseId AND ast.id = :assetId AND a.assignedDate BETWEEN :dateFrom AND :dateTo")
    List<Assignment> findByAssetBaseIdAndAssetIdAndAssignedDateBetween(@Param("baseId") Long baseId, @Param("assetId") Long assetId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
} 