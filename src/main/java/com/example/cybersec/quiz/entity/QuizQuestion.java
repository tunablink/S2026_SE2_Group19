package com.example.cybersec.quiz.entity;

import com.example.cybersec.module.entity.Module;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "quiz_questions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "question_key"})
)
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "question_key", nullable = false, length = 16)
    private String questionKey;

    @Column(name = "correct_option", nullable = false, length = 4)
    private String correctOption;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected QuizQuestion() {
    }

    public QuizQuestion(Module module, String questionKey, String correctOption, int sortOrder) {
        this.module = module;
        this.questionKey = questionKey;
        this.correctOption = correctOption;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getQuestionKey() {
        return questionKey;
    }

    public void setQuestionKey(String questionKey) {
        this.questionKey = questionKey;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
