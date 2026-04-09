package com.example.cybersec.compete.repository;

import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.CompeteWeekCloseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CompeteWeekRepository extends JpaRepository<CompeteWeek, Long> {

    Optional<CompeteWeek> findByWeekStartUtc(Instant weekStartUtc);

    Optional<CompeteWeek> findTopByWeekStartUtcLessThanEqualAndWeekEndUtcGreaterThanEqualOrderByWeekStartUtcDesc(
            Instant nowStart,
            Instant nowEnd);

    List<CompeteWeek> findByCloseStatusAndWeekEndUtcBefore(CompeteWeekCloseStatus status, Instant now);

    long countBySeason_Id(Long seasonId);
}
