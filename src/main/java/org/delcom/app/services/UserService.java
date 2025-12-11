package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- 1. UNTUK AUTH CONTROLLER (Register) ---
    public User registerNewUser(User user) {
        // Enkripsi password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // --- 2. UNTUK UNIT TEST (Biar Test ga error) ---
    // Kita buat alias, jadi kalau test panggil registerUser, dia lari ke registerNewUser
    public User registerUser(User user) {
        return registerNewUser(user);
    }

    // --- 3. UNTUK USER CONTROLLER (Update pakai String ID) ---
    public void updateUser(String id, User updatedData) {
        try {
            UUID uuid = UUID.fromString(id);
            User existingUser = userRepository.findById(uuid).orElse(null);
            if (existingUser != null) {
                updateUser(existingUser, updatedData);
            }
        } catch (IllegalArgumentException e) {
            // Handle jika ID bukan UUID valid
            System.out.println("Invalid UUID format: " + id);
        }
    }

    // --- 4. LOGIKA UPDATE ASLI ---
    public void updateUser(User existingUser, User updatedData) {
        existingUser.setName(updatedData.getName());
        existingUser.setEmail(updatedData.getEmail());
        
        // Cek jika password diubah
        if (updatedData.getPassword() != null && !updatedData.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        
        // Cek jika role diubah (opsional)
        if (updatedData.getRole() != null) {
            existingUser.setRole(updatedData.getRole());
        }

        userRepository.save(existingUser);
    }

    // --- 5. FIND BY EMAIL ---
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }
    
    // --- 6. FIND BY ID ---
    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
}