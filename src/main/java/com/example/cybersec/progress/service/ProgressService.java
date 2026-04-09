package com.example.cybersec.progress.service;

import com.example.cybersec.progress.dto.ProgressSummary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service theo dõi và tính toán tiến trình học tập.
 * Lưu trữ điểm quiz và danh sách module đã hoàn thành (tạm in-memory).
 */
@Service
public class ProgressService {
    private final Map<String, List<Integer>> quizScoresByUser = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> completedModulesByUser = new ConcurrentHashMap<>();

    public void recordQuizResult(String username, Long moduleId, int score, boolean passed) {
        quizScoresByUser.computeIfAbsent(username, k -> new ArrayList<>()).add(score);

        if (passed) {
            completedModulesByUser.computeIfAbsent(username, k -> new ArrayList<>());
            List<Long> modules = completedModulesByUser.get(username);
            if (!modules.contains(moduleId)) {
                modules.add(moduleId);
                modules.sort(Comparator.naturalOrder());
            }
        }
    }

    public ProgressSummary getProgress(String username, int totalModules) {
        List<Integer> scores = quizScoresByUser.getOrDefault(username, List.of());
        List<Long> completed = completedModulesByUser.getOrDefault(username, List.of());

        int totalAttempts = scores.size();
        int averageScore = totalAttempts == 0
                ? 0
                : (int) Math.round(scores.stream().mapToInt(Integer::intValue).average().orElse(0));
        int completionPct = totalModules == 0
                ? 0
                : (completed.size() * 100) / totalModules;

        return new ProgressSummary(completionPct, totalAttempts, averageScore, completed);
    }
}
