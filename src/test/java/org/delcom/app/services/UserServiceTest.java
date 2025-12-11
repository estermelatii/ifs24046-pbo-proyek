package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");
    }

    // ===== TEST REGISTER NEW USER =====
    
    @Test
    void testRegisterNewUser() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.registerNewUser(testUser);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testRegisterUser() {
        // Arrange - Alias method
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.registerUser(testUser);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(testUser);
    }

    // ===== TEST UPDATE USER (String ID) =====

    @Test
    void testUpdateUserWithStringId_Success() {
        // Arrange
        User updatedData = new User();
        updatedData.setName("Updated Name");
        updatedData.setEmail("updated@example.com");
        updatedData.setPassword("newPassword");
        updatedData.setRole("ADMIN");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(testUserId.toString(), updatedData);

        // Assert
        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserWithStringId_UserNotFound() {
        // Arrange - Branch: existingUser == null
        User updatedData = new User();
        updatedData.setName("Updated Name");
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act
        userService.updateUser(testUserId.toString(), updatedData);

        // Assert
        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserWithStringId_InvalidUUID() {
        // Arrange - Branch: catch IllegalArgumentException
        User updatedData = new User();
        updatedData.setName("Updated Name");

        // Act
        userService.updateUser("invalid-uuid-format", updatedData);

        // Assert
        verify(userRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).save(any(User.class));
    }

    // ===== TEST UPDATE USER (User, User) =====

    @Test
    void testUpdateUser_WithPasswordAndRole() {
        // Arrange - Branch: password != null && !isEmpty() && role != null
        User updatedData = new User();
        updatedData.setName("Updated Name");
        updatedData.setEmail("updated@example.com");
        updatedData.setPassword("newPassword123");
        updatedData.setRole("ADMIN");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(testUser, updatedData);

        // Assert
        assertEquals("Updated Name", testUser.getName());
        assertEquals("updated@example.com", testUser.getEmail());
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_WithoutPassword() {
        // Arrange - Branch: password == null
        User updatedData = new User();
        updatedData.setName("Updated Name");
        updatedData.setEmail("updated@example.com");
        updatedData.setPassword(null);
        updatedData.setRole("ADMIN");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(testUser, updatedData);

        // Assert
        assertEquals("Updated Name", testUser.getName());
        assertEquals("updated@example.com", testUser.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_WithEmptyPassword() {
        // Arrange - Branch: password.isEmpty()
        User updatedData = new User();
        updatedData.setName("Updated Name");
        updatedData.setEmail("updated@example.com");
        updatedData.setPassword("");
        updatedData.setRole("ADMIN");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(testUser, updatedData);

        // Assert
        assertEquals("Updated Name", testUser.getName());
        assertEquals("updated@example.com", testUser.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_WithoutRole() {
        // Arrange - Branch: role == null
        User updatedData = new User();
        updatedData.setName("Updated Name");
        updatedData.setEmail("updated@example.com");
        updatedData.setPassword("newPassword");
        updatedData.setRole(null);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String originalRole = testUser.getRole();

        // Act
        userService.updateUser(testUser, updatedData);

        // Assert
        assertEquals("Updated Name", testUser.getName());
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals(originalRole, testUser.getRole()); // Role tidak berubah
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_OnlyNameAndEmail() {
        // Arrange - Branch: password == null && role == null
        User updatedData = new User();
        updatedData.setName("Updated Name");
        updatedData.setEmail("updated@example.com");
        updatedData.setPassword(null);
        updatedData.setRole(null);

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String originalPassword = testUser.getPassword();
        String originalRole = testUser.getRole();

        // Act
        userService.updateUser(testUser, updatedData);

        // Assert
        assertEquals("Updated Name", testUser.getName());
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals(originalPassword, testUser.getPassword()); // Password tidak berubah
        assertEquals(originalRole, testUser.getRole()); // Role tidak berubah
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(testUser);
    }

    // ===== TEST FIND BY EMAIL =====

    @Test
    void testFindByEmail_Found() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act
        User result = userService.findByEmail("notfound@example.com");

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    // ===== TEST FIND BY ID =====

    @Test
    void testFindById_Found() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findById(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        // Act
        User result = userService.findById(randomId);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(randomId);
    }
}