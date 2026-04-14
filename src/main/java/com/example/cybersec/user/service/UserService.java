package com.example.cybersec.user.service;

import com.example.cybersec.auth.dto.RegisterRequest;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional
    public void registerUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setRoles("USER");
        userRepository.save(user);
    }

        public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Đổi mật khẩu cho người dùng.
     *
     * @param username        tên đăng nhập
     * @param currentPassword mật khẩu hiện tại (chưa mã hóa)
     * @param newPassword     mật khẩu mới (chưa mã hóa)
     * @return thông báo kết quả: null nếu thành công, chuỗi lỗi nếu thất bại
     */
    @Transactional
    public String changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "Không tìm thấy người dùng.";
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Mật khẩu hiện tại không đúng.";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return null;
    }
}
