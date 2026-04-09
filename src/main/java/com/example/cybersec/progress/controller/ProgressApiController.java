package com.example.cybersec.progress.controller;

import com.example.cybersec.module.service.ModuleService;
import com.example.cybersec.progress.dto.ProgressSummary;
import com.example.cybersec.progress.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST controller cho tiến độ học tập.
 * Cung cấp phân tích real-time về quiz, điểm trung bình và % hoàn thành.
 */
@RestController
@RequestMapping("/api/progress")
public class ProgressApiController {
    private final ProgressService progressService;
    private final ModuleService moduleService;

    public ProgressApiController(ProgressService progressService, ModuleService moduleService) {
        this.progressService = progressService;
        this.moduleService = moduleService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProgressSummary> getMyProgress(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
        int totalModules = moduleService.getAllModules().size();
        ProgressSummary response = progressService.getProgress(authentication.getName(), totalModules);
        return ResponseEntity.ok(response);
    }
}
