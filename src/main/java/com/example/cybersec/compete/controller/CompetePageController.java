package com.example.cybersec.compete.controller;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.CompeteWeekCloseStatus;
import com.example.cybersec.compete.dto.*;
import com.example.cybersec.compete.service.CompeteLeaderboardService;
import com.example.cybersec.compete.service.CompeteQueryService;
import com.example.cybersec.compete.service.CompeteWeekService;
import com.example.cybersec.compete.service.RewardService;
import com.example.cybersec.compete.service.TournamentEnrollmentService;
import com.example.cybersec.compete.service.UserCompeteProfileService;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Server-rendered Compete hub (weekly tournament / season).
 */
@Controller
public class CompetePageController {

    private static final Logger log = LoggerFactory.getLogger(CompetePageController.class);

    private final CompeteQueryService competeQueryService;
    private final CompeteLeaderboardService leaderboardService;
    private final RewardService rewardService;
    private final CompeteWeekService competeWeekService;
    private final UserCompeteProfileService profileService;
    private final TournamentEnrollmentService enrollmentService;
    private final UserRepository userRepository;

    public CompetePageController(CompeteQueryService competeQueryService,
                                 CompeteLeaderboardService leaderboardService,
                                 RewardService rewardService,
                                 CompeteWeekService competeWeekService,
                                 UserCompeteProfileService profileService,
                                 TournamentEnrollmentService enrollmentService,
                                 UserRepository userRepository) {
        this.competeQueryService = competeQueryService;
        this.leaderboardService = leaderboardService;
        this.rewardService = rewardService;
        this.competeWeekService = competeWeekService;
        this.profileService = profileService;
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/compete")
    public String competePage(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        String username = principal.getName();
        model.addAttribute("username", username);
        addCompeteOptions(model);
        try {
            model.addAttribute("competeSummary", competeQueryService.getSummary(username));
            User user = userRepository.findByUsername(username).orElseThrow();
            model.addAttribute("competeProfile", profileService.getOrCreate(user));
        } catch (Exception ex) {
            log.error("Failed to load compete summary for user {}", username, ex);
            model.addAttribute("competeSummary", fallbackSummary());
            model.addAttribute("competeLoadError", "Could not load tournament data. Temporary data is being displayed.");
        }
        return "compete/compete";
    }

    @GetMapping("/compete/leaderboard")
    public String leaderboardPage(@RequestParam(required = false) CompeteBracket bracket,
                                  @RequestParam(required = false) String band,
                                  Principal principal,
                                  Model model) {
        if (principal == null) return "redirect:/login";
        addCompeteOptions(model);
        try {
            LeaderboardResponseDto board = leaderboardService.getLeaderboard(principal.getName(), null, bracket, normalizeBand(band));
            model.addAttribute("board", board);
        } catch (Exception ex) {
            log.error("Failed to load compete leaderboard for user {}", principal.getName(), ex);
            model.addAttribute("board", fallbackBoard());
            model.addAttribute("competeLoadError", "Could not load the leaderboard. An empty page is being displayed.");
        }
        return "compete/leaderboard";
    }

    @GetMapping("/compete/rewards")
    public String rewardsPage(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            List<RewardGrantResponseDto> rewards = rewardService.listForUser(user);
            model.addAttribute("rewards", rewards);
        } catch (Exception ex) {
            log.error("Failed to load compete rewards for user {}", principal.getName(), ex);
            model.addAttribute("rewards", List.of());
            model.addAttribute("competeLoadError", "Could not load rewards. An empty page is being displayed.");
        }
        return "compete/rewards";
    }

    @PostMapping("/compete/enroll")
    public String enroll(@RequestParam(defaultValue = "true") boolean optedInLeaderboard,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            var week = competeWeekService.getOrCreateCurrentWeek(Instant.now());
            var profile = profileService.getOrCreate(user);
            enrollmentService.enroll(user, week, profile, optedInLeaderboard);
            redirectAttributes.addFlashAttribute("successMessage", "You joined this tournament week.");
        } catch (Exception ex) {
            log.error("Failed to enroll compete user {}", principal.getName(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Could not join this week. Please try again.");
        }
        return "redirect:/compete";
    }

    @PostMapping("/compete/leaderboard-visibility")
    public String updateLeaderboardVisibility(@RequestParam boolean optedInLeaderboard,
                                              Principal principal,
                                              RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            var week = competeWeekService.getOrCreateCurrentWeek(Instant.now());
            var profile = profileService.getOrCreate(user);
            enrollmentService.enroll(user, week, profile, optedInLeaderboard);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    optedInLeaderboard ? "Leaderboard visibility is now on." : "Leaderboard visibility is now off.");
        } catch (Exception ex) {
            log.error("Failed to update leaderboard visibility for user {}", principal.getName(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update leaderboard visibility.");
        }
        return "redirect:/compete";
    }

    @PostMapping("/compete/profile")
    public String updateProfile(@RequestParam String timezone,
                                @RequestParam(defaultValue = "false") boolean autoEnroll,
                                @RequestParam(required = false) CompeteBracket bracket,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            profileService.updateTimezone(user, timezone);
            profileService.updateFlags(user, autoEnroll);
            profileService.updateBracket(user, bracket);
            redirectAttributes.addFlashAttribute("successMessage", "Compete preferences saved.");
        } catch (Exception ex) {
            log.error("Failed to update compete profile for user {}", principal.getName(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Could not save compete preferences.");
        }
        return "redirect:/compete";
    }

    @PostMapping("/compete/rewards/{id}/claim")
    public String claimReward(@PathVariable("id") Long grantId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            rewardService.claim(user, grantId);
            redirectAttributes.addFlashAttribute("successMessage", "Reward claimed.");
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getReason() != null ? ex.getReason() : "Could not claim reward.");
        } catch (Exception ex) {
            log.error("Failed to claim reward {} for user {}", grantId, principal.getName(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Could not claim reward.");
        }
        return "redirect:/compete/rewards";
    }

    private static CompeteSummaryResponseDto fallbackSummary() {
        Instant now = Instant.now();
        return new CompeteSummaryResponseDto(
                new CompeteWeekResponseDto(null, now.minusSeconds(172800), now.plusSeconds(432000), null, "MVP-2026", 1, false, CompeteWeekCloseStatus.OPEN),
                null,
                new CompeteCapsDto(0, 120, 0, 600),
                new CompeteSeasonSummaryDto("MVP-2026", 0, 1)
        );
    }

    private static LeaderboardResponseDto fallbackBoard() {
        return new LeaderboardResponseDto(null, CompeteBracket.CORE, "AM2", List.of());
    }

    private static void addCompeteOptions(Model model) {
        model.addAttribute("bracketOptions", Arrays.asList(CompeteBracket.values()));
        model.addAttribute("timezoneBandOptions", List.of("AM1", "AM2", "AM3"));
        model.addAttribute("timezoneOptions", List.of(
                "UTC",
                "Asia/Ho_Chi_Minh",
                "Asia/Bangkok",
                "Asia/Singapore",
                "Europe/London",
                "America/New_York",
                "America/Los_Angeles"
        ));
    }

    private static String normalizeBand(String band) {
        if (band == null || band.isBlank()) {
            return null;
        }
        return band;
    }
}
