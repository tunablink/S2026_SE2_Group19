package com.example.cybersec.lab.controller;

import com.example.cybersec.compete.service.TournamentPointService;
import com.example.cybersec.lab.dto.LabValidationRequest;
import com.example.cybersec.lab.dto.LabValidationResponse;
import com.example.cybersec.lab.service.LabService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST API Controller cho Security Lab validation.
 * Cho phép người dùng submit exploit/defense kỹ thuật và nhận kết quả tự động.
 */
@RestController
@RequestMapping("/api/labs")
public class LabApiController {
    private final LabService labService;
    private final TournamentPointService tournamentPointService;

    public LabApiController(LabService labService, TournamentPointService tournamentPointService) {
        this.labService = labService;
        this.tournamentPointService = tournamentPointService;
    }

    @PostMapping("/validate")
    public ResponseEntity<LabValidationResponse> validateLab(@Valid @RequestBody LabValidationRequest request,
                                                              Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
        LabValidationResponse response = labService.validateLab(request.getLabType(), request.getInput());
        if (response.isSuccess()) {
            tournamentPointService.recordAfterLabPass(authentication.getName(), request.getLabType());
        }
        return ResponseEntity.ok(response);
    }
}
