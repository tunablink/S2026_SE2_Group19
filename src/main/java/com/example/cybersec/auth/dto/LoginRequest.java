package com.example.cybersec.auth.dto;

/**
 * DTO lưu trữ thông tin đăng nhập.
 * Nắm bắt username và password mà client truyền lên khi tạo request xác thực.
 */
public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
