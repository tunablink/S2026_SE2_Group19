package com.example.cybersec.compete.dto;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.CompeteEnrollmentStatus;

public record CompeteEnrollmentResponseDto(
        Long id,
        CompeteBracket bracket,
        String timezoneBand,
        int tpTotal,
        CompeteEnrollmentStatus status,
        boolean optedInLeaderboard
) {
}
