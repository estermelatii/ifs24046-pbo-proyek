package org.delcom.app.controllers;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired private WishlistService wishlistService;
    @Autowired private AuthService authService; 

    @GetMapping
    public String dashboard(Model model) {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login"; // Jaga-jaga kalau sesi habis

        model.addAttribute("user", user); // Biar nama user muncul di header
        model.addAttribute("items", wishlistService.getAllItems(user));
        model.addAttribute("totalPending", wishlistService.countPending(user));
        model.addAttribute("totalBought", wishlistService.countBought(user));
        
        return "wishlist/dashboard";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("wishlistForm", new WishlistForm());
        return "wishlist/form";
    }

    // --- BAGIAN INI YANG PENTING UNTUK DIPERBAIKI ---
    @PostMapping("/add")
    public String processAdd(@ModelAttribute WishlistForm form) throws IOException {
        // 1. Cari tahu siapa yang login
        User user = authService.getCurrentUser();
        
        // 2. Kalau user tidak ketemu, suruh login lagi (jangan simpan barang)
        if (user == null) {
            return "redirect:/auth/login";
        }

        // 3. Simpan barang dengan melampirkan data user
        wishlistService.addItem(user, form);
        
        return "redirect:/wishlist";
    }

    @GetMapping("/{id}/toggle")
    public String toggleStatus(@PathVariable UUID id) {
        wishlistService.changeStatus(id);
        return "redirect:/wishlist";
    }

    @GetMapping("/{id}/delete")
    public String deleteItem(@PathVariable UUID id) {
        wishlistService.deleteItem(id);
        return "redirect:/wishlist";
    }
}