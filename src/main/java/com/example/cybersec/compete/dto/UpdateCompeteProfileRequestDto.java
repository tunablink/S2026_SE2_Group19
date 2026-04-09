package com.example.cybersec.compete.dto;

public record UpdateCompeteProfileRequestDto(
        String timezone,
        Boolean autoEnroll
) {
}
