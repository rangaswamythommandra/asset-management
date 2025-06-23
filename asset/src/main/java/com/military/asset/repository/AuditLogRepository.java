package com.military.asset.repository;

import com.military.asset.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user")
    List<AuditLog> findAllWithDetails();
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.user.base.id = :baseId")
    List<AuditLog> findByUserBaseId(@Param("baseId") Long baseId);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.entity = :entity")
    List<AuditLog> findByEntity(@Param("entity") String entity);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.user.base.id = :baseId AND al.entity = :entity")
    List<AuditLog> findByUserBaseIdAndEntity(@Param("baseId") Long baseId, @Param("entity") String entity);
    
    // Date range filtering with JOIN FETCH
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.timestamp BETWEEN :dateFrom AND :dateTo")
    List<AuditLog> findByTimestampBetween(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.user.base.id = :baseId AND al.timestamp BETWEEN :dateFrom AND :dateTo")
    List<AuditLog> findByUserBaseIdAndTimestampBetween(@Param("baseId") Long baseId, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.entity = :entity AND al.timestamp BETWEEN :dateFrom AND :dateTo")
    List<AuditLog> findByEntityAndTimestampBetween(@Param("entity") String entity, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.user.base.id = :baseId AND al.entity = :entity AND al.timestamp BETWEEN :dateFrom AND :dateTo")
    List<AuditLog> findByUserBaseIdAndEntityAndTimestampBetween(@Param("baseId") Long baseId, @Param("entity") String entity, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
    
    // Asset type filtering (for assets) with JOIN FETCH
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.entity = 'ASSET' AND al.entityId IN " +
           "(SELECT a.id FROM Asset a WHERE a.assetType.id = :assetTypeId)")
    List<AuditLog> findByAssetTypeId(@Param("assetTypeId") Long assetTypeId);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.user.base.id = :baseId AND al.entity = 'ASSET' AND al.entityId IN " +
           "(SELECT a.id FROM Asset a WHERE a.assetType.id = :assetTypeId)")
    List<AuditLog> findByBaseIdAndAssetTypeId(@Param("baseId") Long baseId, @Param("assetTypeId") Long assetTypeId);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.entity = 'ASSET' AND al.entityId IN " +
           "(SELECT a.id FROM Asset a WHERE a.assetType.id = :assetTypeId) AND al.timestamp BETWEEN :dateFrom AND :dateTo")
    List<AuditLog> findByAssetTypeIdAndTimestampBetween(@Param("assetTypeId") Long assetTypeId, 
                                                        @Param("dateFrom") LocalDateTime dateFrom, 
                                                        @Param("dateTo") LocalDateTime dateTo);
    
    @Query("SELECT al FROM AuditLog al JOIN FETCH al.user WHERE al.user.base.id = :baseId AND al.entity = 'ASSET' AND al.entityId IN " +
           "(SELECT a.id FROM Asset a WHERE a.assetType.id = :assetTypeId) AND al.timestamp BETWEEN :dateFrom AND :dateTo")
    List<AuditLog> findByBaseIdAndAssetTypeIdAndTimestampBetween(@Param("baseId") Long baseId, 
                                                                 @Param("assetTypeId") Long assetTypeId,
                                                                 @Param("dateFrom") LocalDateTime dateFrom, 
                                                                 @Param("dateTo") LocalDateTime dateTo);
} 