package com.military.asset.repository;

import com.military.asset.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    long countByFromBaseId(Long fromBaseId);
    long countByAssetAssetTypeId(Long assetTypeId);
    long countByFromBaseIdAndAssetAssetTypeId(Long fromBaseId, Long assetTypeId);
    
    List<Transfer> findByFromBaseId(Long fromBaseId);
    List<Transfer> findByAssetAssetTypeId(Long assetTypeId);
    List<Transfer> findByFromBaseIdAndAssetAssetTypeId(Long fromBaseId, Long assetTypeId);

    // Additional methods for service compatibility
    List<Transfer> findByAssetId(Long assetId);
    List<Transfer> findByFromBaseIdAndDateBetween(Long fromBaseId, LocalDate dateFrom, LocalDate dateTo);
    List<Transfer> findByAssetIdAndDateBetween(Long assetId, LocalDate dateFrom, LocalDate dateTo);
    List<Transfer> findByFromBaseIdAndAssetIdAndDateBetween(Long fromBaseId, Long assetId, LocalDate dateFrom, LocalDate dateTo);
    List<Transfer> findByDateBetween(LocalDate dateFrom, LocalDate dateTo);
    List<Transfer> findByFromBaseIdAndAssetId(Long fromBaseId, Long assetId);
    
    // Methods with JOIN FETCH to load related entities
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy")
    List<Transfer> findAllWithRelatedEntities();
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE t.fromBase.id = :fromBaseId")
    List<Transfer> findByFromBaseIdWithRelatedEntities(Long fromBaseId);
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE a.assetType.id = :assetTypeId")
    List<Transfer> findByAssetAssetTypeIdWithRelatedEntities(Long assetTypeId);
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE t.fromBase.id = :fromBaseId AND a.assetType.id = :assetTypeId")
    List<Transfer> findByFromBaseIdAndAssetAssetTypeIdWithRelatedEntities(Long fromBaseId, Long assetTypeId);
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE t.date BETWEEN :dateFrom AND :dateTo")
    List<Transfer> findByDateBetweenWithRelatedEntities(LocalDate dateFrom, LocalDate dateTo);
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE t.fromBase.id = :fromBaseId AND t.date BETWEEN :dateFrom AND :dateTo")
    List<Transfer> findByFromBaseIdAndDateBetweenWithRelatedEntities(Long fromBaseId, LocalDate dateFrom, LocalDate dateTo);
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE a.assetType.id = :assetTypeId AND t.date BETWEEN :dateFrom AND :dateTo")
    List<Transfer> findByAssetAssetTypeIdAndDateBetweenWithRelatedEntities(Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
    
    @Query("SELECT t FROM Transfer t JOIN FETCH t.asset a JOIN FETCH a.assetType JOIN FETCH t.fromBase JOIN FETCH t.toBase JOIN FETCH t.createdBy LEFT JOIN FETCH t.approvedBy WHERE t.fromBase.id = :fromBaseId AND a.assetType.id = :assetTypeId AND t.date BETWEEN :dateFrom AND :dateTo")
    List<Transfer> findByFromBaseIdAndAssetAssetTypeIdAndDateBetweenWithRelatedEntities(Long fromBaseId, Long assetTypeId, LocalDate dateFrom, LocalDate dateTo);
} 