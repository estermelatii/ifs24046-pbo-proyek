package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.AuthTokenRepository;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
    }

    @Test
    void testAuthenticateUserSuccess() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String token = "generated-jwt-token";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(new AuthToken());

        // Act
        String result = authService.authenticateUser(email, password);

        // Assert
        assertEquals(token, result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, testUser.getPassword());
        verify(jwtUtil, times(1)).generateToken(email);
        verify(authTokenRepository, times(1)).save(any(AuthToken.class));
    }

    @Test
    void testAuthenticateUserUserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(email, password);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
        verify(authTokenRepository, never()).save(any(AuthToken.class));
    }

    @Test
    void testAuthenticateUserInvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(email, password);
        });

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, testUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
        verify(authTokenRepository, never()).save(any(AuthToken.class));
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = authService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(email);
        });

        assertTrue(exception.getMessage().contains("User not found with email: " + email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetCurrentUserWithUserDetails() {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = mock(UserDetails.class);
        
        when(userDetails.getUsername()).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        User result = authService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetCurrentUserWithStringPrincipal() {
        // Arrange
        String email = "test@example.com";
        
        when(authentication.getPrincipal()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        User result = authService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetCurrentUserAnonymous() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        User result = authService.getCurrentUser();

        // Assert
        assertNull(result);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void testGetCurrentUserNotFoundInDatabase() {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = mock(UserDetails.class);
        
        when(userDetails.getUsername()).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        User result = authService.getCurrentUser();

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testAuthenticateUserWithDifferentEmail() {
        // Arrange
        String email = "another@example.com";
        String password = "password456";
        String token = "another-jwt-token";

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setEmail(email);
        anotherUser.setPassword("encodedPassword456");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(anotherUser));
        when(passwordEncoder.matches(password, anotherUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(new AuthToken());

        // Act
        String result = authService.authenticateUser(email, password);

        // Assert
        assertEquals(token, result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, anotherUser.getPassword());
    }

    @Test
    void testLoadUserByUsernameWithDifferentEmail() {
        // Arrange
        String email = "different@example.com";
        User differentUser = new User();
        differentUser.setEmail(email);
        differentUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(differentUser));

        // Act
        UserDetails userDetails = authService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void testAuthenticateUserSavesTokenCorrectly() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String token = "jwt-token-12345";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> {
            AuthToken savedToken = invocation.getArgument(0);
            assertEquals(token, savedToken.getToken());
            assertEquals(testUser.getId(), savedToken.getUserId());
            assertNotNull(savedToken.getCreatedAt());
            return savedToken;
        });

        // Act
        authService.authenticateUser(email, password);

        // Assert
        verify(authTokenRepository, times(1)).save(any(AuthToken.class));
    }

    @Test
    void testGetCurrentUserWithNullEmail() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            authService.getCurrentUser();
        });
    }

    @Test
    void testAuthenticateUserWithEmptyPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(email, password);
        });
    }

    @Test
    void testLoadUserByUsernameReturnsCorrectAuthorities() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = authService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails.getAuthorities());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }
}