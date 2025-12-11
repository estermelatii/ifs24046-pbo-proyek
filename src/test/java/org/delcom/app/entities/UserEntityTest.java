package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testUser() {
        User user = new User();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        user.setId(id);
        user.setName("Ester");
        user.setEmail("ester@del.ac.id");
        user.setPassword("pass123");
        user.setRole("USER");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(id, user.getId());
        assertEquals("Ester", user.getName());
        assertEquals("ester@del.ac.id", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("USER", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testAuthToken() {
        AuthToken token = new AuthToken();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        token.setId(id);
        token.setToken("xyz-123");
        token.setUserId(userId);
        token.setCreatedAt(now);

        assertEquals(id, token.getId());
        assertEquals("xyz-123", token.getToken());
        assertEquals(userId, token.getUserId());
        assertEquals(now, token.getCreatedAt());
    }
}