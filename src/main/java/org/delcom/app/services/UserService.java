package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- FITUR REGISTER ---
    public User registerNewUser(User user) {
        // Cek apakah email sudah ada
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email sudah terdaftar!");
        }

        // Enkripsi Password (PENTING!)
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Set Default Role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        System.out.println("REGISTER SUKSES: " + user.getEmail());
        return userRepository.save(user);
    }

    // --- FITUR LOGIN (Dipanggil Otomatis oleh Spring Security) ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // CCTV: Ini akan muncul di terminal kalau tombol 'Masuk' ditekan
        System.out.println("------------------------------------------------");
        System.out.println("LOGIN ATTEMPT: Mencoba login dengan email -> " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("GAGAL: Email tidak ditemukan di Database.");
                    return new UsernameNotFoundException("User tidak ditemukan: " + email);
                });

        System.out.println("USER DITEMUKAN: " + user.getEmail());
        System.out.println("Password Hash di DB: " + user.getPassword());
        System.out.println("------------------------------------------------");

        // Mengembalikan object User bawaan Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    // --- HELPER METHODS (Untuk Controller Lain) ---
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserById(String id) {
        try {
            return getUserById(UUID.fromString(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void updateUser(String id, User userDetails) {
        getUserById(id).ifPresent(user -> {
            user.setName(userDetails.getName());
            userRepository.save(user);
        });
    }
}