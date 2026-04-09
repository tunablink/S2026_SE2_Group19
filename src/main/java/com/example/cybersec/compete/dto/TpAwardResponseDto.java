package com.example.cybersec.compete.dto;

/**
 * Returned when clients need confirmation after a solve (optional; mostly for debugging).
 */
public record TpAwardResponseDto(
        long enrollmentId,
        int tpTotal,
        int deltaTp,
        boolean awarded
) {
}
