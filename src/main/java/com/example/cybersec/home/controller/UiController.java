package com.example.cybersec.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Utility controller trình diễn các UI components dùng chung.
 * Trang tĩnh trưng bày alert, button và các phần tử UI chuẩn.
 */
@Controller
public class UiController {

    @GetMapping("/ui-components")
    public String showUiComponents(Model model) {
        model.addAttribute("successMessage", "This is a demonstration of a success alert!");
        model.addAttribute("infoMessage", "This is a demonstration of an info alert.");
        model.addAttribute("errorMessage", "This is a demonstration of an error alert.");
        return "ui-components";
    }
}
