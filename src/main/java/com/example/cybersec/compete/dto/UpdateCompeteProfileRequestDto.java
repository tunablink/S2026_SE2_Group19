package com.example.cybersec.compete.dto;

import com.example.cybersec.compete.domain.CompeteBracket;

public record UpdateCompeteProfileRequestDto(
        String timezone,
        Boolean autoEnroll,
        CompeteBracket bracket
) {
}
