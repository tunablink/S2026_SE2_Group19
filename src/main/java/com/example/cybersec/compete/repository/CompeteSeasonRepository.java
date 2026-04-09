package com.example.cybersec.compete.repository;

import com.example.cybersec.compete.domain.CompeteSeason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompeteSeasonRepository extends JpaRepository<CompeteSeason, Long> {
    Optional<CompeteSeason> findFirstByOrderByStartsAtUtcAsc();

    Optional<CompeteSeason> findFirstByOrderByStartsAtUtcDesc();
}
