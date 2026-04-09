package com.example.cybersec.auth.dto;

/**
 * DTO cho kết quả trả về khi đăng nhập thành công.
 * Chứa JWT token được tạo ra để client lưu lại và sử dụng cho các request sau.
 */
public class LoginResponse {
    private String token;
    private final String tokenType = "Bearer";

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
}
