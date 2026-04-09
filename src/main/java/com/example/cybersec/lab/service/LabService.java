package com.example.cybersec.lab.service;

import com.example.cybersec.lab.dto.LabValidationResponse;
import com.example.cybersec.lab.factory.LabValidatorFactory;
import com.example.cybersec.lab.strategy.LabContext;
import com.example.cybersec.lab.strategy.SecurityStrategy;
import org.springframework.stereotype.Service;

/**
 * Service xử lý nghiệp vụ xác thực cho các bài thực hành Lab.
 * Dựa vào Factory Pattern để điều hướng đến đúng logic xác thực.
 */
@Service
public class LabService {
    private final LabValidatorFactory labValidatorFactory;
    private final LabContext labContext;

    public LabService(LabValidatorFactory labValidatorFactory, LabContext labContext) {
        this.labValidatorFactory = labValidatorFactory;
        this.labContext = labContext;
    }

    public LabValidationResponse validateLab(String labType, String input) {
        SecurityStrategy strategy = labValidatorFactory.createValidator(labType);
        return labContext.execute(strategy, input);
    }
}
