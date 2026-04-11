package com.example.cybersec.lab.controller;

import com.example.cybersec.lab.dto.LabValidationResponse;
import com.example.cybersec.lab.factory.LabValidatorFactory;
import com.example.cybersec.lab.strategy.A01IdorFixStrategy;
import com.example.cybersec.lab.strategy.A01IdorIdentifyStrategy;
import com.example.cybersec.lab.strategy.DefenseStrategy;
import com.example.cybersec.lab.strategy.ExploitStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabCatalogValidationTest {
    private final LabValidatorFactory factory = new LabValidatorFactory(
            new ExploitStrategy(),
            new DefenseStrategy(),
            new A01IdorIdentifyStrategy(),
            new A01IdorFixStrategy());

    @Test
    void allCatalogSampleEvidencePassesValidation() {
        assertEquals(10, LabCatalog.modules().size());
        assertEquals(22, LabCatalog.modules().stream().mapToInt(module -> module.labs().size()).sum());

        for (LabCatalog.LabModule module : LabCatalog.modules()) {
            for (LabCatalog.Lab lab : module.labs()) {
                LabValidationResponse response = factory.createValidator(lab.labType()).validate(lab.sampleEvidence());
                assertTrue(response.isSuccess(), lab.labType() + " should accept its sample evidence");
            }
        }
    }
}
