package com.military.asset.service;

import com.military.asset.model.Expenditure;
import com.military.asset.repository.ExpenditureRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenditureService {
    private final ExpenditureRepository expenditureRepository;
    public ExpenditureService(ExpenditureRepository expenditureRepository) { this.expenditureRepository = expenditureRepository; }

    public List<Expenditure> findAll() { return expenditureRepository.findAllWithDetails(); }
    public Optional<Expenditure> findById(Long id) { return expenditureRepository.findById(id); }
    public Expenditure save(Expenditure expenditure) { return expenditureRepository.save(expenditure); }
    public void deleteById(Long id) { expenditureRepository.deleteById(id); }
    
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
            return expenditureRepository.findByBaseIdAndAssetIdAndExpenditureDateBetween(baseId, assetId, fromDate, toDate).size();
        } else if (baseId != null && fromDate != null && toDate != null) {
            return expenditureRepository.findByBaseIdAndExpenditureDateBetween(baseId, fromDate, toDate).size();
        } else if (assetId != null && fromDate != null && toDate != null) {
            return expenditureRepository.findByAssetIdAndExpenditureDateBetween(assetId, fromDate, toDate).size();
        } else if (fromDate != null && toDate != null) {
            return expenditureRepository.findByExpenditureDateBetween(fromDate, toDate).size();
        } else if (baseId != null && assetId != null) {
            return expenditureRepository.findByBaseIdAndAssetId(baseId, assetId).size();
        } else if (baseId != null) {
            return expenditureRepository.findByBaseId(baseId).size();
        } else if (assetId != null) {
            return expenditureRepository.findByAssetId(assetId).size();
        } else {
            return expenditureRepository.count();
        }
    }
    
    public List<Expenditure> findByFilters(Long baseId, Long assetId, String dateFrom, String dateTo) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            fromDate = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            toDate = LocalDate.parse(dateTo);
        }
        if (baseId != null && assetId != null && fromDate != null && toDate != null) {
            return expenditureRepository.findByBaseIdAndAssetIdAndExpenditureDateBetween(baseId, assetId, fromDate, toDate);
        } else if (baseId != null && fromDate != null && toDate != null) {
            return expenditureRepository.findByBaseIdAndExpenditureDateBetween(baseId, fromDate, toDate);
        } else if (assetId != null && fromDate != null && toDate != null) {
            return expenditureRepository.findByAssetIdAndExpenditureDateBetween(assetId, fromDate, toDate);
        } else if (fromDate != null && toDate != null) {
            return expenditureRepository.findByExpenditureDateBetween(fromDate, toDate);
        } else if (baseId != null && assetId != null) {
            return expenditureRepository.findByBaseIdAndAssetId(baseId, assetId);
        } else if (baseId != null) {
            return expenditureRepository.findByBaseId(baseId);
        } else if (assetId != null) {
            return expenditureRepository.findByAssetId(assetId);
        } else {
            return expenditureRepository.findAllWithDetails();
        }
    }
} 