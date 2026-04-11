package com.example.cybersec.lab.strategy;

import com.example.cybersec.lab.dto.LabValidationResponse;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class A01IdorIdentifyStrategy implements SecurityStrategy {
    @Override
    public LabValidationResponse validate(String input) {
        String normalized = normalize(input);
        boolean changedObjectId = normalized.contains("acc-102")
                || normalized.contains("/102")
                || normalized.contains("accountid=102")
                || normalized.contains("account 102")
                || normalized.contains("user 102")
                || normalized.contains("binh");
        boolean namedIssue = normalized.contains("idor")
                || normalized.contains("bola")
                || normalized.contains("broken access control")
                || normalized.contains("ownership")
                || normalized.contains("unauthorized");
        boolean capturedImpact = normalized.contains("200")
                || normalized.contains("ok")
                || normalized.contains("statement")
                || normalized.contains("balance")
                || normalized.contains("exposed")
                || normalized.contains("data");

        if (changedObjectId && namedIssue && capturedImpact) {
            return new LabValidationResponse(
                    true,
                    "Lab 1 passed. You identified the IDOR/BOLA condition and captured useful evidence.",
                    "Next, apply an ownership check and prove the same request is blocked.");
        }

        return new LabValidationResponse(
                false,
                "Lab 1 evidence is incomplete.",
                "Include the changed object ID, the IDOR/BOLA finding, and the 200 OK or exposed data result.");
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
