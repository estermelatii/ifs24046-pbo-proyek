package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    // Menampilkan Halaman Profil Saya
    @GetMapping("/profile")
    public String myProfile(Model model) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", currentUser);
        return "user/profile"; // Nanti kita buat file profile.html
    }

    // Proses Update Profil (Ganti Nama)
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User userForm) {
        User currentUser = authService.getCurrentUser();
        
        if (currentUser != null) {
            // Kita pakai ID dari user yang sedang login agar aman
            userService.updateUser(currentUser.getId().toString(), userForm);
        }
        
        return "redirect:/user/profile?success";
    }
}