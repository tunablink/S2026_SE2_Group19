package com.example.cybersec.auth.controller;

import com.example.cybersec.auth.dto.ChangePasswordRequest;
import com.example.cybersec.auth.dto.ForgotPasswordRequest;
import com.example.cybersec.auth.dto.LoginRequest;
import com.example.cybersec.auth.dto.LoginResponse;
import com.example.cybersec.auth.dto.RegisterRequest;
import com.example.cybersec.user.service.UserService;
import com.example.cybersec.auth.security.JwtTokenProvider;
import com.example.cybersec.auth.service.ForgotPasswordService;
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
    private final ForgotPasswordService forgotPasswordService;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

        public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                          ForgotPasswordService forgotPasswordService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.forgotPasswordService = forgotPasswordService;
        this.userService = userService;
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

        @PostMapping("/auth/forgot-password")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        boolean success = forgotPasswordService.handleForgotPassword(request.getEmail());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Mật khẩu mới đã được gửi về email của bạn."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Email không tồn tại trong hệ thống."));
        }
    }

        @PostMapping("/auth/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu mới xác nhận không khớp."));
        }
        String error = userService.changePassword(request.getUsername(), request.getCurrentPassword(), request.getNewPassword());
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại."));
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") RegisterRequest registerRequest,
                                 BindingResult result, Model model) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                result.rejectValue("username", "error.username", "Username đã tồn tại!");
            }
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                result.rejectValue("email", "error.email", "Email đã được sử dụng!");
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
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());
        user.setEmail(registerRequest.getEmail());
        user.setRoles("USER");
        userRepository.save(user);

        model.addAttribute("success", true);
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }
}
