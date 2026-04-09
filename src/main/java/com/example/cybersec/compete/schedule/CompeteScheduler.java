package com.example.cybersec.compete.schedule;

import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.CompeteWeekCloseStatus;
import com.example.cybersec.compete.repository.CompeteWeekRepository;
import com.example.cybersec.compete.service.CompeteWeekService;
import com.example.cybersec.compete.service.TournamentCloseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Rolling week creation, freeze window, and week-close pipeline.
 */
@Component
public class CompeteScheduler {

    private final CompeteWeekService competeWeekService;
    private final CompeteWeekRepository weekRepository;
    private final TournamentCloseService tournamentCloseService;

    public CompeteScheduler(
            CompeteWeekService competeWeekService,
            CompeteWeekRepository weekRepository,
            TournamentCloseService tournamentCloseService) {
        this.competeWeekService = competeWeekService;
        this.weekRepository = weekRepository;
        this.tournamentCloseService = tournamentCloseService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyMaintenance() {
        Instant now = Instant.now();
        competeWeekService.getOrCreateCurrentWeek(now);
        competeWeekService.findCurrentWeek(now).ifPresent(w -> competeWeekService.freezeIfInFinalWindow(w, now));
    }

    /**
     * Monday 00:05 server default timezone — align JVM TZ to UTC in production if needed.
     */
    @Scheduled(cron = "0 5 0 ? * MON")
    public void weeklyClose() {
        Instant now = Instant.now();
        List<CompeteWeek> due = weekRepository.findByCloseStatusAndWeekEndUtcBefore(
                CompeteWeekCloseStatus.OPEN, now);
        for (CompeteWeek w : due) {
            tournamentCloseService.closeWeek(w);
        }
    }
}
