package com.military.asset.repository;

import com.military.asset.model.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
    long countByBaseId(Long baseId);
    long countByAssetAssetTypeId(Long assetTypeId);
    long countByBaseIdAndAssetAssetTypeId(Long baseId, Long assetTypeId);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy")
    List<Expenditure> findAllWithDetails();
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE e.base.id = :baseId")
    List<Expenditure> findByBaseId(@Param("baseId") Long baseId);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE a.assetType.id = :assetTypeId")
    List<Expenditure> findByAssetAssetTypeId(@Param("assetTypeId") Long assetTypeId);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE e.base.id = :baseId AND a.assetType.id = :assetTypeId")
    List<Expenditure> findByBaseIdAndAssetAssetTypeId(@Param("baseId") Long baseId, @Param("assetTypeId") Long assetTypeId);
    
    // Additional methods for service compatibility
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE a.id = :assetId")
    List<Expenditure> findByAssetId(@Param("assetId") Long assetId);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE e.base.id = :baseId AND a.id = :assetId")
    List<Expenditure> findByBaseIdAndAssetId(@Param("baseId") Long baseId, @Param("assetId") Long assetId);
    
    // Date filtering methods with JOIN FETCH
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE e.expenditureDate BETWEEN :dateFrom AND :dateTo")
    List<Expenditure> findByExpenditureDateBetween(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE e.base.id = :baseId AND e.expenditureDate BETWEEN :dateFrom AND :dateTo")
    List<Expenditure> findByBaseIdAndExpenditureDateBetween(@Param("baseId") Long baseId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE a.id = :assetId AND e.expenditureDate BETWEEN :dateFrom AND :dateTo")
    List<Expenditure> findByAssetIdAndExpenditureDateBetween(@Param("assetId") Long assetId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT e FROM Expenditure e JOIN FETCH e.asset a JOIN FETCH a.assetType JOIN FETCH e.base LEFT JOIN FETCH e.approvedBy WHERE e.base.id = :baseId AND a.id = :assetId AND e.expenditureDate BETWEEN :dateFrom AND :dateTo")
    List<Expenditure> findByBaseIdAndAssetIdAndExpenditureDateBetween(@Param("baseId") Long baseId, @Param("assetId") Long assetId, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
} 