package com.example.cybersec.learn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller cho bài học dành cho guest (không cần đăng nhập).
 * Tách ra từ HomeController để phân tách rõ chức năng.
 */
@Controller
public class LearnGuestController {

    @GetMapping("/learn-guest")
    public String learnGuest() {
        return "learn_guest/learn";
    }

    @GetMapping("/learn-guest/{id}")
    public String learnGuestLesson(@PathVariable String id) {
        return "learn_guest/" + id;
    }
}
