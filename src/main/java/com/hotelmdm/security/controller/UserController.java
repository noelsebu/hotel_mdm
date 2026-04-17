package com.hotelmdm.security.controller;

import com.hotelmdm.security.model.AppUser;
import com.hotelmdm.security.model.UserRole;
import com.hotelmdm.security.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", UserRole.values());
        return "admin/users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("roles", UserRole.values());
        return "admin/user-form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") AppUser user,
                             BindingResult result,
                             @RequestParam String rawPassword,
                             Model model,
                             RedirectAttributes ra) {
        if (userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "duplicate", "Username already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "admin/user-form";
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        ra.addFlashAttribute("successMessage", "User '" + user.getUsername() + "' created successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(u -> {
            u.setEnabled(!u.isEnabled());
            userRepository.save(u);
        });
        ra.addFlashAttribute("successMessage", "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userRepository.deleteById(id);
        ra.addFlashAttribute("successMessage", "User deleted.");
        return "redirect:/admin/users";
    }
}
