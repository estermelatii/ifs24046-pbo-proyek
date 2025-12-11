package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTests {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private User updatedUser;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");

        // Setup updated user
        updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setPassword("newPassword456");
    }

    // ========== TESTS FOR MISSED METHOD (RED - updateProfile) ==========

    @Test
    @DisplayName("Test updateProfile() with authenticated user - should update and redirect")
    void testUpdateProfileSuccess() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(userService).updateUser(anyString(), any(User.class));

        // Act
        String result = userController.updateProfile(updatedUser);

        // Assert
        assertEquals("redirect:/user/profile?success", result);
        verify(authService).getCurrentUser();
        verify(userService).updateUser(testUser.getId().toString(), updatedUser);
    }

    @Test
    @DisplayName("Test updateProfile() with no authenticated user - should redirect to login")
    void testUpdateProfileNoUser() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = userController.updateProfile(updatedUser);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(userService, never()).updateUser(anyString(), any());
    }

    @Test
    @DisplayName("Test updateProfile() calls updateUser with correct parameters")
    void testUpdateProfileCallsServiceWithCorrectParams() {
        // Arrange
        UUID userId = UUID.randomUUID();
        testUser.setId(userId);
        
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(userService).updateUser(anyString(), any(User.class));

        // Act
        String result = userController.updateProfile(updatedUser);

        // Assert
        assertEquals("redirect:/user/profile?success", result);
        verify(userService).updateUser(userId.toString(), updatedUser);
    }

    // ========== TESTS FOR COVERED METHODS (GREEN) ==========

    @Test
    @DisplayName("Test showProfile() with authenticated user - should return profile view")
    void testShowProfileSuccess() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);

        // Act
        String result = userController.showProfile(model);

        // Assert
        assertEquals("user/profile", result);
        verify(authService).getCurrentUser();
        verify(model).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Test showProfile() with no authenticated user - should redirect to login")
    void testShowProfileNoUser() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = userController.showProfile(model);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("Test UserController() constructor")
    void testUserControllerConstructor() {
        // Act
        UserController controller = new UserController();

        // Assert
        assertNotNull(controller);
    }

    // ========== ADDITIONAL EDGE CASE & INTEGRATION TESTS ==========

    @Test
    @DisplayName("Test showProfile() adds correct user to model")
    void testShowProfileAddsCorrectUser() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);

        // Act
        String result = userController.showProfile(model);

        // Assert
        assertEquals("user/profile", result);
        verify(model).addAttribute(eq("user"), argThat(user -> {
            User u = (User) user;
            return u.getName().equals("John Doe") &&
                   u.getEmail().equals("john@example.com");
        }));
    }

    @Test
    @DisplayName("Test updateProfile() with null updatedUser - should still process")
    void testUpdateProfileWithNullUpdatedUser() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(userService).updateUser(anyString(), any());

        // Act
        String result = userController.updateProfile(null);

        // Assert
        assertEquals("redirect:/user/profile?success", result);
        verify(authService).getCurrentUser();
        verify(userService).updateUser(testUser.getId().toString(), null);
    }

    @Test
    @DisplayName("Test updateProfile() redirect includes success parameter")
    void testUpdateProfileRedirectIncludesSuccessParam() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(userService).updateUser(anyString(), any(User.class));

        // Act
        String result = userController.updateProfile(updatedUser);

        // Assert
        assertTrue(result.contains("?success"));
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/user/profile?success", result);
    }

    @Test
    @DisplayName("Test showProfile() returns correct view name")
    void testShowProfileReturnsCorrectViewName() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);

        // Act
        String result = userController.showProfile(model);

        // Assert
        assertTrue(result.contains("user/"));
        assertTrue(result.contains("profile"));
        assertEquals("user/profile", result);
    }

    @Test
    @DisplayName("Test updateProfile() with different user IDs")
    void testUpdateProfileWithDifferentUserIds() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        
        testUser.setId(userId1);
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(userService).updateUser(anyString(), any(User.class));

        // Act - First call
        String result1 = userController.updateProfile(updatedUser);
        
        // Change user ID
        testUser.setId(userId2);
        
        // Act - Second call
        String result2 = userController.updateProfile(updatedUser);

        // Assert
        assertEquals("redirect:/user/profile?success", result1);
        assertEquals("redirect:/user/profile?success", result2);
        verify(userService).updateUser(userId1.toString(), updatedUser);
        verify(userService).updateUser(userId2.toString(), updatedUser);
    }

    @Test
    @DisplayName("Test showProfile() when called multiple times with same user")
    void testShowProfileMultipleCalls() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);

        // Act
        String result1 = userController.showProfile(model);
        String result2 = userController.showProfile(model);
        String result3 = userController.showProfile(model);

        // Assert
        assertEquals("user/profile", result1);
        assertEquals("user/profile", result2);
        assertEquals("user/profile", result3);
        verify(authService, times(3)).getCurrentUser();
        verify(model, times(3)).addAttribute("user", testUser);
    }

    @Test
    @DisplayName("Test updateProfile() preserves user ID during update")
    void testUpdateProfilePreservesUserId() {
        // Arrange
        UUID originalUserId = testUser.getId();
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(userService).updateUser(anyString(), any(User.class));

        // Act
        userController.updateProfile(updatedUser);

        // Assert
        verify(userService).updateUser(eq(originalUserId.toString()), any(User.class));
        assertEquals(originalUserId, testUser.getId()); // ID should not change
    }

    @Test
    @DisplayName("Test updateProfile() handles service exception gracefully")
    void testUpdateProfileWithServiceException() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doThrow(new RuntimeException("Database error"))
            .when(userService).updateUser(anyString(), any(User.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userController.updateProfile(updatedUser);
        });
        verify(authService).getCurrentUser();
        verify(userService).updateUser(testUser.getId().toString(), updatedUser);
    }

    @Test
    @DisplayName("Test showProfile() with user having different roles")
    void testShowProfileWithDifferentRoles() {
        // Test with USER role
        testUser.setRole("USER");
        when(authService.getCurrentUser()).thenReturn(testUser);
        
        String result1 = userController.showProfile(model);
        assertEquals("user/profile", result1);

        // Test with ADMIN role
        testUser.setRole("ADMIN");
        String result2 = userController.showProfile(model);
        assertEquals("user/profile", result2);

        // Test with MODERATOR role
        testUser.setRole("MODERATOR");
        String result3 = userController.showProfile(model);
        assertEquals("user/profile", result3);
    }

    @Test
    @DisplayName("Test updateProfile() updates are reflected correctly")
    void testUpdateProfileReflectsChanges() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        
        // Capture the arguments passed to updateUser
        doAnswer(invocation -> {
            String userId = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            
            // Verify correct parameters
            assertEquals(testUser.getId().toString(), userId);
            assertEquals("John Updated", user.getName());
            assertEquals("john.updated@example.com", user.getEmail());
            return null;
        }).when(userService).updateUser(anyString(), any(User.class));

        // Act
        String result = userController.updateProfile(updatedUser);

        // Assert
        assertEquals("redirect:/user/profile?success", result);
        verify(userService).updateUser(testUser.getId().toString(), updatedUser);
    }
}