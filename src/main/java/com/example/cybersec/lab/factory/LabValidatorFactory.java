package com.example.cybersec.lab.factory;

import com.example.cybersec.lab.strategy.A01IdorFixStrategy;
import com.example.cybersec.lab.strategy.A01IdorIdentifyStrategy;
import com.example.cybersec.lab.strategy.DefenseStrategy;
import com.example.cybersec.lab.strategy.ExploitStrategy;
import com.example.cybersec.lab.strategy.SecurityStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Factory sinh ra Validator phù hợp dựa trên loại Lab (Factory Pattern).
 * Trả về đúng thuật toán xác thực dựa vào tham số đầu vào.
 */
@Component
public class LabValidatorFactory {
    private final ExploitStrategy exploitStrategy;
    private final DefenseStrategy defenseStrategy;
    private final A01IdorIdentifyStrategy a01IdorIdentifyStrategy;
    private final A01IdorFixStrategy a01IdorFixStrategy;
    private final Map<String, SecurityStrategy> keywordStrategies;

    public LabValidatorFactory(ExploitStrategy exploitStrategy,
                               DefenseStrategy defenseStrategy,
                               A01IdorIdentifyStrategy a01IdorIdentifyStrategy,
                               A01IdorFixStrategy a01IdorFixStrategy) {
        this.exploitStrategy = exploitStrategy;
        this.defenseStrategy = defenseStrategy;
        this.a01IdorIdentifyStrategy = a01IdorIdentifyStrategy;
        this.a01IdorFixStrategy = a01IdorFixStrategy;
        this.keywordStrategies = createKeywordStrategies();
    }

    public SecurityStrategy createValidator(String labType) {
        if (labType == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Lab type is required");
        }
        String normalized = labType.trim().toUpperCase(Locale.ROOT);
        SecurityStrategy strategy = switch (normalized) {
            case "A01_IDOR_IDENTIFY" -> a01IdorIdentifyStrategy;
            case "A01_IDOR_FIX" -> a01IdorFixStrategy;
            case "EXPLOIT" -> exploitStrategy;
            case "DEFENSE" -> defenseStrategy;
            default -> keywordStrategies.get(normalized);
        };
        if (strategy == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Unsupported lab type");
        }
        return strategy;
    }

    private Map<String, SecurityStrategy> createKeywordStrategies() {
        Map<String, SecurityStrategy> validators = new LinkedHashMap<>();
        validators.put("A02_DEBUG_LEAK", keywordValidator("A02 Lab 1", List.of("debug", "endpoint", "verbose", "error", "stack", "leak")));
        validators.put("A02_BASELINE_RETEST", keywordValidator("A02 Lab 2", List.of("baseline", "debug", "generic", "header", "retest", "blocked")));
        validators.put("A03_RISKY_DEPENDENCY", keywordValidator("A03 Lab 1", List.of("dependency", "postinstall", "script", "risky", "blocked", "review")));
        validators.put("A03_PINNING_GATES", keywordValidator("A03 Lab 2", List.of("pin", "lockfile", "dependency review", "gate", "ci", "block")));
        validators.put("A04_SENSITIVE_DATA", keywordValidator("A04 Lab 1", List.of("plaintext", "sensitive", "token", "personal", "disclosure", "cryptographic")));
        validators.put("A04_ENCRYPTION_RETEST", keywordValidator("A04 Lab 2", List.of("encrypt", "key", "kms", "hash", "retest", "plaintext")));
        validators.put("A05_CONFIRM_INJECTION", keywordValidator("A05 Lab 1", List.of("injection", "sqli", "' or '1'='1", "query", "unauthorized", "rows")));
        validators.put("A05_PARAMETERIZATION", keywordValidator("A05 Lab 2", List.of("preparedstatement", "parameter", "bind", "retest", "payload", "unauthorized")));
        validators.put("A06_ABUSE_CASES", keywordValidator("A06 Lab 1", List.of("abuse", "case", "workflow", "missing", "control", "redemption")));
        validators.put("A06_DESIGN_CONTROLS", keywordValidator("A06 Lab 2", List.of("rate limit", "redemption", "workflow", "state", "abuse", "test")));
        validators.put("A07_RATE_LIMIT_LOCKOUT", keywordValidator("A07 Lab 1", List.of("rate limit", "lockout", "login", "failure", "account", "log")));
        validators.put("A07_SESSION_HARDENING", keywordValidator("A07 Lab 2", List.of("httponly", "secure", "samesite", "rotate", "session", "retest")));
        validators.put("A08_UPDATE_INTEGRITY", keywordValidator("A08 Lab 1", List.of("integrity", "signature", "checksum", "install", "tampered", "reject")));
        validators.put("A08_DESERIALIZATION", keywordValidator("A08 Lab 2", List.of("deserialization", "allowlist", "dto", "schema", "reject", "payload")));
        validators.put("A09_STRUCTURED_LOGS", keywordValidator("A09 Lab 1", List.of("structured", "log", "user", "ip", "action", "result")));
        validators.put("A09_ALERT_RULES", keywordValidator("A09 Lab 2", List.of("alert", "rule", "brute force", "privilege", "suppression", "denied")));
        validators.put("A09_VALIDATE_ALERTS", keywordValidator("A09 Lab 3", List.of("validation", "brute-force", "access-denied", "alert", "suppression", "normal")));
        validators.put("A10_SAFE_ERRORS", keywordValidator("A10 Lab 1", List.of("safe", "error", "generic", "log", "internal", "hostname")));
        validators.put("A10_FAIL_CLOSED", keywordValidator("A10 Lab 2", List.of("fail-closed", "deny", "timeout", "error", "403", "blocked")));
        validators.put("A10_TIMEOUTS_CIRCUIT", keywordValidator("A10 Lab 3", List.of("timeout", "circuit breaker", "dependency", "fallback", "retest", "slow")));
        return validators;
    }

    private SecurityStrategy keywordValidator(String labName, List<String> keywords) {
        return input -> {
            String normalized = normalize(input);
            long matches = keywords.stream()
                    .filter(normalized::contains)
                    .count();
            boolean success = normalized.length() >= 60 && matches >= 3;
            if (success) {
                return new com.example.cybersec.lab.dto.LabValidationResponse(
                        true,
                        labName + " passed. Your evidence includes concrete security details.",
                        "Good job. Keep the exploit evidence, fix, and retest result together.");
            }
            return new com.example.cybersec.lab.dto.LabValidationResponse(
                    false,
                    labName + " evidence is incomplete.",
                    "Add specific observations, controls, and retest results from the lab prompt.");
        };
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
