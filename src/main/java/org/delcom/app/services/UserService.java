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

    // --- 1. FITUR REGISTER (Baru) ---
    public User registerNewUser(User user) {
        // Validasi Email Duplikat
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email sudah digunakan! Silakan gunakan email lain.");
        }

        // Enkripsi Password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        return userRepository.save(user);
    }

    // --- 2. FITUR CARI USER (Untuk Login/Profile) ---
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // --- 3. FITUR UPDATE USER (Yang tadi hilang) ---
    // Error di UserController minta method: updateUser(String, User)
    public void updateUser(String id, User updatedData) {
        // Ubah ID dari String ke UUID
        UUID userId = UUID.fromString(id);
        
        // Cari user lama di database
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Update Data (Nama)
        existingUser.setName(updatedData.getName());

        // Update Password (Hanya jika user mengisi password baru)
        if (updatedData.getPassword() != null && !updatedData.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedData.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        // Simpan Perubahan
        userRepository.save(existingUser);
    }
}