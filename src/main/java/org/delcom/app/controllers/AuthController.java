package org.delcom.app.controllers;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
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

    // 1. HALAMAN LOGIN
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm()); 
        return "auth/login"; 
    }

    // 2. HALAMAN REGISTER
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm()); 
        return "auth/register"; 
    }

    // 3. PROSES REGISTER
    @PostMapping("/register/post")
    public String processRegister(@ModelAttribute("registerForm") RegisterForm form) {
        
        // Cek password match
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            return "redirect:/auth/register?error=password_mismatch";
        }

        try {
            User user = new User();
            user.setName(form.getName());
            user.setEmail(form.getEmail());
            user.setPassword(form.getPassword());
            
            userService.registerNewUser(user); // <--- Ini yang bisa bikin error kalau email kembar
            
        } catch (RuntimeException e) {
            // TANGKAP ERRORNYA DISINI
            // Balik lagi ke halaman register dengan pesan error
            return "redirect:/auth/register?error=email_exists";
        }
        
        return "redirect:/auth/login?registered";
    }
}