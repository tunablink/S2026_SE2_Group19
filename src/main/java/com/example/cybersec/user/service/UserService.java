package com.example.cybersec.user.service;

import com.example.cybersec.auth.dto.RegisterRequest;
import com.example.cybersec.user.dto.UpdateProfileRequest;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service quản lý nghiệp vụ đối với User.
 * Xử lý kiểm tra username trùng, mã hoá mật khẩu và đăng ký người dùng mới.
 */
@Service
public class UserService {

    private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[A-Z]).{6,60}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    @Transactional
    public void registerUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(normalizeEmail(request.getEmail()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setRoles("USER");
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean usernameTakenByOtherUser(String username, Long currentUserId) {
        return userRepository.findByUsername(username)
                .filter(user -> !user.getId().equals(currentUserId))
                .isPresent();
    }

    public boolean emailTakenByOtherUser(String email, Long currentUserId) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.findByEmailIgnoreCase(email)
                .filter(user -> !user.getId().equals(currentUserId))
                .isPresent();
    }

    @Transactional
    public User updateProfile(String identifier, UpdateProfileRequest request) {
        User user = findByUsernameOrEmail(identifier);
        if (user == null) {
            return null;
        }
        user.setUsername(request.getUsername());
        user.setEmail(normalizeEmail(request.getEmail()));
        user.setAddress(request.getAddress());
        return userRepository.save(user);
    }

    @Transactional
    public PasswordChangeResult changePassword(String username, String currentPassword,
                                               String newPassword, String confirmPassword) {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmailIgnoreCase(username))
                .orElse(null);
        if (user == null) {
            return PasswordChangeResult.USER_NOT_FOUND;
        }
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            return PasswordChangeResult.CURRENT_PASSWORD_INVALID;
        }
        if (newPassword == null || !newPassword.matches(PASSWORD_PATTERN)) {
            return PasswordChangeResult.NEW_PASSWORD_INVALID;
        }
        if (!newPassword.equals(confirmPassword)) {
            return PasswordChangeResult.CONFIRM_PASSWORD_MISMATCH;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return PasswordChangeResult.SUCCESS;
    }

    public User findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        String normalizedIdentifier = identifier.trim();
        return userRepository.findByUsername(normalizedIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(normalizedIdentifier))
                .orElse(null);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public enum PasswordChangeResult {
        SUCCESS,
        USER_NOT_FOUND,
        CURRENT_PASSWORD_INVALID,
        NEW_PASSWORD_INVALID,
        CONFIRM_PASSWORD_MISMATCH
    }
}
