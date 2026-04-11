package com.example.cybersec.quiz.repository;

import com.example.cybersec.module.entity.Module;
import com.example.cybersec.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByModuleOrderBySortOrderAsc(Module module);
    Optional<QuizQuestion> findByModuleAndQuestionKey(Module module, String questionKey);
}
