package com.example.cybersec.compete.repository;

import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.TournamentPointEvent;
import com.example.cybersec.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface TournamentPointEventRepository extends JpaRepository<TournamentPointEvent, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<TournamentPointEvent> findByIdempotencyKey(String idempotencyKey);

    long countByUserAndWeekAndSourceKey(User user, CompeteWeek week, String sourceKey);

    @Query("""
            SELECT COALESCE(SUM(e.deltaTp), 0) FROM TournamentPointEvent e
            WHERE e.user = :user AND e.createdAt >= :since
            """)
    int sumDeltaTpSince(@Param("user") User user, @Param("since") Instant since);
}
