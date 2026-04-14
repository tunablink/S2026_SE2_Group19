package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteRewardStatus;
import com.example.cybersec.compete.domain.RewardGrant;
import com.example.cybersec.compete.dto.RewardGrantResponseDto;
import com.example.cybersec.compete.repository.RewardGrantRepository;
import com.example.cybersec.user.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * Claim flow for weekly/season grants (MVP: status flip only; inventory integration later).
 */
@Service
public class RewardService {

    private final RewardGrantRepository rewardGrantRepository;
    private final ObjectMapper objectMapper;

    public RewardService(RewardGrantRepository rewardGrantRepository, ObjectMapper objectMapper) {
        this.rewardGrantRepository = rewardGrantRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<RewardGrantResponseDto> listForUser(User user) {
        return rewardGrantRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RewardGrantResponseDto> listPending(User user) {
        return rewardGrantRepository.findByUserAndStatusOrderByCreatedAtDesc(user, CompeteRewardStatus.PENDING).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public RewardGrantResponseDto claim(User user, Long grantId) {
        RewardGrant g = rewardGrantRepository.findById(grantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reward not found"));
        if (!g.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your reward");
        }
        if (g.getStatus() != CompeteRewardStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reward already handled");
        }
        if (g.getExpiresAt() != null && g.getExpiresAt().isBefore(Instant.now())) {
            g.setStatus(CompeteRewardStatus.EXPIRED);
            rewardGrantRepository.save(g);
            throw new ResponseStatusException(HttpStatus.GONE, "Reward expired");
        }
        g.setStatus(CompeteRewardStatus.CLAIMED);
        g.setClaimedAt(Instant.now());
        rewardGrantRepository.save(g);
        return toDto(g);
    }

    private RewardGrantResponseDto toDto(RewardGrant g) {
        return new RewardGrantResponseDto(
                g.getId(),
                g.getTier(),
                titleFromPayload(g),
                g.getStatus(),
                g.getPayload(),
                g.getExpiresAt(),
                g.getWeek() != null ? g.getWeek().getId() : null,
                g.getSeason() != null ? g.getSeason().getId() : null
        );
    }

    private String titleFromPayload(RewardGrant g) {
        try {
            JsonNode root = objectMapper.readTree(g.getPayload());
            JsonNode title = root.get("title");
            if (title != null && title.isTextual()) {
                return title.asText();
            }
        } catch (Exception ignored) {
        }
        return g.getTier();
    }
}
