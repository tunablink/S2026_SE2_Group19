package com.example.cybersec.quiz.controller;

import com.example.cybersec.quiz.dto.QuizSubmitRequest;
import com.example.cybersec.quiz.dto.QuizSubmitResponse;
import com.example.cybersec.quiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST endpoint xử lý trả lời Quiz và chấm điểm.
 * Nhận đáp án, tính toán điểm số và phản hồi lập tức.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizApiController {
    private final QuizService quizService;

    public QuizApiController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizSubmitResponse> submitQuiz(@Valid @RequestBody QuizSubmitRequest request,
                                                         Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
        QuizSubmitResponse response = quizService.submitQuiz(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}
