package com.example.cybersec.dashboard.service;

import com.example.cybersec.dashboard.dto.DashboardResponse;
import com.example.cybersec.module.entity.Module;
import com.example.cybersec.module.repository.ModuleRepository;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Service cung cấp dữ liệu cho hiển thị Dashboard.
 * Tính toán tỷ lệ hoàn thành và gợi ý lộ trình tiếp theo.
 *
 * NOTE: DashboardRepository đã bị loại bỏ vì chỉ là wrapper rỗng.
 * Sử dụng trực tiếp ModuleRepository.
 */
@Service
public class DashboardService {
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    public DashboardService(ModuleRepository moduleRepository, UserRepository userRepository) {
        this.moduleRepository = moduleRepository;
        this.userRepository = userRepository;
    }

    public DashboardResponse getLearningPathForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "User is not available."));

        if (!hasLearnerAccess(user.getRoles())) {
            throw new ResponseStatusException(FORBIDDEN, "You do not have permission to view learner dashboard.");
        }

        List<Module> modules = moduleRepository.findAll();
        int totalModules = modules.size();

        // NOTE: Module entity currently has no 'status' field.
        // completedModules logic will need a proper progress tracking mechanism.
        // For now, default to 0 completed until progress entity is added.
        int completedModules = 0;

        int completionPercentage = totalModules == 0 ? 0 : (completedModules * 100) / totalModules;
        String message = totalModules == 0
                ? "You haven't started learning yet. Please select your first module."
                : "Dashboard loaded successfully.";

        List<Module> suggestedNextModules = modules.stream()
                .limit(2)
                .collect(Collectors.toList());

        return new DashboardResponse(
                completionPercentage, completedModules, totalModules,
                message, modules, suggestedNextModules
        );
    }

    private boolean hasLearnerAccess(String roles) {
        if (roles == null || roles.isBlank()) return false;
        return roles.contains("USER") || roles.contains("LEARNER") || roles.contains("ADMIN");
    }
}
