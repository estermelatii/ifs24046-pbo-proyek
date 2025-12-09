package org.delcom.app.controllers;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
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
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        model.addAttribute("items", wishlistService.getAllItems(user));
        model.addAttribute("totalPending", wishlistService.countPending(user));
        model.addAttribute("totalBought", wishlistService.countBought(user));
        return "wishlist/dashboard";
    }

    @GetMapping("/stats")
    public String statistics(Model model) {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("user", user);
        model.addAttribute("items", wishlistService.getAllItems(user)); // Data items untuk kurva
        model.addAttribute("totalPending", wishlistService.countPending(user));
        model.addAttribute("totalBought", wishlistService.countBought(user));
        return "wishlist/stats";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("wishlistForm", new WishlistForm());
        model.addAttribute("isEdit", false); // Penanda Tambah Baru
        return "wishlist/form";
    }

    @PostMapping("/add")
    public String processAdd(@ModelAttribute WishlistForm form) throws IOException {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        wishlistService.addItem(user, form);
        return "redirect:/wishlist";
    }

    // --- FITUR BARU: TAMPILKAN FORM EDIT ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        WishlistItem item = wishlistService.findById(id);
        if (item == null) return "redirect:/wishlist";

        // Salin data lama ke form agar user tidak ngetik ulang
        WishlistForm form = new WishlistForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setCategory(item.getCategory());
        form.setTargetDate(item.getTargetDate());
        form.setShopUrl(item.getShopUrl());
        form.setDescription(item.getDescription());
        
        model.addAttribute("wishlistForm", form);
        model.addAttribute("isEdit", true); // Penanda sedang Edit
        return "wishlist/form";
    }

    // --- FITUR BARU: PROSES UPDATE ---
    @PostMapping("/update")
    public String processUpdate(@ModelAttribute WishlistForm form) throws IOException {
        User user = authService.getCurrentUser();
        if (user == null) return "redirect:/auth/login";
        wishlistService.updateItem(user, form);
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

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable UUID id, Model model) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        var item = wishlistService.getAllItems(currentUser)
                .stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
        if (item == null) return "redirect:/wishlist";
        model.addAttribute("item", item);
        return "wishlist/detail";
    }
}