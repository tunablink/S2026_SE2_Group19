package com.example.cybersec.dashboard.dto;

import com.example.cybersec.module.entity.Module;
import java.util.List;

/**
 * DTO trả về thông tin dashboard cho client.
 * Lưu trữ tỷ lệ hoàn thành, danh sách module và đề xuất module tiếp theo.
 */
public class DashboardResponse {
    private int completionPercentage;
    private int completedModules;
    private int totalModules;
    private int totalQuizAttempts;
    private int averageScore;
    private String message;
    private List<Module> modules;
    private List<Module> suggestedNextModules;
    private List<Long> completedModuleIds;

    public DashboardResponse(int completionPercentage, int completedModules,
                             int totalModules, String message,
                             List<Module> modules, List<Module> suggestedNextModules) {
        this(completionPercentage, completedModules, totalModules, 0, 0, message,
                modules, suggestedNextModules, List.of());
    }

    public DashboardResponse(int completionPercentage, int completedModules,
                             int totalModules, int totalQuizAttempts, int averageScore, String message,
                             List<Module> modules, List<Module> suggestedNextModules,
                             List<Long> completedModuleIds) {
        this.completionPercentage = completionPercentage;
        this.completedModules = completedModules;
        this.totalModules = totalModules;
        this.totalQuizAttempts = totalQuizAttempts;
        this.averageScore = averageScore;
        this.message = message;
        this.modules = modules;
        this.suggestedNextModules = suggestedNextModules;
        this.completedModuleIds = completedModuleIds;
    }

    public int getCompletionPercentage() { return completionPercentage; }
    public int getCompletedModules() { return completedModules; }
    public int getTotalModules() { return totalModules; }
    public int getTotalQuizAttempts() { return totalQuizAttempts; }
    public int getAverageScore() { return averageScore; }
    public String getMessage() { return message; }
    public List<Module> getModules() { return modules; }
    public List<Module> getSuggestedNextModules() { return suggestedNextModules; }
    public List<Long> getCompletedModuleIds() { return completedModuleIds; }
}
