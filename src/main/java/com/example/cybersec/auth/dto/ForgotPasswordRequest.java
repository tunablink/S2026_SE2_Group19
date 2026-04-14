package com.example.cybersec.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO cho yêu cầu quên mật khẩu.
 * Người dùng gửi email đã đăng ký để nhận mật khẩu mới.
 */
public class ForgotPasswordRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}