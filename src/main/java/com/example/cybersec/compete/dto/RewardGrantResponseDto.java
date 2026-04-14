package com.example.cybersec.compete.dto;

import com.example.cybersec.compete.domain.CompeteRewardStatus;

import java.time.Instant;

public record RewardGrantResponseDto(
        Long id,
        String tier,
        String title,
        CompeteRewardStatus status,
        String payload,
        Instant expiresAt,
        Long weekId,
        Long seasonId
) {
}
