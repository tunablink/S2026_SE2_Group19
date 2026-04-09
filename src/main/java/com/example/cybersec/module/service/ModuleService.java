package com.example.cybersec.module.service;

import com.example.cybersec.module.entity.Module;
import com.example.cybersec.module.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service đảm nhiệm việc lấy dữ liệu các bài học (Module).
 * Truy vấn danh sách toàn bộ module hoặc một module cụ thể.
 */
@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    /** Fixed: use findById instead of loading all modules and filtering */
    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Module not found"));
    }
}
