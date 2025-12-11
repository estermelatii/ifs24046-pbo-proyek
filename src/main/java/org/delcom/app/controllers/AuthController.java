package org.delcom.app.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    // --- HALAMAN LOGIN (GET) ---
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    // --- PROSES LOGIN (POST) -> INI YANG TADINYA HILANG ---
    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginForm loginForm, 
                               HttpServletResponse response, 
                               RedirectAttributes redirectAttributes) {
        try {
            // 1. Cek User & Password via AuthService
            String token = authService.authenticateUser(loginForm.getEmail(), loginForm.getPassword());
            
            // 2. Simpan Token di Cookie agar browser ingat
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1 Jam
            response.addCookie(cookie);

            return "redirect:/wishlist";
        } catch (Exception e) {
            // Jika gagal
            redirectAttributes.addFlashAttribute("error", "Email atau Password salah!");
            return "redirect:/auth/login?error=true";
        }
    }

    // --- HALAMAN REGISTER (GET) ---
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    // --- PROSES REGISTER (POST) -> INI JUGA TADINYA HILANG ---
    @PostMapping("/register")
    public String processRegister(@ModelAttribute RegisterForm registerForm) {
        // Mapping dari DTO ke Entity User
        User user = new User();
        user.setName(registerForm.getName());
        user.setEmail(registerForm.getEmail());
        user.setPassword(registerForm.getPassword());
        user.setRole("USER"); // Default Role

        // Simpan User
        userService.registerNewUser(user);

        return "redirect:/auth/login";
    }
    
    // --- LOGOUT ---
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Hapus cookie
        response.addCookie(cookie);
        return "redirect:/auth/login";
    }
}