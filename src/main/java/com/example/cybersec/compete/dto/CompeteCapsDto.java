package com.example.cybersec.compete.dto;

public record CompeteCapsDto(
        int dailyTpUsed,
        int dailyTpCap,
        int weeklyTpTotal,
        int weeklyHardCap
) {
}
