package com.example.cybersec.compete.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollRequestDto(
        @NotNull Boolean optedInLeaderboard
) {
}
