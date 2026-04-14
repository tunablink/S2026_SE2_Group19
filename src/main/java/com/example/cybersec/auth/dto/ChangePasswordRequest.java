package com.example.cybersec.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO cho yêu cầu đổi mật khẩu từ trang login.
 * Người dùng nhập username, mật khẩu hiện tại và mật khẩu mới.
 */
public class ChangePasswordRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[A-Z]).{6,60}$",
            message = "Mật khẩu mới tối thiểu 6 ký tự, có ít nhất 1 số và 1 chữ hoa"
    )
    private String newPassword;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    private String confirmNewPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
}