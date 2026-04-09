package com.example.cybersec.quiz.dto;

/**
 * DTO trả về kết quả bài quiz.
 * Mang theo điểm số, trạng thái pass/fail và thông điệp hướng dẫn.
 */
public class QuizSubmitResponse {
    private int score;
    private boolean passed;
    private String message;

    public QuizSubmitResponse(int score, boolean passed, String message) {
        this.score = score;
        this.passed = passed;
        this.message = message;
    }

    public int getScore() { return score; }
    public boolean isPassed() { return passed; }
    public String getMessage() { return message; }
}
