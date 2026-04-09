package com.example.cybersec.dashboard.controller;

import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * View controller cho trang Dashboard (server-rendered HTML).
 */
@Controller
public class DashboardPageController {

    private final UserService userService;

    public DashboardPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
        }
        return "dashboard/dashboard";
    }
}
