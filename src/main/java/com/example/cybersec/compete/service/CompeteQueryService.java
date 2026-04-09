package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.TournamentEnrollment;
import com.example.cybersec.compete.domain.UserCompeteProfile;
import com.example.cybersec.compete.dto.CompeteCapsDto;
import com.example.cybersec.compete.dto.CompeteEnrollmentResponseDto;
import com.example.cybersec.compete.dto.CompeteSeasonSummaryDto;
import com.example.cybersec.compete.dto.CompeteSummaryResponseDto;
import com.example.cybersec.compete.dto.CompeteWeekResponseDto;
import com.example.cybersec.compete.repository.SeasonStandingRepository;
import com.example.cybersec.compete.repository.TournamentEnrollmentRepository;
import com.example.cybersec.compete.repository.TournamentPointEventRepository;
import com.example.cybersec.compete.support.CompeteUtcTime;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Aggregates read models for the Compete hub screen.
 */
@Service
public class CompeteQueryService {

    private final UserRepository userRepository;
    private final CompeteWeekService competeWeekService;
    private final UserCompeteProfileService profileService;
    private final TournamentEnrollmentRepository enrollmentRepository;
    private final TournamentPointEventRepository pointEventRepository;
    private final SeasonStandingRepository seasonStandingRepository;

    public CompeteQueryService(
            UserRepository userRepository,
            CompeteWeekService competeWeekService,
            UserCompeteProfileService profileService,
            TournamentEnrollmentRepository enrollmentRepository,
            TournamentPointEventRepository pointEventRepository,
            SeasonStandingRepository seasonStandingRepository) {
        this.userRepository = userRepository;
        this.competeWeekService = competeWeekService;
        this.profileService = profileService;
        this.enrollmentRepository = enrollmentRepository;
        this.pointEventRepository = pointEventRepository;
        this.seasonStandingRepository = seasonStandingRepository;
    }

    @Transactional(readOnly = true)
    public CompeteSummaryResponseDto getSummary(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Instant now = Instant.now();
        CompeteWeek week = competeWeekService.getOrCreateCurrentWeek(now);
        UserCompeteProfile profile = profileService.getOrCreate(user);

        CompeteWeekResponseDto weekDto = new CompeteWeekResponseDto(
                week.getId(),
                week.getWeekStartUtc(),
                week.getWeekEndUtc(),
                week.getSeason().getId(),
                week.getSeason().getCode(),
                week.getWeekIndex(),
                week.isFrozen(),
                week.getCloseStatus()
        );

        TournamentEnrollment enrollment = enrollmentRepository.findByUserAndWeek(user, week).orElse(null);
        CompeteEnrollmentResponseDto enDto = enrollment == null ? null : new CompeteEnrollmentResponseDto(
                enrollment.getId(),
                enrollment.getBracket(),
                enrollment.getTimezoneBand(),
                enrollment.getTpTotal(),
                enrollment.getStatus(),
                enrollment.isOptedInLeaderboard()
        );

        Instant dayStart = CompeteUtcTime.startOfUtcDay(now);
        int dailyUsed = pointEventRepository.sumDeltaTpSince(user, dayStart);
        int weeklyTotal = enrollment != null ? enrollment.getTpTotal() : 0;
        CompeteCapsDto caps = new CompeteCapsDto(
                dailyUsed,
                TournamentPointService.DAILY_TP_CAP,
                weeklyTotal,
                TournamentPointService.WEEKLY_HARD_CAP
        );

        Long seasonId = week.getSeason().getId();
        int sp = seasonStandingRepository.findByUserIdAndSeasonId(user.getId(), seasonId)
                .map(s -> s.getSpTotal())
                .orElse(0);

        CompeteSeasonSummaryDto seasonDto = new CompeteSeasonSummaryDto(
                week.getSeason().getCode(),
                sp,
                week.getWeekIndex()
        );

        return new CompeteSummaryResponseDto(weekDto, enDto, caps, seasonDto);
    }
}
