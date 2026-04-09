package com.example.cybersec.lab.dto;

/**
 * DTO trả về kết quả xác thực phòng Lab.
 * Trả lời cho client biết submission thành công hay thất bại,
 * kèm theo tin nhắn giải thích và hint nếu có.
 */
public class LabValidationResponse {
    private boolean success;
    private String message;
    private String hint;

    public LabValidationResponse(boolean success, String message, String hint) {
        this.success = success;
        this.message = message;
        this.hint = hint;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getHint() { return hint; }
}
