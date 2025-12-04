package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        // 1. Ambil data autentikasi dari sistem keamanan Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Cek apakah ada yang login?
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("AUTH SERVICE: Tidak ada user yang login (Anonymous).");
            return null;
        }

        // 3. Ambil Email dari sesi login
        String email = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        System.out.println("AUTH SERVICE: Mencari user dengan email -> " + email);

        // 4. Cari User di Database berdasarkan email tadi
        return userRepository.findByEmail(email).orElse(null);
    }
}