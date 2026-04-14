package com.example.cybersec.user.entity;

import jakarta.persistence.*;

/**
 * JPA Entity biểu diễn một người dùng của hệ thống.
 * <p>
 * Ánh xạ với bảng "users" dưới database, lưu giữ thông tin cốt lõi
 * như username, mật khẩu mã hoá, quyền (roles) và địa chỉ.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        private String username;
    private String password;
    private String roles;
    private String address;
    private String email;

    public User() {}

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
        public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
