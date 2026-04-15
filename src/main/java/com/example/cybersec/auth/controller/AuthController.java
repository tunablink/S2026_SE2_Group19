package com.example.cybersec.auth.controller;

import com.example.cybersec.auth.dto.LoginRequest;
import com.example.cybersec.auth.dto.LoginResponse;
import com.example.cybersec.auth.dto.RegisterRequest;
import com.example.cybersec.auth.security.JwtTokenProvider;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller xử lý xác thực và đăng ký người dùng.
 * Cung cấp endpoint cho form đăng nhập, cấp JWT token,
 * và tạo tài khoản mới kèm validation.
 */
@Controller
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession(true);
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new LoginResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not found."));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication failed."));
        }
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") RegisterRequest registerRequest,
                                 BindingResult result, Model model) {
        String normalizedEmail = normalizeEmail(registerRequest.getEmail());
        registerRequest.setEmail(normalizedEmail);

        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            result.rejectValue("username", "error.username", "Username đã tồn tại!");
        }
        if (normalizedEmail != null && !normalizedEmail.isBlank() && userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            result.rejectValue("email", "error.email", "Email da duoc su dung!");
        }
        if (registerRequest.getPassword() != null && !registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp!");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", registerRequest);
            return "register";
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());
        user.setRoles("USER");
        userRepository.save(user);

        model.addAttribute("success", true);
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }
    @GetMapping("/forgot-password")
    public String forgotPassword(@RequestParam(required = false) String sent, Model model) {
        model.addAttribute("sent", "1".equals(sent) || "true".equalsIgnoreCase(sent));
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit() {
        return "redirect:/forgot-password?sent=1";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(required = false) String token, Model model) {
        boolean hasToken = token != null && !token.isBlank();
        model.addAttribute("token", hasToken ? token : "");
        model.addAttribute("invalidToken", !hasToken);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit() {
        return "redirect:/login?reset=done";
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
