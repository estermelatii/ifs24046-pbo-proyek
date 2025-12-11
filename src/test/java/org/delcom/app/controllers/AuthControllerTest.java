package org.delcom.app.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTests {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;

    private LoginForm loginForm;
    private RegisterForm registerForm;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup login form
        loginForm = new LoginForm();
        loginForm.setEmail("test@example.com");
        loginForm.setPassword("password123");

        // Setup register form
        registerForm = new RegisterForm();
        registerForm.setName("John Doe");
        registerForm.setEmail("john@example.com");
        registerForm.setPassword("password123");

        // Setup test user
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");
    }

    // ========== TESTS FOR MISSED METHOD (RED - processLogin with branches) ==========

    @Test
    @DisplayName("Test processLogin() with valid credentials - should redirect to wishlist")
    void testProcessLoginSuccess() {
        // Arrange
        String mockToken = "mock-jwt-token-12345";
        when(authService.authenticateUser(loginForm.getEmail(), loginForm.getPassword()))
            .thenReturn(mockToken);

        // Act
        String result = authController.processLogin(loginForm, response, redirectAttributes);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(authService).authenticateUser(loginForm.getEmail(), loginForm.getPassword());
        
        // Verify cookie was added
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("token", capturedCookie.getName());
        assertEquals(mockToken, capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(3600, capturedCookie.getMaxAge());
    }

    @Test
    @DisplayName("Test processLogin() with invalid credentials - should redirect to login with error")
    void testProcessLoginFailure() {
        // Arrange
        when(authService.authenticateUser(loginForm.getEmail(), loginForm.getPassword()))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        String result = authController.processLogin(loginForm, response, redirectAttributes);

        // Assert
        assertEquals("redirect:/auth/login?error=true", result);
        verify(authService).authenticateUser(loginForm.getEmail(), loginForm.getPassword());
        verify(redirectAttributes).addFlashAttribute("error", "Email atau Password salah!");
        verify(response, never()).addCookie(any());
    }

    @Test
    @DisplayName("Test processLogin() exception handling with different exceptions")
    void testProcessLoginWithVariousExceptions() {
        // Test with RuntimeException
        when(authService.authenticateUser(anyString(), anyString()))
            .thenThrow(new RuntimeException("Database error"));
        
        String result1 = authController.processLogin(loginForm, response, redirectAttributes);
        assertEquals("redirect:/auth/login?error=true", result1);
        verify(redirectAttributes, atLeastOnce()).addFlashAttribute("error", "Email atau Password salah!");

        // Reset mock untuk test berikutnya
        reset(authService, redirectAttributes);

        // Test with IllegalArgumentException
        when(authService.authenticateUser(anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Invalid input"));
        
        String result2 = authController.processLogin(loginForm, response, redirectAttributes);
        assertEquals("redirect:/auth/login?error=true", result2);
        verify(redirectAttributes).addFlashAttribute("error", "Email atau Password salah!");
    }

    @Test
    @DisplayName("Test processLogin() sets cookie with correct properties")
    void testProcessLoginCookieProperties() {
        // Arrange
        String token = "test-token-abc";
        when(authService.authenticateUser(anyString(), anyString())).thenReturn(token);

        // Act
        authController.processLogin(loginForm, response, redirectAttributes);

        // Assert
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        
        Cookie cookie = cookieCaptor.getValue();
        assertEquals("token", cookie.getName());
        assertEquals(token, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge()); // 1 hour
    }

    // ========== TESTS FOR COVERED METHODS (GREEN) ==========

    @Test
    @DisplayName("Test showLoginForm() - should return login view")
    void testShowLoginForm() {
        // Act
        String result = authController.showLoginForm(model);

        // Assert
        assertEquals("auth/login", result);
        verify(model).addAttribute(eq("loginForm"), any(LoginForm.class));
    }

    @Test
    @DisplayName("Test showRegisterForm() - should return register view")
    void testShowRegisterForm() {
        // Act
        String result = authController.showRegisterForm(model);

        // Assert
        assertEquals("auth/register", result);
        verify(model).addAttribute(eq("registerForm"), any(RegisterForm.class));
    }

    @Test
    @DisplayName("Test processRegister() - should register user and redirect to login")
    void testProcessRegisterSuccess() {
        // Arrange - userService.registerNewUser() mengembalikan User, bukan void
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        // Act
        String result = authController.processRegister(registerForm);

        // Assert
        assertEquals("redirect:/auth/login", result);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerNewUser(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals("John Doe", capturedUser.getName());
        assertEquals("john@example.com", capturedUser.getEmail());
        assertEquals("password123", capturedUser.getPassword());
        assertEquals("USER", capturedUser.getRole());
    }

    @Test
    @DisplayName("Test logout() - should clear cookie and redirect to login")
    void testLogout() {
        // Act
        String result = authController.logout(response);

        // Assert
        assertEquals("redirect:/auth/login", result);
        
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        
        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("token", capturedCookie.getName());
        assertNull(capturedCookie.getValue());
        assertEquals("/", capturedCookie.getPath());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals(0, capturedCookie.getMaxAge()); // Cookie deleted
    }

    @Test
    @DisplayName("Test AuthController() constructor")
    void testAuthControllerConstructor() {
        // Act
        AuthController controller = new AuthController();

        // Assert
        assertNotNull(controller);
    }

    // ========== ADDITIONAL EDGE CASE & INTEGRATION TESTS ==========

    @Test
    @DisplayName("Test processLogin() with empty credentials")
    void testProcessLoginWithEmptyCredentials() {
        // Arrange
        LoginForm emptyForm = new LoginForm();
        emptyForm.setEmail("");
        emptyForm.setPassword("");
        
        when(authService.authenticateUser("", ""))
            .thenThrow(new RuntimeException("Empty credentials"));

        // Act
        String result = authController.processLogin(emptyForm, response, redirectAttributes);

        // Assert
        assertEquals("redirect:/auth/login?error=true", result);
        verify(redirectAttributes).addFlashAttribute("error", "Email atau Password salah!");
    }

    @Test
    @DisplayName("Test processLogin() with null password")
    void testProcessLoginWithNullPassword() {
        // Arrange
        loginForm.setPassword(null);
        when(authService.authenticateUser(loginForm.getEmail(), null))
            .thenThrow(new RuntimeException("Null password"));

        // Act
        String result = authController.processLogin(loginForm, response, redirectAttributes);

        // Assert
        assertEquals("redirect:/auth/login?error=true", result);
    }

    @Test
    @DisplayName("Test processRegister() sets default USER role")
    void testProcessRegisterSetsDefaultRole() {
        // Arrange
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        // Act
        authController.processRegister(registerForm);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerNewUser(userCaptor.capture());
        
        User user = userCaptor.getValue();
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("Test processRegister() with special characters in name")
    void testProcessRegisterWithSpecialCharacters() {
        // Arrange
        registerForm.setName("José María O'Brien");
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        // Act
        String result = authController.processRegister(registerForm);

        // Assert
        assertEquals("redirect:/auth/login", result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerNewUser(userCaptor.capture());
        assertEquals("José María O'Brien", userCaptor.getValue().getName());
    }

    @Test
    @DisplayName("Test logout() cookie deletion properties")
    void testLogoutCookieDeletionProperties() {
        // Act
        authController.logout(response);

        // Assert
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        
        Cookie cookie = cookieCaptor.getValue();
        assertEquals(0, cookie.getMaxAge()); // MaxAge 0 means delete
        assertNull(cookie.getValue()); // Value should be null
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }

    @Test
    @DisplayName("Test showLoginForm() creates new LoginForm instance")
    void testShowLoginFormCreatesNewInstance() {
        // Act
        authController.showLoginForm(model);

        // Assert
        verify(model).addAttribute(eq("loginForm"), argThat(form -> {
            return form instanceof LoginForm;
        }));
    }

    @Test
    @DisplayName("Test showRegisterForm() creates new RegisterForm instance")
    void testShowRegisterFormCreatesNewInstance() {
        // Act
        authController.showRegisterForm(model);

        // Assert
        verify(model).addAttribute(eq("registerForm"), argThat(form -> {
            return form instanceof RegisterForm;
        }));
    }

    @Test
    @DisplayName("Test processLogin() redirect paths are correct")
    void testProcessLoginRedirectPaths() {
        // Success case
        when(authService.authenticateUser(anyString(), anyString())).thenReturn("token");
        String successResult = authController.processLogin(loginForm, response, redirectAttributes);
        assertTrue(successResult.startsWith("redirect:"));
        assertEquals("redirect:/wishlist", successResult);

        // Reset untuk test failure
        reset(authService);

        // Failure case
        when(authService.authenticateUser(anyString(), anyString()))
            .thenThrow(new RuntimeException("Error"));
        String failureResult = authController.processLogin(loginForm, response, redirectAttributes);
        assertTrue(failureResult.startsWith("redirect:"));
        assertTrue(failureResult.contains("error=true"));
    }

    @Test
    @DisplayName("Test processRegister() redirect path is correct")
    void testProcessRegisterRedirectPath() {
        // Arrange
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        // Act
        String result = authController.processRegister(registerForm);

        // Assert
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/auth/login", result);
    }

    @Test
    @DisplayName("Test logout() redirect path is correct")
    void testLogoutRedirectPath() {
        // Act
        String result = authController.logout(response);

        // Assert
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/auth/login", result);
    }

    @Test
    @DisplayName("Test processLogin() with very long token")
    void testProcessLoginWithLongToken() {
        // Arrange
        String longToken = "a".repeat(500); // Very long token
        when(authService.authenticateUser(anyString(), anyString())).thenReturn(longToken);

        // Act
        String result = authController.processLogin(loginForm, response, redirectAttributes);

        // Assert
        assertEquals("redirect:/wishlist", result);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        assertEquals(longToken, cookieCaptor.getValue().getValue());
    }

    @Test
    @DisplayName("Test processRegister() maps all form fields correctly")
    void testProcessRegisterMapsAllFields() {
        // Arrange
        registerForm.setName("Test User");
        registerForm.setEmail("test@test.com");
        registerForm.setPassword("testpass");
        
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        // Act
        authController.processRegister(registerForm);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerNewUser(userCaptor.capture());
        
        User user = userCaptor.getValue();
        assertEquals("Test User", user.getName());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("testpass", user.getPassword());
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("Test processLogin() exception message in redirect attributes")
    void testProcessLoginExceptionMessageInRedirectAttributes() {
        // Arrange
        when(authService.authenticateUser(anyString(), anyString()))
            .thenThrow(new RuntimeException("Custom error message"));

        // Act
        authController.processLogin(loginForm, response, redirectAttributes);

        // Assert
        verify(redirectAttributes).addFlashAttribute("error", "Email atau Password salah!");
    }

    @Test
    @DisplayName("Test multiple login attempts with different credentials")
    void testMultipleLoginAttempts() {
        // First attempt - success
        when(authService.authenticateUser("user1@test.com", "pass1"))
            .thenReturn("token1");
        
        LoginForm form1 = new LoginForm();
        form1.setEmail("user1@test.com");
        form1.setPassword("pass1");
        
        String result1 = authController.processLogin(form1, response, redirectAttributes);
        assertEquals("redirect:/wishlist", result1);

        // Second attempt - failure
        when(authService.authenticateUser("user2@test.com", "wrongpass"))
            .thenThrow(new RuntimeException("Failed"));
        
        LoginForm form2 = new LoginForm();
        form2.setEmail("user2@test.com");
        form2.setPassword("wrongpass");
        
        String result2 = authController.processLogin(form2, response, redirectAttributes);
        assertEquals("redirect:/auth/login?error=true", result2);

        verify(authService).authenticateUser("user1@test.com", "pass1");
        verify(authService).authenticateUser("user2@test.com", "wrongpass");
    }

    @Test
    @DisplayName("Test processLogin() success path full flow")
    void testProcessLoginSuccessPathFullFlow() {
        // Arrange
        String token = "valid-token-xyz";
        when(authService.authenticateUser(loginForm.getEmail(), loginForm.getPassword()))
            .thenReturn(token);

        // Act
        String result = authController.processLogin(loginForm, response, redirectAttributes);

        // Assert - Verify semua step
        assertEquals("redirect:/wishlist", result);
        verify(authService).authenticateUser(loginForm.getEmail(), loginForm.getPassword());
        verify(response).addCookie(any(Cookie.class));
        verify(redirectAttributes, never()).addFlashAttribute(anyString(), anyString());
    }

    @Test
    @DisplayName("Test processLogin() failure path full flow")
    void testProcessLoginFailurePathFullFlow() {
        // Arrange
        when(authService.authenticateUser(anyString(), anyString()))
            .thenThrow(new RuntimeException("Authentication failed"));

        // Act
        String result = authController.processLogin(loginForm, response, redirectAttributes);

        // Assert - Verify semua step
        assertEquals("redirect:/auth/login?error=true", result);
        verify(authService).authenticateUser(loginForm.getEmail(), loginForm.getPassword());
        verify(redirectAttributes).addFlashAttribute("error", "Email atau Password salah!");
        verify(response, never()).addCookie(any(Cookie.class));
    }
}