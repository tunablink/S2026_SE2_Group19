package com.example.cybersec.search.dto;

public record SearchResultDto(
        String type,
        String title,
        String description,
        String url,
        boolean requiresLogin) {
}
