package com.military.asset.controller;

import com.military.asset.model.Base;
import com.military.asset.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class BaseController {
    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @GetMapping
    public ResponseEntity<List<Base>> getAllBases() {
        return ResponseEntity.ok(baseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Base> getBaseById(@PathVariable Long id) {
        return baseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Base> createBase(@RequestBody Base base) {
        return ResponseEntity.ok(baseService.save(base));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Base> updateBase(@PathVariable Long id, @RequestBody Base base) {
        return baseService.findById(id)
                .map(existing -> {
                    base.setId(id);
                    return ResponseEntity.ok(baseService.save(base));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBase(@PathVariable Long id) {
        baseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 