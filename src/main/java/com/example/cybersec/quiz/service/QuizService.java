package com.example.cybersec.quiz.service;

import com.example.cybersec.compete.service.TournamentPointService;
import com.example.cybersec.module.entity.Module;
import com.example.cybersec.module.repository.ModuleRepository;
import com.example.cybersec.quiz.dto.QuizAnswerResult;
import com.example.cybersec.quiz.dto.QuizSubmitRequest;
import com.example.cybersec.quiz.dto.QuizSubmitResponse;
import com.example.cybersec.quiz.entity.QuizAttempt;
import com.example.cybersec.quiz.entity.QuizAttemptAnswer;
import com.example.cybersec.quiz.entity.QuizQuestion;
import com.example.cybersec.quiz.repository.QuizAttemptRepository;
import com.example.cybersec.quiz.repository.QuizQuestionRepository;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service mô phỏng logic chấm điểm bài quiz cuối học phần.
 * So khớp câu trả lời với đáp án đúng để tính điểm và lưu tiến trình.
 */
@Service
public class QuizService {
    private static final int PASSING_SCORE = 70;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final TournamentPointService tournamentPointService;

    public QuizService(UserRepository userRepository,
                       ModuleRepository moduleRepository,
                       QuizQuestionRepository questionRepository,
                       QuizAttemptRepository attemptRepository,
                       TournamentPointService tournamentPointService) {
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.tournamentPointService = tournamentPointService;
    }

    @Transactional
    public QuizSubmitResponse submitQuiz(String username, QuizSubmitRequest request) {
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Answers are required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Module module = resolveModule(request);
        Map<String, String> answerKey = loadAnswerKey(module);

        int totalQuestions = answerKey.size();
        int correct = 0;
        List<QuizAnswerResult> answerResults = new ArrayList<>();
        for (Map.Entry<String, String> entry : answerKey.entrySet()) {
            String questionKey = entry.getKey();
            String expected = normalizeOption(entry.getValue());
            String submitted = normalizeOption(request.getAnswers().get(questionKey));
            boolean isCorrect = expected.equals(submitted);
            if (isCorrect) {
                correct++;
            }
            answerResults.add(new QuizAnswerResult(questionKey, submitted, expected, isCorrect));
        }

        int score = totalQuestions == 0 ? 0 : (correct * 100) / totalQuestions;
        boolean passed = score >= PASSING_SCORE;

        QuizAttempt attempt = new QuizAttempt(user, module, score, correct, totalQuestions, passed);
        for (QuizAnswerResult result : answerResults) {
            attempt.addAnswer(new QuizAttemptAnswer(
                    result.getQuestionKey(),
                    result.getSelectedOption(),
                    result.getCorrectOption(),
                    result.isCorrect()
            ));
        }
        attempt = attemptRepository.save(attempt);

        if (passed) {
            tournamentPointService.recordAfterQuizPass(username, module.getId());
        }

        String message = passed
                ? "Quiz passed. Your progress has been saved."
                : "Quiz not passed. Review the lesson and try again.";
        return new QuizSubmitResponse(score, correct, totalQuestions, passed, message, attempt.getId(), answerResults);
    }

    private Module resolveModule(QuizSubmitRequest request) {
        if (request.getModuleId() != null) {
            return moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Module not found"));
        }
        String slug = request.getModuleSlug();
        if (slug == null || slug.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Module id or slug is required");
        }
        return moduleRepository.findByName(slug.trim())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Module not found"));
    }

    private Map<String, String> loadAnswerKey(Module module) {
        List<QuizQuestion> questions = questionRepository.findByModuleOrderBySortOrderAsc(module);
        if (questions.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Quiz questions are not configured for this module");
        }
        return questions.stream().collect(Collectors.toMap(
                QuizQuestion::getQuestionKey,
                QuizQuestion::getCorrectOption,
                (a, b) -> a,
                LinkedHashMap::new
        ));
    }

    private String normalizeOption(String option) {
        if (option == null) {
            return "";
        }
        return option.trim().toUpperCase();
    }
}
