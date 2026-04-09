package com.example.cybersec.lab.strategy;

import com.example.cybersec.lab.dto.LabValidationResponse;
import org.springframework.stereotype.Component;

/**
 * Chiến lược kiểm tra bài thực hành phòng ngự (Defense).
 * Phân tích mã nguồn để tìm kiếm kỹ thuật ngăn chặn lỗ hổng
 * (Prepared Statements, Output Encoding).
 */
@Component("defenseStrategy")
public class DefenseStrategy implements SecurityStrategy {
    @Override
    public LabValidationResponse validate(String input) {
        boolean hasParameterizedQueryHint = input != null && input.toLowerCase().contains("preparedstatement");
        boolean hasOutputEncodingHint = input != null && input.toLowerCase().contains("htmlspecialchars");
        boolean success = hasParameterizedQueryHint || hasOutputEncodingHint;

        if (success) {
            return new LabValidationResponse(true, "Defense applied successfully.", "Good job! Your mitigation pattern is accepted.");
        }
        return new LabValidationResponse(false, "Defense is incomplete.", "Use parameterized queries or output encoding.");
    }
}
