package com.example.cybersec.compete.bootstrap;

import com.example.cybersec.compete.domain.CompeteSeason;
import com.example.cybersec.compete.repository.CompeteSeasonRepository;
import com.example.cybersec.compete.service.CompeteWeekService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Ensures at least one {@link CompeteSeason} exists so weekly rows can be created (dev-friendly bootstrap).
 */
@Component
@Order(100)
public class CompeteBootstrapRunner implements ApplicationRunner {

    private final CompeteSeasonRepository seasonRepository;
    private final CompeteWeekService competeWeekService;

    public CompeteBootstrapRunner(CompeteSeasonRepository seasonRepository, CompeteWeekService competeWeekService) {
        this.seasonRepository = seasonRepository;
        this.competeWeekService = competeWeekService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (seasonRepository.count() == 0) {
            CompeteSeason s = new CompeteSeason(
                    "MVP-2026",
                    Instant.parse("2026-01-01T00:00:00Z"),
                    Instant.parse("2027-01-01T00:00:00Z")
            );
            seasonRepository.save(s);
        }
        competeWeekService.getOrCreateCurrentWeek(Instant.now());
    }
}
