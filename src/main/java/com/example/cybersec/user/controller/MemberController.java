package com.example.cybersec.user.controller;

import com.example.cybersec.user.dto.ChangePasswordRequest;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;

/**
 * Controller quản lý hồ sơ người dùng.
 * Quản lý trang Member Home với dữ liệu cá nhân.
 */
@Controller
public class MemberController {

    private final UserService userService;

    public MemberController(UserService userService) {
        this.userService = userService;
    }

        @GetMapping("/member/home")
    public String memberHome(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
        }
        return "member-home";
    }

    @PostMapping("/member/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                             Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Bạn chưa đăng nhập."));
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu mới xác nhận không khớp."));
        }

        String error = userService.changePassword(principal.getName(), request.getCurrentPassword(), request.getNewPassword());
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }

        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
    }
}
