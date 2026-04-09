package com.example.cybersec.module.controller;

import com.example.cybersec.module.entity.Module;
import com.example.cybersec.module.service.ModuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller để lấy danh sách học phần (modules).
 * Cung cấp API read-only cho client tải nội dung khóa học.
 */
@RestController
@RequestMapping("/api")
public class ModuleApiController {

    private final ModuleService moduleService;

    public ModuleApiController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/modules")
    public List<Module> getModules() {
        return moduleService.getAllModules();
    }

    @GetMapping("/modules/{id}")
    public Module getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id);
    }
}
