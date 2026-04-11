package com.example.cybersec.quiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_attempt_answers")
public class QuizAttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @Column(name = "question_key", nullable = false, length = 16)
    private String questionKey;

    @Column(name = "selected_option", length = 4)
    private String selectedOption;

    @Column(name = "correct_option", nullable = false, length = 4)
    private String correctOption;

    @Column(nullable = false)
    private boolean correct;

    protected QuizAttemptAnswer() {
    }

    public QuizAttemptAnswer(String questionKey, String selectedOption, String correctOption, boolean correct) {
        this.questionKey = questionKey;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(QuizAttempt attempt) {
        this.attempt = attempt;
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
