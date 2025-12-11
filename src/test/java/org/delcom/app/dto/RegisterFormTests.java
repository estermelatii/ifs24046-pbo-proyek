package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RegisterForm Tests")
class RegisterFormTests {

    private RegisterForm registerForm;

    @BeforeEach
    void setUp() {
        registerForm = new RegisterForm();
    }

    // ========== TESTS FOR CONSTRUCTOR ==========

    @Test
    @DisplayName("Test RegisterForm() constructor - should create empty form")
    void testRegisterFormConstructor() {
        // Act
        RegisterForm form = new RegisterForm();

        // Assert
        assertNotNull(form);
        assertNull(form.getName());
        assertNull(form.getEmail());
        assertNull(form.getPassword());
        assertNull(form.getPasswordConfirm());
    }

    // ========== TESTS FOR GETTERS AND SETTERS ==========

    @Test
    @DisplayName("Test setName() and getName()")
    void testSetAndGetName() {
        // Arrange
        String name = "John Doe";

        // Act
        registerForm.setName(name);

        // Assert
        assertEquals(name, registerForm.getName());
    }

    @Test
    @DisplayName("Test setEmail() and getEmail()")
    void testSetAndGetEmail() {
        // Arrange
        String email = "john@example.com";

        // Act
        registerForm.setEmail(email);

        // Assert
        assertEquals(email, registerForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() and getPassword()")
    void testSetAndGetPassword() {
        // Arrange
        String password = "password123";

        // Act
        registerForm.setPassword(password);

        // Assert
        assertEquals(password, registerForm.getPassword());
    }

    @Test
    @DisplayName("Test setPasswordConfirm() and getPasswordConfirm()")
    void testSetAndGetPasswordConfirm() {
        // Arrange
        String passwordConfirm = "password123";

        // Act
        registerForm.setPasswordConfirm(passwordConfirm);

        // Assert
        assertEquals(passwordConfirm, registerForm.getPasswordConfirm());
    }

    // ========== TESTS WITH NULL VALUES ==========

    @Test
    @DisplayName("Test setName() with null")
    void testSetNameWithNull() {
        // Act
        registerForm.setName(null);

        // Assert
        assertNull(registerForm.getName());
    }

    @Test
    @DisplayName("Test setEmail() with null")
    void testSetEmailWithNull() {
        // Act
        registerForm.setEmail(null);

        // Assert
        assertNull(registerForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() with null")
    void testSetPasswordWithNull() {
        // Act
        registerForm.setPassword(null);

        // Assert
        assertNull(registerForm.getPassword());
    }

    @Test
    @DisplayName("Test setPasswordConfirm() with null")
    void testSetPasswordConfirmWithNull() {
        // Act
        registerForm.setPasswordConfirm(null);

        // Assert
        assertNull(registerForm.getPasswordConfirm());
    }

    // ========== TESTS WITH EMPTY VALUES ==========

    @Test
    @DisplayName("Test setName() with empty string")
    void testSetNameWithEmptyString() {
        // Act
        registerForm.setName("");

        // Assert
        assertEquals("", registerForm.getName());
    }

    @Test
    @DisplayName("Test setEmail() with empty string")
    void testSetEmailWithEmptyString() {
        // Act
        registerForm.setEmail("");

        // Assert
        assertEquals("", registerForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() with empty string")
    void testSetPasswordWithEmptyString() {
        // Act
        registerForm.setPassword("");

        // Assert
        assertEquals("", registerForm.getPassword());
    }

    @Test
    @DisplayName("Test setPasswordConfirm() with empty string")
    void testSetPasswordConfirmWithEmptyString() {
        // Act
        registerForm.setPasswordConfirm("");

        // Assert
        assertEquals("", registerForm.getPasswordConfirm());
    }

    // ========== TESTS WITH SPECIAL CHARACTERS ==========

    @Test
    @DisplayName("Test setName() with special characters")
    void testSetNameWithSpecialCharacters() {
        // Arrange
        String specialName = "José María O'Brien-Smith";

        // Act
        registerForm.setName(specialName);

        // Assert
        assertEquals(specialName, registerForm.getName());
    }

    @Test
    @DisplayName("Test setEmail() with plus sign")
    void testSetEmailWithPlusSign() {
        // Arrange
        String emailWithPlus = "user+tag@example.com";

        // Act
        registerForm.setEmail(emailWithPlus);

        // Assert
        assertEquals(emailWithPlus, registerForm.getEmail());
    }

    @Test
    @DisplayName("Test setEmail() with subdomain")
    void testSetEmailWithSubdomain() {
        // Arrange
        String emailWithSubdomain = "user@mail.example.co.uk";

        // Act
        registerForm.setEmail(emailWithSubdomain);

        // Assert
        assertEquals(emailWithSubdomain, registerForm.getEmail());
    }

    @Test
    @DisplayName("Test setPassword() with special characters")
    void testSetPasswordWithSpecialCharacters() {
        // Arrange
        String complexPassword = "P@ssw0rd!#$%";

        // Act
        registerForm.setPassword(complexPassword);

        // Assert
        assertEquals(complexPassword, registerForm.getPassword());
    }

    // ========== TESTS WITH LONG VALUES ==========

    @Test
    @DisplayName("Test setName() with very long string")
    void testSetNameWithVeryLongString() {
        // Arrange
        String longName = "A".repeat(500);

        // Act
        registerForm.setName(longName);

        // Assert
        assertEquals(longName, registerForm.getName());
        assertEquals(500, registerForm.getName().length());
    }

    @Test
    @DisplayName("Test setPassword() with very long string")
    void testSetPasswordWithVeryLongString() {
        // Arrange
        String longPassword = "p".repeat(1000);

        // Act
        registerForm.setPassword(longPassword);

        // Assert
        assertEquals(longPassword, registerForm.getPassword());
        assertEquals(1000, registerForm.getPassword().length());
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Test setting all fields together")
    void testSetAllFieldsTogether() {
        // Arrange
        String name = "Alice Johnson";
        String email = "alice@example.com";
        String password = "securePass123";
        String passwordConfirm = "securePass123";

        // Act
        registerForm.setName(name);
        registerForm.setEmail(email);
        registerForm.setPassword(password);
        registerForm.setPasswordConfirm(passwordConfirm);

        // Assert
        assertEquals(name, registerForm.getName());
        assertEquals(email, registerForm.getEmail());
        assertEquals(password, registerForm.getPassword());
        assertEquals(passwordConfirm, registerForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test updating existing values")
    void testUpdatingExistingValues() {
        // Arrange - Set initial values
        registerForm.setName("Initial Name");
        registerForm.setEmail("initial@example.com");
        registerForm.setPassword("initialPass");
        registerForm.setPasswordConfirm("initialPass");

        // Act - Update values
        registerForm.setName("Updated Name");
        registerForm.setEmail("updated@example.com");
        registerForm.setPassword("updatedPass");
        registerForm.setPasswordConfirm("updatedPass");

        // Assert
        assertEquals("Updated Name", registerForm.getName());
        assertEquals("updated@example.com", registerForm.getEmail());
        assertEquals("updatedPass", registerForm.getPassword());
        assertEquals("updatedPass", registerForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test password and passwordConfirm can be different")
    void testPasswordAndPasswordConfirmDifferent() {
        // Act
        registerForm.setPassword("password123");
        registerForm.setPasswordConfirm("differentPassword");

        // Assert
        assertEquals("password123", registerForm.getPassword());
        assertEquals("differentPassword", registerForm.getPasswordConfirm());
        assertNotEquals(registerForm.getPassword(), registerForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test password and passwordConfirm can be same")
    void testPasswordAndPasswordConfirmSame() {
        // Act
        String password = "samePassword123";
        registerForm.setPassword(password);
        registerForm.setPasswordConfirm(password);

        // Assert
        assertEquals(password, registerForm.getPassword());
        assertEquals(password, registerForm.getPasswordConfirm());
        assertEquals(registerForm.getPassword(), registerForm.getPasswordConfirm());
    }

    // ========== TESTS WITH WHITESPACE ==========

    @Test
    @DisplayName("Test setName() with whitespace only")
    void testSetNameWithWhitespaceOnly() {
        // Act
        registerForm.setName("   ");

        // Assert
        assertEquals("   ", registerForm.getName());
    }

    @Test
    @DisplayName("Test setName() with leading and trailing whitespace")
    void testSetNameWithLeadingTrailingWhitespace() {
        // Act
        registerForm.setName("  John Doe  ");

        // Assert
        assertEquals("  John Doe  ", registerForm.getName());
    }

    @Test
    @DisplayName("Test setEmail() with whitespace")
    void testSetEmailWithWhitespace() {
        // Act
        registerForm.setEmail("  user@example.com  ");

        // Assert
        assertEquals("  user@example.com  ", registerForm.getEmail());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Test getName() returns correct value after multiple sets")
    void testGetNameAfterMultipleSets() {
        // Act
        registerForm.setName("First");
        registerForm.setName("Second");
        registerForm.setName("Third");

        // Assert
        assertEquals("Third", registerForm.getName());
    }

    @Test
    @DisplayName("Test all getters return null for new instance")
    void testAllGettersReturnNullForNewInstance() {
        // Arrange
        RegisterForm newForm = new RegisterForm();

        // Assert
        assertNull(newForm.getName());
        assertNull(newForm.getEmail());
        assertNull(newForm.getPassword());
        assertNull(newForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test form with minimum valid data")
    void testFormWithMinimumValidData() {
        // Act
        registerForm.setName("A");
        registerForm.setEmail("a@b.c");
        registerForm.setPassword("p");
        registerForm.setPasswordConfirm("p");

        // Assert
        assertEquals("A", registerForm.getName());
        assertEquals("a@b.c", registerForm.getEmail());
        assertEquals("p", registerForm.getPassword());
        assertEquals("p", registerForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test setEmail() with various valid formats")
    void testSetEmailWithVariousValidFormats() {
        String[] emails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_123@test-domain.com",
            "123@example.com"
        };

        for (String email : emails) {
            registerForm.setEmail(email);
            assertEquals(email, registerForm.getEmail());
        }
    }

    @Test
    @DisplayName("Test setPassword() with numbers only")
    void testSetPasswordWithNumbersOnly() {
        // Act
        registerForm.setPassword("123456789");

        // Assert
        assertEquals("123456789", registerForm.getPassword());
    }

    @Test
    @DisplayName("Test setPasswordConfirm() with numbers only")
    void testSetPasswordConfirmWithNumbersOnly() {
        // Act
        registerForm.setPasswordConfirm("987654321");

        // Assert
        assertEquals("987654321", registerForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test complete registration form scenario")
    void testCompleteRegistrationFormScenario() {
        // Arrange
        String name = "Bob Smith";
        String email = "bob.smith@company.com";
        String password = "SecureP@ss123";
        String passwordConfirm = "SecureP@ss123";

        // Act
        registerForm.setName(name);
        registerForm.setEmail(email);
        registerForm.setPassword(password);
        registerForm.setPasswordConfirm(passwordConfirm);

        // Assert
        assertNotNull(registerForm);
        assertEquals(name, registerForm.getName());
        assertEquals(email, registerForm.getEmail());
        assertEquals(password, registerForm.getPassword());
        assertEquals(passwordConfirm, registerForm.getPasswordConfirm());
        assertEquals(registerForm.getPassword(), registerForm.getPasswordConfirm());
    }

    @Test
    @DisplayName("Test setName() preserves Unicode characters")
    void testSetNamePreservesUnicodeCharacters() {
        // Arrange
        String unicodeName = "测试用户";

        // Act
        registerForm.setName(unicodeName);

        // Assert
        assertEquals(unicodeName, registerForm.getName());
    }

    @Test
    @DisplayName("Test multiple instances are independent")
    void testMultipleInstancesAreIndependent() {
        // Arrange
        RegisterForm form1 = new RegisterForm();
        RegisterForm form2 = new RegisterForm();

        // Act
        form1.setName("User One");
        form2.setName("User Two");

        // Assert
        assertEquals("User One", form1.getName());
        assertEquals("User Two", form2.getName());
        assertNotEquals(form1.getName(), form2.getName());
    }
}