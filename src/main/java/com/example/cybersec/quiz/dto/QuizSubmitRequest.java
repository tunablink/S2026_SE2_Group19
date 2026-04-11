package com.example.cybersec.quiz.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO nhận phản hồi quiz của học viên.
 * Chứa mã ID module và danh sách câu trả lời.
 */
public class QuizSubmitRequest {
    private Long moduleId;

    private String moduleSlug;

    @NotNull(message = "Answers are required")
    private Map<String, String> answers;

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public String getModuleSlug() { return moduleSlug; }
    public void setModuleSlug(String moduleSlug) { this.moduleSlug = moduleSlug; }
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
}
