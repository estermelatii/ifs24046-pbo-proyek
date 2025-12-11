package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginForm Tests")
class LoginFormTests {

    private LoginForm loginForm;

    @BeforeEach
    void setUp() {
        loginForm = new LoginForm();
    }

    // ========== TESTS FOR CONSTRUCTOR ==========

    @Test
    @DisplayName("Test LoginForm() constructor - should create empty form")
    void testLoginFormConstructor() {
        // Act
        LoginForm form = new LoginForm();

        // Assert
        assertNotNull(form);
        assertNull(form.getEmail());
        assertNull(form.getPassword());
        assertFalse(form.isRememberMe()); // default boolean is false
    }

    // ========== TESTS FOR EMAIL GETTER/SETTER ==========

    @Test
    @DisplayName("Test setEmail() and getEmail()")
    void testSetAndGetEmail() {
        // Arrange
        String email = "user@example.com";

        // Act
        loginForm.setEmail(email);

        // Assert
        assertEquals(email, loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setEmail() with null")
    void testSetEmailWithNull() {
        // Act
        loginForm.setEmail(null);

        // Assert
        assertNull(loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setEmail() with empty string")
    void testSetEmailWithEmptyString() {
        // Act
        loginForm.setEmail("");

        // Assert
        assertEquals("", loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setEmail() with valid formats")
    void testSetEmailWithValidFormats() {
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_123@test-domain.com",
            "test@subdomain.example.com"
        };

        for (String email : validEmails) {
            loginForm.setEmail(email);
            assertEquals(email, loginForm.getEmail());
        }
    }

    @Test
    @DisplayName("Test setEmail() with special characters")
    void testSetEmailWithSpecialCharacters() {
        // Arrange
        String emailWithPlus = "user+newsletter@example.com";

        // Act
        loginForm.setEmail(emailWithPlus);

        // Assert
        assertEquals(emailWithPlus, loginForm.getEmail());
    }

    @Test
    @DisplayName("Test getEmail() after multiple sets")
    void testGetEmailAfterMultipleSets() {
        // Act
        loginForm.setEmail("first@example.com");
        loginForm.setEmail("second@example.com");
        loginForm.setEmail("third@example.com");

        // Assert
        assertEquals("third@example.com", loginForm.getEmail());
    }

    // ========== TESTS FOR PASSWORD GETTER/SETTER ==========

    @Test
    @DisplayName("Test setPassword() and getPassword()")
    void testSetAndGetPassword() {
        // Arrange
        String password = "password123";

        // Act
        loginForm.setPassword(password);

        // Assert
        assertEquals(password, loginForm.getPassword());
    }

    @Test
    @DisplayName("Test setPassword() with null")
    void testSetPasswordWithNull() {
        // Act
        loginForm.setPassword(null);

        // Assert
        assertNull(loginForm.getPassword());
    }

    @Test
    @DisplayName("Test setPassword() with empty string")
    void testSetPasswordWithEmptyString() {
        // Act
        loginForm.setPassword("");

        // Assert
        assertEquals("", loginForm.getPassword());
    }

    @Test
    @DisplayName("Test setPassword() with special characters")
    void testSetPasswordWithSpecialCharacters() {
        // Arrange
        String complexPassword = "P@ssw0rd!#$%^&*()";

        // Act
        loginForm.setPassword(complexPassword);

        // Assert
        assertEquals(complexPassword, loginForm.getPassword());
    }

    @Test
    @DisplayName("Test setPassword() with very long string")
    void testSetPasswordWithVeryLongString() {
        // Arrange
        String longPassword = "p".repeat(1000);

        // Act
        loginForm.setPassword(longPassword);

        // Assert
        assertEquals(longPassword, loginForm.getPassword());
        assertEquals(1000, loginForm.getPassword().length());
    }

    @Test
    @DisplayName("Test getPassword() after multiple sets")
    void testGetPasswordAfterMultipleSets() {
        // Act
        loginForm.setPassword("firstPassword");
        loginForm.setPassword("secondPassword");
        loginForm.setPassword("thirdPassword");

        // Assert
        assertEquals("thirdPassword", loginForm.getPassword());
    }

    // ========== TESTS FOR REMEMBER_ME GETTER/SETTER ==========

    @Test
    @DisplayName("Test setRememberMe() and isRememberMe() - true")
    void testSetAndIsRememberMeTrue() {
        // Act
        loginForm.setRememberMe(true);

        // Assert
        assertTrue(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test setRememberMe() and isRememberMe() - false")
    void testSetAndIsRememberMeFalse() {
        // Act
        loginForm.setRememberMe(false);

        // Assert
        assertFalse(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test isRememberMe() default value is false")
    void testIsRememberMeDefaultValue() {
        // Assert
        assertFalse(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test setRememberMe() toggle")
    void testSetRememberMeToggle() {
        // Act & Assert - Initially false
        assertFalse(loginForm.isRememberMe());

        // Toggle to true
        loginForm.setRememberMe(true);
        assertTrue(loginForm.isRememberMe());

        // Toggle back to false
        loginForm.setRememberMe(false);
        assertFalse(loginForm.isRememberMe());

        // Toggle to true again
        loginForm.setRememberMe(true);
        assertTrue(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test setRememberMe() multiple times with same value")
    void testSetRememberMeMultipleTimesSameValue() {
        // Act
        loginForm.setRememberMe(true);
        loginForm.setRememberMe(true);
        loginForm.setRememberMe(true);

        // Assert
        assertTrue(loginForm.isRememberMe());
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Test setting all fields together")
    void testSetAllFieldsTogether() {
        // Arrange
        String email = "test@example.com";
        String password = "testPass123";
        boolean rememberMe = true;

        // Act
        loginForm.setEmail(email);
        loginForm.setPassword(password);
        loginForm.setRememberMe(rememberMe);

        // Assert
        assertEquals(email, loginForm.getEmail());
        assertEquals(password, loginForm.getPassword());
        assertTrue(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test updating existing values")
    void testUpdatingExistingValues() {
        // Arrange - Set initial values
        loginForm.setEmail("initial@example.com");
        loginForm.setPassword("initialPass");
        loginForm.setRememberMe(false);

        // Act - Update values
        loginForm.setEmail("updated@example.com");
        loginForm.setPassword("updatedPass");
        loginForm.setRememberMe(true);

        // Assert
        assertEquals("updated@example.com", loginForm.getEmail());
        assertEquals("updatedPass", loginForm.getPassword());
        assertTrue(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test complete login form scenario")
    void testCompleteLoginFormScenario() {
        // Arrange
        String email = "john.doe@company.com";
        String password = "SecureP@ss123";
        boolean rememberMe = true;

        // Act
        loginForm.setEmail(email);
        loginForm.setPassword(password);
        loginForm.setRememberMe(rememberMe);

        // Assert
        assertNotNull(loginForm);
        assertEquals(email, loginForm.getEmail());
        assertEquals(password, loginForm.getPassword());
        assertTrue(loginForm.isRememberMe());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Test all getters return default values for new instance")
    void testAllGettersReturnDefaultValuesForNewInstance() {
        // Arrange
        LoginForm newForm = new LoginForm();

        // Assert
        assertNull(newForm.getEmail());
        assertNull(newForm.getPassword());
        assertFalse(newForm.isRememberMe());
    }

    @Test
    @DisplayName("Test setEmail() with whitespace")
    void testSetEmailWithWhitespace() {
        // Act
        loginForm.setEmail("  user@example.com  ");

        // Assert
        assertEquals("  user@example.com  ", loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setEmail() with whitespace only")
    void testSetEmailWithWhitespaceOnly() {
        // Act
        loginForm.setEmail("   ");

        // Assert
        assertEquals("   ", loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() with whitespace")
    void testSetPasswordWithWhitespace() {
        // Act
        loginForm.setPassword("  password123  ");

        // Assert
        assertEquals("  password123  ", loginForm.getPassword());
    }

    @Test
    @DisplayName("Test setPassword() with numbers only")
    void testSetPasswordWithNumbersOnly() {
        // Act
        loginForm.setPassword("123456789");

        // Assert
        assertEquals("123456789", loginForm.getPassword());
    }

    @Test
    @DisplayName("Test form with minimum valid data")
    void testFormWithMinimumValidData() {
        // Act
        loginForm.setEmail("a@b.c");
        loginForm.setPassword("p");
        loginForm.setRememberMe(false);

        // Assert
        assertEquals("a@b.c", loginForm.getEmail());
        assertEquals("p", loginForm.getPassword());
        assertFalse(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test multiple instances are independent")
    void testMultipleInstancesAreIndependent() {
        // Arrange
        LoginForm form1 = new LoginForm();
        LoginForm form2 = new LoginForm();

        // Act
        form1.setEmail("user1@example.com");
        form1.setPassword("password1");
        form1.setRememberMe(true);

        form2.setEmail("user2@example.com");
        form2.setPassword("password2");
        form2.setRememberMe(false);

        // Assert
        assertEquals("user1@example.com", form1.getEmail());
        assertEquals("password1", form1.getPassword());
        assertTrue(form1.isRememberMe());

        assertEquals("user2@example.com", form2.getEmail());
        assertEquals("password2", form2.getPassword());
        assertFalse(form2.isRememberMe());

        assertNotEquals(form1.getEmail(), form2.getEmail());
        assertNotEquals(form1.getPassword(), form2.getPassword());
        assertNotEquals(form1.isRememberMe(), form2.isRememberMe());
    }

    @Test
    @DisplayName("Test setEmail() preserves case sensitivity")
    void testSetEmailPreservesCaseSensitivity() {
        // Act
        loginForm.setEmail("User@Example.COM");

        // Assert
        assertEquals("User@Example.COM", loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() preserves case sensitivity")
    void testSetPasswordPreservesCaseSensitivity() {
        // Act
        loginForm.setPassword("PaSsWoRd123");

        // Assert
        assertEquals("PaSsWoRd123", loginForm.getPassword());
    }

    @Test
    @DisplayName("Test form reset scenario")
    void testFormResetScenario() {
        // Arrange - Set initial values
        loginForm.setEmail("user@example.com");
        loginForm.setPassword("password123");
        loginForm.setRememberMe(true);

        // Act - Reset to null/default
        loginForm.setEmail(null);
        loginForm.setPassword(null);
        loginForm.setRememberMe(false);

        // Assert
        assertNull(loginForm.getEmail());
        assertNull(loginForm.getPassword());
        assertFalse(loginForm.isRememberMe());
    }

    @Test
    @DisplayName("Test setEmail() with international characters")
    void testSetEmailWithInternationalCharacters() {
        // Note: This might not be valid email, but testing setter behavior
        String email = "用户@例え.jp";

        // Act
        loginForm.setEmail(email);

        // Assert
        assertEquals(email, loginForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() with unicode characters")
    void testSetPasswordWithUnicodeCharacters() {
        // Arrange
        String unicodePassword = "密码123";

        // Act
        loginForm.setPassword(unicodePassword);

        // Assert
        assertEquals(unicodePassword, loginForm.getPassword());
    }

    @Test
    @DisplayName("Test rememberMe with login credentials")
    void testRememberMeWithLoginCredentials() {
        // Scenario 1: Login with remember me
        loginForm.setEmail("user1@example.com");
        loginForm.setPassword("pass123");
        loginForm.setRememberMe(true);

        assertEquals("user1@example.com", loginForm.getEmail());
        assertEquals("pass123", loginForm.getPassword());
        assertTrue(loginForm.isRememberMe());

        // Scenario 2: Login without remember me
        LoginForm form2 = new LoginForm();
        form2.setEmail("user2@example.com");
        form2.setPassword("pass456");
        form2.setRememberMe(false);

        assertEquals("user2@example.com", form2.getEmail());
        assertEquals("pass456", form2.getPassword());
        assertFalse(form2.isRememberMe());
    }

    @Test
    @DisplayName("Test partial form completion")
    void testPartialFormCompletion() {
        // Only set email
        loginForm.setEmail("partial@example.com");

        assertEquals("partial@example.com", loginForm.getEmail());
        assertNull(loginForm.getPassword());
        assertFalse(loginForm.isRememberMe());

        // Add password
        loginForm.setPassword("password");

        assertEquals("partial@example.com", loginForm.getEmail());
        assertEquals("password", loginForm.getPassword());
        assertFalse(loginForm.isRememberMe());

        // Add remember me
        loginForm.setRememberMe(true);

        assertEquals("partial@example.com", loginForm.getEmail());
        assertEquals("password", loginForm.getPassword());
        assertTrue(loginForm.isRememberMe());
    }
}