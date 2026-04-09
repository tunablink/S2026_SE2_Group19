package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteEnrollmentStatus;
import com.example.cybersec.compete.domain.CompeteRewardStatus;
import com.example.cybersec.compete.domain.CompeteSeason;
import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.CompeteWeekCloseStatus;
import com.example.cybersec.compete.domain.RewardGrant;
import com.example.cybersec.compete.domain.SeasonStanding;
import com.example.cybersec.compete.domain.TournamentEnrollment;
import com.example.cybersec.compete.domain.WeeklyStanding;
import com.example.cybersec.compete.repository.CompeteWeekRepository;
import com.example.cybersec.compete.repository.RewardGrantRepository;
import com.example.cybersec.compete.repository.SeasonStandingRepository;
import com.example.cybersec.compete.repository.TournamentEnrollmentRepository;
import com.example.cybersec.compete.repository.WeeklyStandingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Finalizes weeks: standings, season points, and pending rewards. Idempotent per week ({@link CompeteWeekCloseStatus}).
 */
@Service
public class TournamentCloseService {

    private final CompeteWeekRepository weekRepository;
    private final TournamentEnrollmentRepository enrollmentRepository;
    private final WeeklyStandingRepository weeklyStandingRepository;
    private final SeasonStandingRepository seasonStandingRepository;
    private final RewardGrantRepository rewardGrantRepository;

    public TournamentCloseService(
            CompeteWeekRepository weekRepository,
            TournamentEnrollmentRepository enrollmentRepository,
            WeeklyStandingRepository weeklyStandingRepository,
            SeasonStandingRepository seasonStandingRepository,
            RewardGrantRepository rewardGrantRepository) {
        this.weekRepository = weekRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.weeklyStandingRepository = weeklyStandingRepository;
        this.seasonStandingRepository = seasonStandingRepository;
        this.rewardGrantRepository = rewardGrantRepository;
    }

    @Transactional
    public void closeWeek(CompeteWeek week) {
        CompeteWeek managed = weekRepository.findById(week.getId()).orElseThrow();
        if (managed.getCloseStatus() != CompeteWeekCloseStatus.OPEN) {
            return;
        }
        managed.setCloseStatus(CompeteWeekCloseStatus.CLOSING);
        weekRepository.saveAndFlush(managed);

        List<TournamentEnrollment> active = enrollmentRepository.findByWeekAndStatus(
                managed, CompeteEnrollmentStatus.ACTIVE);

        Map<String, List<TournamentEnrollment>> cohorts = active.stream()
                .filter(TournamentEnrollment::isOptedInLeaderboard)
                .collect(Collectors.groupingBy(e -> e.getBracket().name() + "|" + e.getTimezoneBand()));

        CompeteSeason season = managed.getSeason();
        Long seasonId = season.getId();

        for (List<TournamentEnrollment> cohort : cohorts.values()) {
            List<TournamentEnrollment> sorted = new ArrayList<>(cohort);
            sorted.sort(Comparator
                    .comparing(TournamentEnrollment::getTpTotal).reversed()
                    .thenComparing(TournamentEnrollment::getFirstTpAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(TournamentEnrollment::getId));

            int n = sorted.size();
            int rank = 1;
            for (TournamentEnrollment e : sorted) {
                BigDecimal percentile = n <= 1
                        ? BigDecimal.valueOf(100)
                        : BigDecimal.valueOf(100.0 * (n - rank + 1) / n).setScale(4, RoundingMode.HALF_UP);
                int pp = placementPointsFromRankFraction(rank, n);
                WeeklyStanding standing = new WeeklyStanding(
                        e,
                        e.getUser().getId(),
                        managed.getId(),
                        e.getBracket().name(),
                        e.getTimezoneBand(),
                        e.getTpTotal(),
                        rank,
                        n,
                        percentile,
                        pp
                );
                weeklyStandingRepository.save(standing);

                SeasonStanding ss = seasonStandingRepository
                        .findByUserIdAndSeasonId(e.getUser().getId(), seasonId)
                        .orElseGet(() -> new SeasonStanding(e.getUser().getId(), seasonId, 0));
                ss.setSpTotal(ss.getSpTotal() + pp);
                ss.setUpdatedAt(Instant.now());
                seasonStandingRepository.save(ss);

                if (e.getTpTotal() > 0) {
                    createWeeklyReward(e, managed, season, rank);
                    e.setStatus(CompeteEnrollmentStatus.REWARD_PENDING);
                } else {
                    e.setStatus(CompeteEnrollmentStatus.COMPLETED);
                }
                enrollmentRepository.save(e);
                rank++;
            }
        }

        for (TournamentEnrollment e : active) {
            if (!e.isOptedInLeaderboard()) {
                e.setStatus(CompeteEnrollmentStatus.COMPLETED);
                enrollmentRepository.save(e);
            }
        }

        managed.setCloseStatus(CompeteWeekCloseStatus.CLOSED);
        weekRepository.save(managed);
    }

    private void createWeeklyReward(TournamentEnrollment e, CompeteWeek week, CompeteSeason season, int rank) {
        String tier;
        String payload;
        if (rank == 1) {
            tier = "WEEKLY_GOLD";
            payload = "{\"title\":\"Weekly Tournament — Gold\",\"tokens\":1}";
        } else if (rank == 2) {
            tier = "WEEKLY_SILVER";
            payload = "{\"title\":\"Weekly Tournament — Silver\"}";
        } else if (rank == 3) {
            tier = "WEEKLY_BRONZE";
            payload = "{\"title\":\"Weekly Tournament — Bronze\"}";
        } else {
            tier = "PARTICIPANT";
            payload = "{\"title\":\"Weekly Tournament — Participant\"}";
        }
        Instant expires = Instant.now().plus(14, ChronoUnit.DAYS);
        RewardGrant g = new RewardGrant(e.getUser(), week, season, tier, payload, expires);
        g.setStatus(CompeteRewardStatus.PENDING);
        rewardGrantRepository.save(g);
    }

    private int placementPointsFromRankFraction(int rank, int n) {
        if (n <= 0) {
            return 0;
        }
        double f = (double) rank / n;
        if (f <= 0.01) {
            return 100;
        }
        if (f <= 0.05) {
            return 90;
        }
        if (f <= 0.10) {
            return 80;
        }
        if (f <= 0.25) {
            return 65;
        }
        if (f <= 0.50) {
            return 50;
        }
        return 35;
    }
}
