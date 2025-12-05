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

    // 1. DASHBOARD
    @GetMapping
    public String dashboard(Model model) {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);
        model.addAttribute("items", wishlistService.getAllItems(user));
        model.addAttribute("totalPending", wishlistService.countPending(user));
        model.addAttribute("totalBought", wishlistService.countBought(user));
        
        return "wishlist/dashboard";
    }

    // 2. HALAMAN STATISTIK
    // Posisi tetap DI ATAS /{id} agar aman dari error UUID
    @GetMapping("/stats")
    public String statistics(Model model) {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);
        
        // --- [PENAMBAHAN PENTING] ---
        // Kita kirim daftar barang ke halaman statistik
        // agar bisa diambil datanya (Nama & Harga) untuk bikin Kurva Grafik
        model.addAttribute("items", wishlistService.getAllItems(user)); 
        // -----------------------------

        model.addAttribute("totalPending", wishlistService.countPending(user));
        model.addAttribute("totalBought", wishlistService.countBought(user));

        return "wishlist/stats";
    }

    // 3. FORM TAMBAH
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("wishlistForm", new WishlistForm());
        return "wishlist/form";
    }

    @PostMapping("/add")
    public String processAdd(@ModelAttribute WishlistForm form) throws IOException {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";

        wishlistService.addItem(user, form);
        return "redirect:/wishlist";
    }

    // 4. ACTION TOGGLE & DELETE
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

    // 5. DETAIL BARANG (Wajib paling bawah)
    @GetMapping("/{id}")
    public String viewDetail(@PathVariable UUID id, Model model) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        
        var item = wishlistService.getAllItems(currentUser)
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (item == null) return "redirect:/wishlist";

        model.addAttribute("item", item);
        return "wishlist/detail";
    }
}