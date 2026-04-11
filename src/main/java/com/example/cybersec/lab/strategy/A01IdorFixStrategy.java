package com.example.cybersec.lab.strategy;

import com.example.cybersec.lab.dto.LabValidationResponse;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class A01IdorFixStrategy implements SecurityStrategy {
    @Override
    public LabValidationResponse validate(String input) {
        String normalized = normalize(input);
        boolean centralizedCheck = normalized.contains("middleware")
                || normalized.contains("interceptor")
                || normalized.contains("filter")
                || normalized.contains("policy")
                || normalized.contains("preauthorize")
                || normalized.contains("guard");
        boolean ownershipCheck = normalized.contains("owner")
                || normalized.contains("ownership")
                || normalized.contains("belongs")
                || normalized.contains("currentuser")
                || normalized.contains("req.user")
                || normalized.contains("principal");
        boolean deniesCrossAccount = normalized.contains("403")
                || normalized.contains("forbidden")
                || normalized.contains("accessdenied")
                || normalized.contains("deny")
                || normalized.contains("blocked");
        boolean retested = normalized.contains("retest")
                || normalized.contains("test")
                || normalized.contains("assert")
                || normalized.contains("expect")
                || normalized.contains("same request");

        if (centralizedCheck && ownershipCheck && deniesCrossAccount && retested) {
            return new LabValidationResponse(
                    true,
                    "Lab 2 passed. Your fix enforces ownership before returning the object.",
                    "Keep this check server-side and cover both allowed and denied paths in tests.");
        }

        return new LabValidationResponse(
                false,
                "Lab 2 fix is incomplete.",
                "Include middleware or a policy layer, an ownership comparison, a 403/deny result, and a retest.");
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
