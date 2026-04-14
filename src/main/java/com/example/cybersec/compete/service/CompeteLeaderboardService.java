package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.CompeteEnrollmentStatus;
import com.example.cybersec.compete.domain.CompeteWeek;
import com.example.cybersec.compete.domain.TournamentEnrollment;
import com.example.cybersec.compete.domain.UserCompeteProfile;
import com.example.cybersec.compete.dto.LeaderboardResponseDto;
import com.example.cybersec.compete.dto.LeaderboardRowResponseDto;
import com.example.cybersec.compete.repository.CompeteWeekRepository;
import com.example.cybersec.compete.repository.TournamentEnrollmentRepository;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Read model for cohort leaderboard (opted-in users only).
 */
@Service
public class CompeteLeaderboardService {

    private final UserRepository userRepository;
    private final CompeteWeekRepository weekRepository;
    private final CompeteWeekService competeWeekService;
    private final TournamentEnrollmentRepository enrollmentRepository;
    private final UserCompeteProfileService profileService;

    public CompeteLeaderboardService(
            UserRepository userRepository,
            CompeteWeekRepository weekRepository,
            CompeteWeekService competeWeekService,
            TournamentEnrollmentRepository enrollmentRepository,
            UserCompeteProfileService profileService) {
        this.userRepository = userRepository;
        this.weekRepository = weekRepository;
        this.competeWeekService = competeWeekService;
        this.enrollmentRepository = enrollmentRepository;
        this.profileService = profileService;
    }

    @Transactional
    public LeaderboardResponseDto getLeaderboard(String username, Long weekId, CompeteBracket bracket, String band) {
        User me = userRepository.findByUsername(username).orElseThrow();
        CompeteWeek week = weekId != null
                ? weekRepository.findById(weekId).orElseThrow()
                : competeWeekService.getOrCreateCurrentWeek(Instant.now());
        UserCompeteProfile profile = profileService.getOrCreate(me);
        TournamentEnrollment mine = enrollmentRepository.findByUserAndWeek(me, week).orElse(null);
        CompeteBracket b = bracket != null
                ? bracket
                : mine != null ? mine.getBracket() : profile.getBracket();
        String tzBand = band != null
                ? band
                : mine != null ? mine.getTimezoneBand() : profile.getTimezoneBand();

        List<TournamentEnrollment> cohort = enrollmentRepository.findLeaderboardCohort(
                week, CompeteEnrollmentStatus.ACTIVE, b, tzBand);

        List<LeaderboardRowResponseDto> rows = new ArrayList<>();
        int rank = 1;
        for (TournamentEnrollment e : cohort) {
            String name = e.getUser().getUsername();
            rows.add(new LeaderboardRowResponseDto(rank++, e.getUser().getId(), name, e.getTpTotal(), e.getBracket()));
        }
        return new LeaderboardResponseDto(week.getId(), b, tzBand, rows);
    }
}
