package com.example.cybersec.lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * View controller cho hiển thị giao diện Threat Hunting Simulator.
 */
@Controller
public class LabPageController {

    @GetMapping("/lab")
    public String lab(Model model) {
        model.addAttribute("modules", LabCatalog.modules());
        return "lab/lab-index";
    }

    @GetMapping("/lab/{moduleSlug}/{labSlug}")
    public String labDetail(@PathVariable String moduleSlug, @PathVariable String labSlug, Model model) {
        LabCatalog.LabModule module = LabCatalog.findModule(moduleSlug)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lab module not found"));
        LabCatalog.Lab lab = LabCatalog.findLab(module, labSlug)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lab not found"));

        model.addAttribute("modules", LabCatalog.modules());
        model.addAttribute("module", module);
        model.addAttribute("lab", lab);
        return "lab/lab-detail";
    }
}
