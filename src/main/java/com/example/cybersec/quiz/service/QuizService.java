package com.example.cybersec.quiz.service;

import com.example.cybersec.compete.service.TournamentPointService;
import com.example.cybersec.progress.service.ProgressService;
import com.example.cybersec.quiz.dto.QuizSubmitRequest;
import com.example.cybersec.quiz.dto.QuizSubmitResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service mô phỏng logic chấm điểm bài quiz cuối học phần.
 * So khớp câu trả lời với đáp án đúng để tính điểm và lưu tiến trình.
 */
@Service
public class QuizService {
    private static final int PASSING_SCORE = 70;
    private final ProgressService progressService;
    private final TournamentPointService tournamentPointService;

    public QuizService(ProgressService progressService, TournamentPointService tournamentPointService) {
        this.progressService = progressService;
        this.tournamentPointService = tournamentPointService;
    }

    public QuizSubmitResponse submitQuiz(String username, QuizSubmitRequest request) {
        // Sample answer key; can be replaced by DB-backed quiz/questions later.
        Map<String, String> answerKey = Map.of("Q1", "A", "Q2", "B", "Q3", "C");

        int totalQuestions = answerKey.size();
        int correct = 0;
        for (Map.Entry<String, String> entry : answerKey.entrySet()) {
            String submitted = request.getAnswers().get(entry.getKey());
            if (entry.getValue().equalsIgnoreCase(submitted)) {
                correct++;
            }
        }

        int score = totalQuestions == 0 ? 0 : (correct * 100) / totalQuestions;
        boolean passed = score >= PASSING_SCORE;

        progressService.recordQuizResult(username, request.getModuleId(), score, passed);
        if (passed) {
            tournamentPointService.recordAfterQuizPass(username, request.getModuleId());
        }

        String message = passed
                ? "Quiz passed. Next module can be unlocked."
                : "Quiz not passed. Please review theory and labs, then retry.";
        return new QuizSubmitResponse(score, passed, message);
    }
}
