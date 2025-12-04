package org.delcom.app.controllers;

import org.delcom.app.entities.User; // Pastikan pakai User entity
import org.delcom.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Menampilkan Halaman Login
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login"; 
    }

    // Menampilkan Halaman Register
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // PERBAIKAN: Gunakan new User() agar cocok dengan method POST di bawah
        model.addAttribute("user", new User()); 
        return "auth/register"; 
    }

    // Proses Register
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user) {
        // Simpan user baru
        userService.registerNewUser(user); 
        return "redirect:/auth/login?registered";
    }
}