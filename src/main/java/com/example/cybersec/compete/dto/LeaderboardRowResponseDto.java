package com.example.cybersec.compete.dto;

import com.example.cybersec.compete.domain.CompeteBracket;

public record LeaderboardRowResponseDto(
        int rank,
        long userId,
        String displayName,
        int tpTotal,
        CompeteBracket bracket
) {
}
