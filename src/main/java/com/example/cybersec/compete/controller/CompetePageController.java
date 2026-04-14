package com.example.cybersec.compete.controller;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.CompeteWeekCloseStatus;
import com.example.cybersec.compete.dto.*;
import com.example.cybersec.compete.service.CompeteLeaderboardService;
import com.example.cybersec.compete.service.CompeteQueryService;
import com.example.cybersec.compete.service.RewardService;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.Instant;
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
    private final UserRepository userRepository;

    public CompetePageController(CompeteQueryService competeQueryService,
                                 CompeteLeaderboardService leaderboardService,
                                 RewardService rewardService,
                                 UserRepository userRepository) {
        this.competeQueryService = competeQueryService;
        this.leaderboardService = leaderboardService;
        this.rewardService = rewardService;
        this.userRepository = userRepository;
    }

    @GetMapping("/compete")
    public String competePage(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        String username = principal.getName();
        model.addAttribute("username", username);
        try {
            model.addAttribute("competeSummary", competeQueryService.getSummary(username));
        } catch (Exception ex) {
            log.error("Failed to load compete summary for user {}", username, ex);
            model.addAttribute("competeSummary", fallbackSummary());
            model.addAttribute("competeLoadError", "Could not load tournament data. Temporary data is being displayed.");
        }
        return "compete/compete";
    }

    @GetMapping("/compete/leaderboard")
    public String leaderboardPage(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        try {
            LeaderboardResponseDto board = leaderboardService.getLeaderboard(principal.getName(), null, null, null);
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
}
