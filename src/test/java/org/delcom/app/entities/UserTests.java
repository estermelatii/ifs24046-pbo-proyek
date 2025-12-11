package org.delcom.app.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTests {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    // ========== TESTS FOR MISSED METHODS (RED) ==========

    @Test
    @DisplayName("Test toResponseMap() - Should return map with user data")
    void testToResponseMap() {
        // Arrange
        UUID testId = UUID.randomUUID();
        user.setId(testId);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole("USER");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        // Act
        Map<String, Object> responseMap = user.toResponseMap();

        // Assert
        assertNotNull(responseMap);
        assertEquals(testId, responseMap.get("id"));
        assertEquals("John Doe", responseMap.get("name"));
        assertEquals("john@example.com", responseMap.get("email"));
        assertEquals("USER", responseMap.get("role"));
        assertEquals(now, responseMap.get("createdAt"));
        assertFalse(responseMap.containsKey("password"), "Password should not be in response map");
    }

    @Test
    @DisplayName("Test setId(String) - Should convert String to UUID")
    void testSetIdWithString() {
        // Arrange
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";

        // Act
        user.setId(uuidString);

        // Assert
        assertNotNull(user.getId());
        assertEquals(UUID.fromString(uuidString), user.getId());
    }

    @Test
    @DisplayName("Test setId(String) with null - Should handle null gracefully")
    void testSetIdWithNullString() {
        // Act
        user.setId((String) null);

        // Assert
        assertNull(user.getId());
    }

    @Test
    @DisplayName("Test onUpdate() - Should update updatedAt timestamp")
    void testOnUpdate() {
        // Arrange
        LocalDateTime initialTime = LocalDateTime.now().minusDays(1);
        user.setUpdatedAt(initialTime);

        // Act
        // Simulate @PreUpdate callback
        user.onUpdate();

        // Assert
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(initialTime));
    }

    // ========== TESTS FOR COVERED METHODS (GREEN) ==========

    @Test
    @DisplayName("Test onCreate() - Should set createdAt and updatedAt")
    void testOnCreate() {
        // Act
        user.onCreate();

        // Assert
        assertNotNull(user.getCreatedAt(), "createdAt should not be null");
        assertNotNull(user.getUpdatedAt(), "updatedAt should not be null");
        
        // Check that createdAt and updatedAt are equal (or very close) when truncated to milliseconds
        assertEquals(
            user.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), 
            user.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS),
            "createdAt and updatedAt should be equal when created"
        );
        
        // Verify the timestamps are recent (within the last second)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(
            user.getCreatedAt().isAfter(now.minusSeconds(1)),
            "createdAt should be within the last second"
        );
        assertTrue(
            user.getCreatedAt().isBefore(now.plusSeconds(1)),
            "createdAt should not be in the future"
        );
    }

    @Test
    @DisplayName("Test setName() and getName()")
    void testSetAndGetName() {
        // Arrange
        String name = "Jane Smith";

        // Act
        user.setName(name);

        // Assert
        assertEquals(name, user.getName());
    }

    @Test
    @DisplayName("Test setEmail() and getEmail()")
    void testSetAndGetEmail() {
        // Arrange
        String email = "jane@example.com";

        // Act
        user.setEmail(email);

        // Assert
        assertEquals(email, user.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() and getPassword()")
    void testSetAndGetPassword() {
        // Arrange
        String password = "securePassword123";

        // Act
        user.setPassword(password);

        // Assert
        assertEquals(password, user.getPassword());
    }

    @Test
    @DisplayName("Test setRole() and getRole()")
    void testSetAndGetRole() {
        // Arrange
        String role = "ADMIN";

        // Act
        user.setRole(role);

        // Assert
        assertEquals(role, user.getRole());
    }

    @Test
    @DisplayName("Test setCreatedAt() and getCreatedAt()")
    void testSetAndGetCreatedAt() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Act
        user.setCreatedAt(createdAt);

        // Assert
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    @DisplayName("Test setUpdatedAt() and getUpdatedAt()")
    void testSetAndGetUpdatedAt() {
        // Arrange
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 10, 0);

        // Act
        user.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Test setId(UUID) and getId()")
    void testSetAndGetIdWithUUID() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        user.setId(id);

        // Assert
        assertEquals(id, user.getId());
    }

    @Test
    @DisplayName("Test User() constructor - Should create empty user")
    void testUserConstructor() {
        // Act
        User newUser = new User();

        // Assert
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getName());
        assertNull(newUser.getEmail());
        assertNull(newUser.getPassword());
        assertNull(newUser.getRole());
        assertNull(newUser.getCreatedAt());
        assertNull(newUser.getUpdatedAt());
    }

    // ========== ADDITIONAL INTEGRATION TESTS ==========

    @Test
    @DisplayName("Test complete user lifecycle")
    void testCompleteUserLifecycle() {
        // Arrange & Act
        user.onCreate(); // Simulate @PrePersist
        
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setName("Alice Johnson");
        user.setEmail("alice@example.com");
        user.setPassword("password123");
        user.setRole("USER");

        // Simulate update
        try {
            Thread.sleep(10); // Small delay to ensure timestamp difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        user.onUpdate(); // Simulate @PreUpdate

        // Assert
        assertEquals(id, user.getId());
        assertEquals("Alice Johnson", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("USER", user.getRole());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(user.getCreatedAt()) || 
                   user.getUpdatedAt().isEqual(user.getCreatedAt()));
    }

    @Test
    @DisplayName("Test toResponseMap() with null values")
    void testToResponseMapWithNullValues() {
        // Act
        Map<String, Object> responseMap = user.toResponseMap();

        // Assert
        assertNotNull(responseMap);
        assertNull(responseMap.get("id"));
        assertNull(responseMap.get("name"));
        assertNull(responseMap.get("email"));
        assertNull(responseMap.get("role"));
        assertNull(responseMap.get("createdAt"));
    }

    @Test
    @DisplayName("Test setId(String) with invalid UUID format - Should throw exception")
    void testSetIdWithInvalidString() {
        // Arrange
        String invalidUuid = "not-a-valid-uuid";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            user.setId(invalidUuid);
        });
    }

    @Test
    @DisplayName("Test role changes")
    void testRoleChanges() {
        // Act
        user.setRole("USER");
        assertEquals("USER", user.getRole());

        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());

        user.setRole("MODERATOR");
        assertEquals("MODERATOR", user.getRole());

        // Assert
        assertEquals("MODERATOR", user.getRole());
    }

    @Test
    @DisplayName("Test password update")
    void testPasswordUpdate() {
        // Arrange
        String oldPassword = "oldPassword123";
        String newPassword = "newSecurePassword456";

        // Act
        user.setPassword(oldPassword);
        assertEquals(oldPassword, user.getPassword());

        user.setPassword(newPassword);

        // Assert
        assertEquals(newPassword, user.getPassword());
        assertNotEquals(oldPassword, user.getPassword());
    }
}