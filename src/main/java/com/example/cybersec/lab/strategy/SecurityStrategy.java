package com.example.cybersec.lab.strategy;

import com.example.cybersec.lab.dto.LabValidationResponse;

/**
 * Giao diện lõi đại diện cho một Chiến lược bảo mật (Strategy Pattern).
 * Mọi kỹ thuật mô phỏng kiểm thử lab (Tấn công, Phòng thủ, Mật mã học...)
 * đều cài đặt interface này để bảo đảm tính đa hình.
 */
public interface SecurityStrategy {
    LabValidationResponse validate(String input);
}
