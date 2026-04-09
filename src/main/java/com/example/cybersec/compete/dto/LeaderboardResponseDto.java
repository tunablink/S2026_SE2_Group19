package com.example.cybersec.compete.dto;

import com.example.cybersec.compete.domain.CompeteBracket;

import java.util.List;

public record LeaderboardResponseDto(
        Long weekId,
        CompeteBracket bracket,
        String timezoneBand,
        List<LeaderboardRowResponseDto> entries
) {
}
