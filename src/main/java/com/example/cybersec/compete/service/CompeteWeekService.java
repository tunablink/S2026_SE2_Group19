package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteSeason;
import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.repository.CompeteSeasonRepository;
import com.example.cybersec.compete.repository.CompeteWeekRepository;
import com.example.cybersec.compete.support.CompeteUtcTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Resolves the active UTC week and creates the next row when Monday rolls over.
 */
@Service
public class CompeteWeekService {

    private static final String DEFAULT_SEASON_CODE = "MVP-2026";
    private static final Instant DEFAULT_SEASON_START = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant DEFAULT_SEASON_END = Instant.parse("2027-01-01T00:00:00Z");

    private final CompeteSeasonRepository seasonRepository;
    private final CompeteWeekRepository weekRepository;

    public CompeteWeekService(CompeteSeasonRepository seasonRepository, CompeteWeekRepository weekRepository) {
        this.seasonRepository = seasonRepository;
        this.weekRepository = weekRepository;
    }

    public Optional<CompeteWeek> findCurrentWeek(Instant now) {
        return weekRepository.findTopByWeekStartUtcLessThanEqualAndWeekEndUtcGreaterThanEqualOrderByWeekStartUtcDesc(
                now, now);
    }

    /**
     * If the DB has no row for this calendar week, attach one to the latest season.
     */
    @Transactional
    public CompeteWeek getOrCreateCurrentWeek(Instant now) {
        Optional<CompeteWeek> currentWeek = findCurrentWeek(now);
        if (currentWeek.isPresent()) {
            return currentWeek.get();
        }

        CompeteSeason season = resolveOrCreateSeason(now);
        Instant start = CompeteUtcTime.startOfWeekMondayUtc(now);
        Optional<CompeteWeek> weekByStart = weekRepository.findByWeekStartUtc(start);
        if (weekByStart.isPresent()) {
            return weekByStart.get();
        }

        long existingWeeks = weekRepository.countBySeason_Id(season.getId());
        int weekIndex = (int) ((existingWeeks % 8) + 1);
        Instant end = CompeteUtcTime.endOfWeekSundayUtc(start);
        CompeteWeek week = new CompeteWeek(season, weekIndex, start, end);
        return weekRepository.save(week);
    }

    @Transactional
    public void freezeIfInFinalWindow(CompeteWeek week, Instant now) {
        Instant freezeAt = week.getWeekEndUtc().minusSeconds(15 * 60);
        if (!now.isBefore(freezeAt)) {
            week.setFrozen(true);
            weekRepository.save(week);
        }
    }

    public boolean isWeekClosedForScoring(CompeteWeek week, Instant now) {
        return week.isFrozen() || now.isAfter(week.getWeekEndUtc());
    }

    private CompeteSeason resolveOrCreateSeason(Instant now) {
        return seasonRepository.findFirstByOrderByStartsAtUtcDesc().orElseGet(() -> {
            Instant startsAt = now.isBefore(DEFAULT_SEASON_START) ? now : DEFAULT_SEASON_START;
            Instant endsAt = now.isAfter(DEFAULT_SEASON_END)
                    ? now.plusSeconds(365L * 24 * 60 * 60)
                    : DEFAULT_SEASON_END;
            return seasonRepository.save(new CompeteSeason(DEFAULT_SEASON_CODE, startsAt, endsAt));
        });
    }
}
