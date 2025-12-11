package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.AuthTokenRepository;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticateUser_Success() {
        // 1. Siapkan data dummy
        String email = "test@del.ac.id";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        String dummyToken = "jwt-token-xyz";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(encodedPassword);

        // 2. Mocking perilaku dependency
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        // Penting: sesuaikan dengan method generateToken di JwtUtil kamu (pakai string/uuid)
        when(jwtUtil.generateToken(any(String.class))).thenReturn(dummyToken); 

        // 3. Jalankan method
        String resultToken = authService.authenticateUser(email, rawPassword);

        // 4. Verifikasi
        assertEquals(dummyToken, resultToken);
        // Pastikan token disimpan ke database
        verify(authTokenRepository, times(1)).save(any(AuthToken.class));
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        String email = "notfound@del.ac.id";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Harusnya error karena user tidak ada
        assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(email, "pass");
        });
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        String email = "test@del.ac.id";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPass");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        // Harusnya error karena password salah
        assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(email, "wrongPass");
        });
    }

    @Test
    void testLoadUserByUsername() {
        String email = "user@del.ac.id";
        User user = new User();
        user.setEmail(email);
        user.setPassword("pass");
        user.setRole("USER"); // Pastikan role ada biar ga error authority

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = authService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }
}