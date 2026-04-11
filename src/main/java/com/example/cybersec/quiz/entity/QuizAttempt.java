package com.example.cybersec.quiz.entity;

import com.example.cybersec.module.entity.Module;
import com.example.cybersec.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private int score;

    @Column(name = "correct_count", nullable = false)
    private int correctCount;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private boolean passed;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttemptAnswer> answers = new ArrayList<>();

    protected QuizAttempt() {
    }

    public QuizAttempt(User user, Module module, int score, int correctCount, int totalQuestions, boolean passed) {
        this.user = user;
        this.module = module;
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.submittedAt = Instant.now();
    }

    public void addAnswer(QuizAttemptAnswer answer) {
        answer.setAttempt(this);
        answers.add(answer);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Module getModule() {
        return module;
    }

    public int getScore() {
        return score;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public boolean isPassed() {
        return passed;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public List<QuizAttemptAnswer> getAnswers() {
        return answers;
    }
}
