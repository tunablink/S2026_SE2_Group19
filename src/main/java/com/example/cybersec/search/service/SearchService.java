package com.example.cybersec.search.service;

import com.example.cybersec.lab.controller.LabCatalog;
import com.example.cybersec.search.dto.SearchResultDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class SearchService {
    private static final List<SearchItem> LESSONS = List.of(
            lesson("A01", "Broken Access Control", "Authorization mistakes, IDOR/BOLA, privilege escalation, ownership checks.", "A01-broken-access-control"),
            lesson("A02", "Security Misconfiguration", "Unsafe defaults, exposed services, debug endpoints, verbose errors, weak headers.", "A02-security-misconfiguration"),
            lesson("A03", "Software Supply Chain Failures", "Dependency risk, lockfiles, review gates, package provenance, CI/CD integrity.", "A03-software-supply-chain-failures"),
            lesson("A04", "Cryptographic Failures", "Sensitive data, plaintext storage, encryption, hashing, key handling.", "A04-cryptographic-failures"),
            lesson("A05", "Injection", "SQL injection, NoSQL injection, parameterization, prepared statements, query safety.", "A05-injection"),
            lesson("A06", "Insecure Design", "Abuse cases, threat modeling, workflow controls, rate limits, secure design.", "A06-insecure-design"),
            lesson("A07", "Authentication Failures", "Login hardening, brute force, lockout, session cookies, session rotation.", "A07-authentication-failures"),
            lesson("A08", "Software or Data Integrity Failures", "Unsigned updates, checksum verification, signatures, unsafe deserialization.", "A08-software-or-data-integrity-failures"),
            lesson("A09", "Security Logging and Monitoring Failures", "Structured logs, alert rules, brute-force detection, monitoring validation.", "A09-security-logging-and-monitoring-failures"),
            lesson("A10", "Server-Side Request Forgery and Exceptional Conditions", "Safe errors, fail-closed behavior, timeouts, circuit breakers, dependency failure.", "A10-server-side-request-forgery")
    );

    public List<SearchResultDto> search(String query, boolean authenticated) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.length() < 2) {
            return List.of();
        }

        List<ScoredResult> scoredResults = new ArrayList<>();
        addLessonResults(scoredResults, normalizedQuery, authenticated);
        addQuizResults(scoredResults, normalizedQuery, authenticated);
        addLabResults(scoredResults, normalizedQuery, authenticated);

        return scoredResults.stream()
                .sorted(Comparator.comparingInt(ScoredResult::score).reversed()
                        .thenComparing(result -> result.result().title()))
                .limit(12)
                .map(ScoredResult::result)
                .toList();
    }

    private void addLessonResults(List<ScoredResult> results, String query, boolean authenticated) {
        for (SearchItem lesson : LESSONS) {
            addIfMatches(results, query, new SearchResultDto(
                    "Lesson",
                    lesson.code() + ": " + lesson.title(),
                    lesson.description(),
                    (authenticated ? "/learn/" : "/learn-guest/") + lesson.slug(),
                    false),
                    lesson.searchText());
        }
    }

    private void addQuizResults(List<ScoredResult> results, String query, boolean authenticated) {
        for (SearchItem lesson : LESSONS) {
            addIfMatches(results, query, new SearchResultDto(
                    "Quiz",
                    lesson.code() + " quiz: " + lesson.title(),
                    "Knowledge check for " + lesson.title() + ".",
                    authenticated ? "/learn/question/" + lesson.slug() : "/login",
                    !authenticated),
                    lesson.searchText() + " quiz challenge question knowledge check");
        }
    }

    private void addLabResults(List<ScoredResult> results, String query, boolean authenticated) {
        for (LabCatalog.LabModule module : LabCatalog.modules()) {
            for (LabCatalog.Lab lab : module.labs()) {
                String searchText = normalize(module.code() + " " + module.title() + " " + lab.title() + " "
                        + lab.badge() + " " + lab.scenario() + " " + lab.simulator() + " " + lab.sampleEvidence());
                String labUrl = "/lab/" + module.slug() + "/" + lab.slug();
                addIfMatches(results, query, new SearchResultDto(
                        "Lab",
                        module.code() + " lab: " + lab.title(),
                        lab.scenario(),
                        authenticated ? labUrl : "/login",
                        !authenticated),
                        searchText);
            }
        }
    }

    private void addIfMatches(List<ScoredResult> results, String query, SearchResultDto result, String searchText) {
        int score = score(query, searchText, result);
        if (score > 0) {
            results.add(new ScoredResult(score, result));
        }
    }

    private int score(String query, String searchText, SearchResultDto result) {
        int score = 0;
        String title = normalize(result.title());
        String[] terms = query.split(" ");

        if (title.contains(query)) {
            score += 40;
        }
        if (searchText.contains(query)) {
            score += 20;
        }
        for (String term : terms) {
            if (term.length() < 2) {
                continue;
            }
            if (title.contains(term)) {
                score += 8;
            } else if (searchText.contains(term)) {
                score += 3;
            }
        }
        return score;
    }

    private static SearchItem lesson(String code, String title, String description, String slug) {
        return new SearchItem(code, title, description, slug, normalize(code + " " + title + " " + description));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").replaceAll("\\s+", " ").trim();
    }

    private record SearchItem(String code, String title, String description, String slug, String searchText) {
    }

    private record ScoredResult(int score, SearchResultDto result) {
    }
}
