package com.example.cybersec.dashboard.controller;

import com.example.cybersec.dashboard.dto.DashboardResponse;
import com.example.cybersec.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST controller truy xuất dữ liệu dashboard qua API.
 * Cung cấp lộ trình học tập và thống kê tiến độ cá nhân.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/learning-path")
    public ResponseEntity<DashboardResponse> getDashboardLearningPath(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
        DashboardResponse response = dashboardService.getLearningPathForUser(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
