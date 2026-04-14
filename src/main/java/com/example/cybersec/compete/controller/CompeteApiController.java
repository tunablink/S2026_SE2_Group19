package com.example.cybersec.compete.controller;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.TournamentEnrollment;
import com.example.cybersec.compete.dto.*;
import com.example.cybersec.compete.service.*;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST surface for weekly tournament / season summaries, enrollment, leaderboard, and rewards.
 */
@RestController
@RequestMapping("/api/compete")
public class CompeteApiController {

    private final CompeteQueryService competeQueryService;
    private final CompeteLeaderboardService leaderboardService;
    private final RewardService rewardService;
    private final UserRepository userRepository;
    private final CompeteWeekService competeWeekService;
    private final UserCompeteProfileService profileService;
    private final TournamentEnrollmentService enrollmentService;

    public CompeteApiController(CompeteQueryService competeQueryService,
                                CompeteLeaderboardService leaderboardService,
                                RewardService rewardService,
                                UserRepository userRepository,
                                CompeteWeekService competeWeekService,
                                UserCompeteProfileService profileService,
                                TournamentEnrollmentService enrollmentService) {
        this.competeQueryService = competeQueryService;
        this.leaderboardService = leaderboardService;
        this.rewardService = rewardService;
        this.userRepository = userRepository;
        this.competeWeekService = competeWeekService;
        this.profileService = profileService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/me/summary")
    public ResponseEntity<CompeteSummaryResponseDto> summary(Authentication auth) {
        requireAuth(auth);
        return ResponseEntity.ok(competeQueryService.getSummary(auth.getName()));
    }

    @PostMapping("/me/enroll")
    public ResponseEntity<CompeteEnrollmentResponseDto> enroll(
            @Valid @RequestBody EnrollRequestDto body, Authentication auth) {
        requireAuth(auth);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        var week = competeWeekService.getOrCreateCurrentWeek(Instant.now());
        var profile = profileService.getOrCreate(user);
        TournamentEnrollment e = enrollmentService.enroll(user, week, profile, body.optedInLeaderboard());
        return ResponseEntity.ok(toEnrollmentDto(e));
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateCompeteProfileRequestDto body, Authentication auth) {
        requireAuth(auth);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (body.timezone() != null) profileService.updateTimezone(user, body.timezone());
        if (body.autoEnroll() != null) profileService.updateFlags(user, body.autoEnroll());
        if (body.bracket() != null) profileService.updateBracket(user, body.bracket());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponseDto> leaderboard(
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) CompeteBracket bracket,
            @RequestParam(required = false) String band,
            Authentication auth) {
        requireAuth(auth);
        return ResponseEntity.ok(leaderboardService.getLeaderboard(auth.getName(), weekId, bracket, band));
    }

    @GetMapping("/rewards")
    public ResponseEntity<java.util.List<RewardGrantResponseDto>> rewards(Authentication auth) {
        requireAuth(auth);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(rewardService.listForUser(user));
    }

    @PostMapping("/rewards/{id}/claim")
    public ResponseEntity<RewardGrantResponseDto> claim(@PathVariable("id") Long grantId, Authentication auth) {
        requireAuth(auth);
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(rewardService.claim(user, grantId));
    }

    private static void requireAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
    }

    private static CompeteEnrollmentResponseDto toEnrollmentDto(TournamentEnrollment e) {
        return new CompeteEnrollmentResponseDto(
                e.getId(), e.getBracket(), e.getTimezoneBand(),
                e.getTpTotal(), e.getStatus(), e.isOptedInLeaderboard()
        );
    }
}
