package com.example.cybersec.lab.strategy;

import com.example.cybersec.lab.dto.LabValidationResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class A01LabStrategyTest {
    private final A01IdorIdentifyStrategy identifyStrategy = new A01IdorIdentifyStrategy();
    private final A01IdorFixStrategy fixStrategy = new A01IdorFixStrategy();

    @Test
    void identifiesIdorEvidenceWhenChangedObjectReturnsData() {
        LabValidationResponse response = identifyStrategy.validate(
                "IDOR/BOLA finding: changing account ID from ACC-101 to ACC-102 returned 200 OK "
                        + "and exposed another user's statement data.");

        assertTrue(response.isSuccess());
    }

    @Test
    void rejectsIdorEvidenceWithoutImpact() {
        LabValidationResponse response = identifyStrategy.validate("ACC-102 looks interesting.");

        assertFalse(response.isSuccess());
    }

    @Test
    void acceptsOwnershipMiddlewareAndRetest() {
        LabValidationResponse response = fixStrategy.validate(
                "Middleware enforceOwnership compares req.account.ownerId with req.user.id, "
                        + "returns 403 Forbidden on mismatch, and retest asserts the same request is blocked.");

        assertTrue(response.isSuccess());
    }

    @Test
    void rejectsFixWithoutDeniedRetest() {
        LabValidationResponse response = fixStrategy.validate("Add an owner field to the response.");

        assertFalse(response.isSuccess());
    }
}
