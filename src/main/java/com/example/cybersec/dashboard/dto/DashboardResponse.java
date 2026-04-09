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
    private String message;
    private List<Module> modules;
    private List<Module> suggestedNextModules;

    public DashboardResponse(int completionPercentage, int completedModules,
                             int totalModules, String message,
                             List<Module> modules, List<Module> suggestedNextModules) {
        this.completionPercentage = completionPercentage;
        this.completedModules = completedModules;
        this.totalModules = totalModules;
        this.message = message;
        this.modules = modules;
        this.suggestedNextModules = suggestedNextModules;
    }

    public int getCompletionPercentage() { return completionPercentage; }
    public int getCompletedModules() { return completedModules; }
    public int getTotalModules() { return totalModules; }
    public String getMessage() { return message; }
    public List<Module> getModules() { return modules; }
    public List<Module> getSuggestedNextModules() { return suggestedNextModules; }
}
