package com.military.asset.service;

import com.military.asset.model.Base;
import com.military.asset.repository.BaseRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BaseService {
    private final BaseRepository baseRepository;
    public BaseService(BaseRepository baseRepository) { this.baseRepository = baseRepository; }

    public List<Base> findAll() { return baseRepository.findAll(); }
    public Optional<Base> findById(Long id) { return baseRepository.findById(id); }
    public Base save(Base base) { return baseRepository.save(base); }
    public void deleteById(Long id) { baseRepository.deleteById(id); }
} 