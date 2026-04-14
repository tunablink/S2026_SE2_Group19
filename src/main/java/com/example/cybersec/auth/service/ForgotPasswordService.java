package com.example.cybersec.auth.service;

import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

/**
 * Service xử lý quên mật khẩu.
 * Tạo mật khẩu mới ngẫu nhiên, cập nhật vào DB và gửi về email người dùng.
 */
@Service
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
    private static final int NEW_PASSWORD_LENGTH = 10;

    public ForgotPasswordService(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    /**
     * Xử lý yêu cầu quên mật khẩu.
     *
     * @param email email người dùng đã đăng ký
     * @return true nếu tìm thấy email và gửi mail thành công, false nếu email không tồn tại
     */
    public boolean handleForgotPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        String newPassword = generateRandomPassword();

        // Cập nhật mật khẩu mới (đã mã hóa) vào DB
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Gửi mật khẩu mới về email
        sendPasswordEmail(email, user.getUsername(), newPassword);
        return true;
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(NEW_PASSWORD_LENGTH);
        for (int i = 0; i < NEW_PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private void sendPasswordEmail(String toEmail, String username, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[CyberSec] Mật khẩu mới của bạn");
        message.setText(
                "Xin chào " + username + ",\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu. Dưới đây là mật khẩu mới của bạn:\n\n"
                + "    Mật khẩu mới: " + newPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi đăng nhập.\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng liên hệ quản trị viên.\n\n"
                + "Trân trọng,\n"
                + "CyberSec Team"
        );
        mailSender.send(message);
    }
}