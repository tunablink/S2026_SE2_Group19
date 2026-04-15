package com.example.cybersec.user.controller;

import com.example.cybersec.auth.security.MyUserDetails;
import com.example.cybersec.user.dto.UpdateProfileRequest;
import com.example.cybersec.user.entity.User;
import com.example.cybersec.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controller quản lý hồ sơ người dùng.
 * Quản lý trang Member Home với dữ liệu cá nhân.
 */
@Controller
public class MemberController {

    private final UserService userService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public MemberController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/member/home")
    public String memberHome(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsernameOrEmail(principal.getName());
            model.addAttribute("user", user);
        }
        return "member-home";
    }

    @GetMapping("/member/profile/edit")
    public String editProfile(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsernameOrEmail(principal.getName());
        if (user == null) {
            redirectAttributes.addFlashAttribute("profileError", "Account not found. Please sign in again.");
            return "redirect:/member/home";
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", toUpdateProfileRequest(user));
        return "member-edit";
    }

    @PostMapping("/member/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profile") UpdateProfileRequest profile,
                                BindingResult result,
                                Model model,
                                Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByUsernameOrEmail(authentication.getName());
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("profileError", "Account not found. Please sign in again.");
            return "redirect:/member/home";
        }

        if (userService.usernameTakenByOtherUser(profile.getUsername(), currentUser.getId())) {
            result.rejectValue("username", "error.username", "Username is already taken!");
        }
        if (userService.emailTakenByOtherUser(profile.getEmail(), currentUser.getId())) {
            result.rejectValue("email", "error.email", "Email is already in use!");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "member-edit";
        }

        User updatedUser = userService.updateProfile(authentication.getName(), profile);
        if (updatedUser == null) {
            redirectAttributes.addFlashAttribute("profileError", "Unable to update your profile. Please try again.");
            return "redirect:/member/home";
        }

        refreshAuthentication(updatedUser, authentication, request, response);
        redirectAttributes.addFlashAttribute("profileSuccess", "Your profile has been updated.");
        return "redirect:/member/home";
    }

    @PostMapping("/member/home/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("passwordError", "Please sign in again before changing your password.");
            return "redirect:/login";
        }

        UserService.PasswordChangeResult result = userService.changePassword(
                principal.getName(),
                currentPassword,
                newPassword,
                confirmPassword
        );

        switch (result) {
            case SUCCESS -> redirectAttributes.addFlashAttribute("passwordSuccess", "Your password has been updated.");
            case CURRENT_PASSWORD_INVALID -> redirectAttributes.addFlashAttribute("passwordError", "Current password is incorrect.");
            case NEW_PASSWORD_INVALID -> redirectAttributes.addFlashAttribute("passwordError", "New password must be 6-60 characters and include at least 1 number and 1 uppercase letter.");
            case CONFIRM_PASSWORD_MISMATCH -> redirectAttributes.addFlashAttribute("passwordError", "Password confirmation does not match.");
            case USER_NOT_FOUND -> redirectAttributes.addFlashAttribute("passwordError", "Account not found. Please sign in again.");
        }

        return "redirect:/member/home";
    }

    private UpdateProfileRequest toUpdateProfileRequest(User user) {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setAddress(user.getAddress());
        return request;
    }

    private void refreshAuthentication(User user, Authentication currentAuthentication,
                                       HttpServletRequest request, HttpServletResponse response) {
        MyUserDetails userDetails = new MyUserDetails(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                currentAuthentication.getCredentials(),
                userDetails.getAuthorities()
        );
        authentication.setDetails(currentAuthentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
    }
}
