package com.example.cybersec.progress.dto;

import java.util.List;

/**
 * DTO tổng hợp tiến độ học tập của người dùng.
 * Thống kê số lần làm quiz, điểm số trung bình và tỉ lệ hoàn thành.
 */
public class ProgressSummary {
    private int completionPercentage;
    private int totalQuizAttempts;
    private int averageScore;
    private List<Long> completedModuleIds;

    public ProgressSummary(int completionPercentage, int totalQuizAttempts,
                           int averageScore, List<Long> completedModuleIds) {
        this.completionPercentage = completionPercentage;
        this.totalQuizAttempts = totalQuizAttempts;
        this.averageScore = averageScore;
        this.completedModuleIds = completedModuleIds;
    }

    public int getCompletionPercentage() { return completionPercentage; }
    public int getTotalQuizAttempts() { return totalQuizAttempts; }
    public int getAverageScore() { return averageScore; }
    public List<Long> getCompletedModuleIds() { return completedModuleIds; }
}
