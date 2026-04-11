package com.example.cybersec.quiz.dto;

public class QuizAnswerResult {
    private String questionKey;
    private String selectedOption;
    private String correctOption;
    private boolean correct;

    public QuizAnswerResult(String questionKey, String selectedOption, String correctOption, boolean correct) {
        this.questionKey = questionKey;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.correct = correct;
    }

    public String getQuestionKey() {
        return questionKey;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public boolean isCorrect() {
        return correct;
    }
}
