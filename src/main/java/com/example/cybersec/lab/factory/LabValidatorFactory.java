package com.example.cybersec.lab.factory;

import com.example.cybersec.lab.strategy.DefenseStrategy;
import com.example.cybersec.lab.strategy.ExploitStrategy;
import com.example.cybersec.lab.strategy.SecurityStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Factory sinh ra Validator phù hợp dựa trên loại Lab (Factory Pattern).
 * Trả về đúng thuật toán xác thực dựa vào tham số đầu vào.
 */
@Component
public class LabValidatorFactory {
    private final ExploitStrategy exploitStrategy;
    private final DefenseStrategy defenseStrategy;

    public LabValidatorFactory(ExploitStrategy exploitStrategy, DefenseStrategy defenseStrategy) {
        this.exploitStrategy = exploitStrategy;
        this.defenseStrategy = defenseStrategy;
    }

    public SecurityStrategy createValidator(String labType) {
        if (labType == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Lab type is required");
        }
        return switch (labType.trim().toUpperCase()) {
            case "EXPLOIT" -> exploitStrategy;
            case "DEFENSE" -> defenseStrategy;
            default -> throw new ResponseStatusException(BAD_REQUEST, "Unsupported lab type");
        };
    }
}
