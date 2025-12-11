package org.delcom.app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.AuthTokenRepository;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // --- METHOD INI YANG DICARI TEST (authenticateUser) ---
    public String authenticateUser(String email, String password) {
        // 1. Cari User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Cek Password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. Generate Token
        String token = jwtUtil.generateToken(user.getEmail());

        // 4. Simpan Token ke DB (Optional, tapi bagus buat security)
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setUserId(user.getId());
        authToken.setCreatedAt(LocalDateTime.now());
        authTokenRepository.save(authToken);

        return token;
    }

    // --- METHOD SPRING SECURITY ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Authority list (kosong dulu gpp)
        );
    }

    // --- METHOD BANTUAN LAIN ---
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        
        if ("anonymousUser".equals(email)) return null;

        return userRepository.findByEmail(email).orElse(null);
    }
}