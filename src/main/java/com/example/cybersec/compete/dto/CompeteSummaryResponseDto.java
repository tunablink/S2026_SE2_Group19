package com.example.cybersec.compete.dto;

/**
 * Hub payload: current week + enrollment + simple cap readout for UI.
 */
public record CompeteSummaryResponseDto(
        CompeteWeekResponseDto week,
        CompeteEnrollmentResponseDto enrollment,
        CompeteCapsDto caps,
        CompeteSeasonSummaryDto season
) {
}
