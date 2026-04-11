package com.example.cybersec.quiz.repository;

import com.example.cybersec.quiz.entity.QuizAttempt;
import com.example.cybersec.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    long countByUser(User user);

    long countByUserAndSubmittedAtGreaterThanEqual(User user, Instant since);

    @Query("SELECT COALESCE(AVG(a.score), 0) FROM QuizAttempt a WHERE a.user = :user")
    double averageScoreByUser(@Param("user") User user);

    @Query("""
            SELECT DISTINCT a.module.id FROM QuizAttempt a
            WHERE a.user = :user AND a.passed = true
            """)
    List<Long> findCompletedModuleIdsByUser(@Param("user") User user);
}
