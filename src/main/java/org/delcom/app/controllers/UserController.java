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

    @Autowired private AuthService authService;
    @Autowired private UserService userService;

    // Tampilkan Halaman Profil
    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);
        return "user/profile"; // Mengarah ke templates/user/profile.html
    }

    // Proses Update Profil
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User updatedUser) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";

        // Panggil service untuk update nama & password
        userService.updateUser(currentUser.getId().toString(), updatedUser);

        return "redirect:/user/profile?success";
    }
}