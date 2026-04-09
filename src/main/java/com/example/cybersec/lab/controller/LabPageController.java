package com.example.cybersec.lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View controller cho hiển thị giao diện Threat Hunting Simulator.
 */
@Controller
public class LabPageController {

    @GetMapping("/lab")
    public String lab() {
        return "lab/lab";
    }
}
