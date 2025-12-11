package org.delcom.app.utils;

import org.delcom.app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject nilai properti secara manual untuk pengujian
        ReflectionTestUtils.setField(jwtUtil, "secret", "rahasiaNegaraYangSangatPanjangSekali1234567890"); // Sesuai nama variabel 'secret'
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 jam

        // Siapkan user dummy
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@del.ac.id"); // Kita anggap email sebagai username
    }

    @Test
    void testGenerateAndValidateToken() {
        // 1. Buat Token (Gunakan email sebagai username/subject)
        String token = jwtUtil.generateToken(user.getEmail());
        assertNotNull(token);

        // 2. Cek Username dari Token (Pake method extractUsername)
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(user.getEmail(), extractedUsername);

        // 3. Validasi Token (Butuh token DAN username pembanding)
        Boolean isValid = jwtUtil.validateToken(token, user.getEmail());
        assertTrue(isValid);
    }
    
    @Test
    void testGenerateTokenWithUserId() {
        // Test variasi generate token dengan UUID
        String token = jwtUtil.generateToken(user.getId());
        assertNotNull(token);
        
        // Pastikan token valid (subject-nya adalah ID string)
        String subject = jwtUtil.extractUsername(token);
        assertEquals(user.getId().toString(), subject);
    }
}