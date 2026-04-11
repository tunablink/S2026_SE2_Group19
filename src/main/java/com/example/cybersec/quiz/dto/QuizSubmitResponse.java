package com.example.cybersec.quiz.dto;

/**
 * DTO trả về kết quả bài quiz.
 * Mang theo điểm số, trạng thái pass/fail và thông điệp hướng dẫn.
 */
public class QuizSubmitResponse {
    private int score;
    private int correctCount;
    private int totalQuestions;
    private boolean passed;
    private String message;
    private Long attemptId;
    private java.util.List<QuizAnswerResult> answers;

    public QuizSubmitResponse(int score, boolean passed, String message) {
        this(score, 0, 0, passed, message, null, java.util.List.of());
    }

    public QuizSubmitResponse(int score, int correctCount, int totalQuestions, boolean passed,
                              String message, Long attemptId, java.util.List<QuizAnswerResult> answers) {
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.message = message;
        this.attemptId = attemptId;
        this.answers = answers;
    }

    public int getScore() { return score; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalQuestions() { return totalQuestions; }
    public boolean isPassed() { return passed; }
    public String getMessage() { return message; }
    public Long getAttemptId() { return attemptId; }
    public java.util.List<QuizAnswerResult> getAnswers() { return answers; }
}
