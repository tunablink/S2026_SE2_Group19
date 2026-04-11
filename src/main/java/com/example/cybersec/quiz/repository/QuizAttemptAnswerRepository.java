package com.example.cybersec.quiz.repository;

import com.example.cybersec.quiz.entity.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptAnswerRepository extends JpaRepository<QuizAttemptAnswer, Long> {
}
