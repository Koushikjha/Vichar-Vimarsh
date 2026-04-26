package com.example.dashboard.controller;

import com.example.auth.util.JwtUtil;
import com.example.user.repository.UserRepository;
import com.example.user.entity.User;
import com.example.auth.service.OnlineUserTracker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OnlineUserTracker onlineUserTracker;

    // ───────────────── LOGIN PAGE ─────────────────
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ───────────────── REGISTER PAGE ─────────────────
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }
        // auth.getName() returns phone (UserDetails.getUsername() returns phone in User entity)
        String phone = auth.getName();

        // Look up the actual display username
        User currentUser = userRepo.findByPhone(phone).orElse(null);
        String displayName = (currentUser != null && currentUser.getUsername() != null)
                ? currentUser.getUsername()
                : phone;

        onlineUserTracker.userLoggedIn(displayName);
        model.addAttribute("username", displayName);
        model.addAttribute("phone", phone);

        List<User> otherUsers = userRepo.findAll().stream()
                .filter(u -> !phone.equals(u.getPhone()))
                .collect(Collectors.toList());

        model.addAttribute("otherUsers", otherUsers);

        return "home";
    }


}