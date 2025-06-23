package com.military.asset.repository;

import com.military.asset.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    long countByBaseId(Long baseId);
    long countByAssetTypeId(Long assetTypeId);
    long countByBaseIdAndAssetTypeId(Long baseId, Long assetTypeId);
    
    List<Purchase> findByBaseId(Long baseId);
    List<Purchase> findByAssetTypeId(Long assetTypeId);
    List<Purchase> findByBaseIdAndAssetTypeId(Long baseId, Long assetTypeId);
    
    // Date filtering methods
    long countByDateBetween(LocalDate dateFrom, LocalDate dateTo);
    long countByBaseIdAndDateBetween(Long baseId, LocalDate dateFrom, LocalDate dateTo);
    long countByAssetTypeIdAndDateBetween(Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
    long countByBaseIdAndAssetTypeIdAndDateBetween(Long baseId, Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
    
    List<Purchase> findByDateBetween(LocalDate dateFrom, LocalDate dateTo);
    List<Purchase> findByBaseIdAndDateBetween(Long baseId, LocalDate dateFrom, LocalDate dateTo);
    List<Purchase> findByAssetTypeIdAndDateBetween(Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
    List<Purchase> findByBaseIdAndAssetTypeIdAndDateBetween(Long baseId, Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
    
    // Methods with JOIN FETCH to load related entities
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy")
    List<Purchase> findAllWithRelatedEntities();
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.base.id = :baseId")
    List<Purchase> findByBaseIdWithRelatedEntities(Long baseId);
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.assetType.id = :assetTypeId")
    List<Purchase> findByAssetTypeIdWithRelatedEntities(Long assetTypeId);
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.base.id = :baseId AND p.assetType.id = :assetTypeId")
    List<Purchase> findByBaseIdAndAssetTypeIdWithRelatedEntities(Long baseId, Long assetTypeId);
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.date BETWEEN :dateFrom AND :dateTo")
    List<Purchase> findByDateBetweenWithRelatedEntities(LocalDate dateFrom, LocalDate dateTo);
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.base.id = :baseId AND p.date BETWEEN :dateFrom AND :dateTo")
    List<Purchase> findByBaseIdAndDateBetweenWithRelatedEntities(Long baseId, LocalDate dateFrom, LocalDate dateTo);
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.assetType.id = :assetTypeId AND p.date BETWEEN :dateFrom AND :dateTo")
    List<Purchase> findByAssetTypeIdAndDateBetweenWithRelatedEntities(Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
    
    @Query("SELECT p FROM Purchase p JOIN FETCH p.assetType JOIN FETCH p.base JOIN FETCH p.createdBy WHERE p.base.id = :baseId AND p.assetType.id = :assetTypeId AND p.date BETWEEN :dateFrom AND :dateTo")
    List<Purchase> findByBaseIdAndAssetTypeIdAndDateBetweenWithRelatedEntities(Long baseId, Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
} 