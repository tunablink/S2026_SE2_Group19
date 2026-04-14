package com.example.cybersec.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller cho các trang công khai: trang chủ và đăng nhập.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String reset, Model model) {
        model.addAttribute("resetDone", "done".equals(reset));
        return "login";
    }
}
