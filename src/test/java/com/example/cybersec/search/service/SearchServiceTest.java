package com.example.cybersec.search.service;

import com.example.cybersec.search.dto.SearchResultDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchServiceTest {
    private final SearchService searchService = new SearchService();

    @Test
    void findsAuthenticatedLessonAndLabResults() {
        List<SearchResultDto> results = searchService.search("idor", true);

        assertTrue(results.stream().anyMatch(result -> result.url().equals("/learn/A01-broken-access-control")));
        assertTrue(results.stream().anyMatch(result -> result.url().equals("/lab/a01/idor")));
        assertTrue(results.stream().noneMatch(SearchResultDto::requiresLogin));
    }

    @Test
    void guestLessonResultsUseGuestUrls() {
        List<SearchResultDto> results = searchService.search("injection", false);

        assertTrue(results.stream().anyMatch(result -> result.url().equals("/learn-guest/A05-injection")));
        assertTrue(results.stream().anyMatch(SearchResultDto::requiresLogin));
    }
}
