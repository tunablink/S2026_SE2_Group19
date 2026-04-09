package com.example.cybersec.compete.repository;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.CompeteEnrollmentStatus;
import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.TournamentEnrollment;
import com.example.cybersec.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentEnrollmentRepository extends JpaRepository<TournamentEnrollment, Long> {

    Optional<TournamentEnrollment> findByUserAndWeek(User user, CompeteWeek week);

    long countByUser(User user);

    List<TournamentEnrollment> findByWeekAndStatus(CompeteWeek week, CompeteEnrollmentStatus status);

    @Query("""
            SELECT e FROM TournamentEnrollment e
            WHERE e.week = :week
              AND e.status = :status
              AND e.bracket = :bracket
              AND e.timezoneBand = :band
              AND e.optedInLeaderboard = true
            ORDER BY e.tpTotal DESC, e.firstTpAt ASC, e.id ASC
            """)
    List<TournamentEnrollment> findLeaderboardCohort(
            @Param("week") CompeteWeek week,
            @Param("status") CompeteEnrollmentStatus status,
            @Param("bracket") CompeteBracket bracket,
            @Param("band") String band);
}
