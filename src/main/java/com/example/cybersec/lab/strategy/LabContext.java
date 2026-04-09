package com.example.cybersec.lab.strategy;

import com.example.cybersec.lab.dto.LabValidationResponse;
import org.springframework.stereotype.Component;

/**
 * Context component quản lý việc áp dụng SecurityStrategy (Strategy Pattern).
 * Gói gọn quá trình gọi validate sau một lớp giao tiếp thống nhất.
 */
@Component
public class LabContext {
    public LabValidationResponse execute(SecurityStrategy strategy, String input) {
        return strategy.validate(input);
    }
}
