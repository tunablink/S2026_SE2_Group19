package com.example.cybersec.user.controller;

import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

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
}
