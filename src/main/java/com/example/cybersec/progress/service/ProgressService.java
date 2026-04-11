package com.example.cybersec.progress.service;

import com.example.cybersec.progress.dto.ProgressSummary;
import com.example.cybersec.quiz.repository.QuizAttemptRepository;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service theo dõi và tính toán tiến trình học tập.
 * Lưu trữ điểm quiz và danh sách module đã hoàn thành (tạm in-memory).
 */
@Service
public class ProgressService {
    private final UserRepository userRepository;
    private final QuizAttemptRepository attemptRepository;

    public ProgressService(UserRepository userRepository, QuizAttemptRepository attemptRepository) {
        this.userRepository = userRepository;
        this.attemptRepository = attemptRepository;
    }

    public ProgressSummary getProgress(String username, int totalModules) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ProgressSummary(0, 0, 0, List.of());
        }

        List<Long> completed = attemptRepository.findCompletedModuleIdsByUser(user).stream()
                .sorted()
                .toList();
        int totalAttempts = (int) attemptRepository.countByUser(user);
        int averageScore = totalAttempts == 0 ? 0 : (int) Math.round(attemptRepository.averageScoreByUser(user));
        int completionPct = totalModules == 0
                ? 0
                : (completed.size() * 100) / totalModules;

        return new ProgressSummary(completionPct, totalAttempts, averageScore, completed);
    }
}
