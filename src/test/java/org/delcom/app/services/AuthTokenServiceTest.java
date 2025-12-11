package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    private AuthToken testToken;

    @BeforeEach
    void setUp() {
        testToken = new AuthToken();
        testToken.setId(UUID.randomUUID());
        testToken.setToken("test-token-12345");
    }

    @Test
    void testSaveToken() {
        // Arrange
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(testToken);

        // Act
        authTokenService.saveToken(testToken);

        // Assert
        verify(authTokenRepository, times(1)).save(testToken);
    }

    @Test
    void testSaveTokenWithNullToken() {
        // Arrange
        AuthToken nullToken = null;

        // Act
        authTokenService.saveToken(nullToken);

        // Assert
        verify(authTokenRepository, times(1)).save(nullToken);
    }

    @Test
    void testSaveTokenWithNewToken() {
        // Arrange
        AuthToken newToken = new AuthToken();
        newToken.setToken("new-token-67890");
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(newToken);

        // Act
        authTokenService.saveToken(newToken);

        // Assert
        verify(authTokenRepository, times(1)).save(newToken);
    }

    @Test
    void testRemoveTokenByToken() {
        // Arrange
        String tokenString = "test-token-12345";
        doNothing().when(authTokenRepository).deleteByToken(anyString());

        // Act
        authTokenService.removeTokenByToken(tokenString);

        // Assert
        verify(authTokenRepository, times(1)).deleteByToken(tokenString);
    }

    @Test
    void testRemoveTokenByTokenWithEmptyString() {
        // Arrange
        String emptyToken = "";
        doNothing().when(authTokenRepository).deleteByToken(anyString());

        // Act
        authTokenService.removeTokenByToken(emptyToken);

        // Assert
        verify(authTokenRepository, times(1)).deleteByToken(emptyToken);
    }

    @Test
    void testRemoveTokenByTokenWithNullString() {
        // Arrange
        String nullToken = null;
        doNothing().when(authTokenRepository).deleteByToken(any());

        // Act
        authTokenService.removeTokenByToken(nullToken);

        // Assert
        verify(authTokenRepository, times(1)).deleteByToken(nullToken);
    }

    @Test
    void testIsTokenValidWhenTokenExists() {
        // Arrange
        String tokenString = "test-token-12345";
        when(authTokenRepository.findByToken(tokenString)).thenReturn(testToken);

        // Act
        boolean result = authTokenService.isTokenValid(tokenString);

        // Assert
        assertTrue(result);
        verify(authTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    void testIsTokenValidWhenTokenDoesNotExist() {
        // Arrange
        String tokenString = "non-existent-token";
        when(authTokenRepository.findByToken(tokenString)).thenReturn(null);

        // Act
        boolean result = authTokenService.isTokenValid(tokenString);

        // Assert
        assertFalse(result);
        verify(authTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    void testIsTokenValidWithEmptyString() {
        // Arrange
        String emptyToken = "";
        when(authTokenRepository.findByToken(emptyToken)).thenReturn(null);

        // Act
        boolean result = authTokenService.isTokenValid(emptyToken);

        // Assert
        assertFalse(result);
        verify(authTokenRepository, times(1)).findByToken(emptyToken);
    }

    @Test
    void testIsTokenValidWithNullString() {
        // Arrange
        String nullToken = null;
        when(authTokenRepository.findByToken(nullToken)).thenReturn(null);

        // Act
        boolean result = authTokenService.isTokenValid(nullToken);

        // Assert
        assertFalse(result);
        verify(authTokenRepository, times(1)).findByToken(nullToken);
    }

    @Test
    void testSaveTokenMultipleTimes() {
        // Arrange
        AuthToken token1 = new AuthToken();
        token1.setToken("token-1");
        AuthToken token2 = new AuthToken();
        token2.setToken("token-2");
        
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(token1, token2);

        // Act
        authTokenService.saveToken(token1);
        authTokenService.saveToken(token2);

        // Assert
        verify(authTokenRepository, times(2)).save(any(AuthToken.class));
    }

    @Test
    void testRemoveTokenByTokenMultipleTimes() {
        // Arrange
        doNothing().when(authTokenRepository).deleteByToken(anyString());

        // Act
        authTokenService.removeTokenByToken("token-1");
        authTokenService.removeTokenByToken("token-2");
        authTokenService.removeTokenByToken("token-3");

        // Assert
        verify(authTokenRepository, times(3)).deleteByToken(anyString());
    }

    @Test
    void testIsTokenValidMultipleCalls() {
        // Arrange
        when(authTokenRepository.findByToken("valid-token")).thenReturn(testToken);
        when(authTokenRepository.findByToken("invalid-token")).thenReturn(null);

        // Act
        boolean result1 = authTokenService.isTokenValid("valid-token");
        boolean result2 = authTokenService.isTokenValid("invalid-token");
        boolean result3 = authTokenService.isTokenValid("valid-token");

        // Assert
        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
        verify(authTokenRepository, times(2)).findByToken("valid-token");
        verify(authTokenRepository, times(1)).findByToken("invalid-token");
    }

    @Test
    void testSaveTokenWithDifferentId() {
        // Arrange
        AuthToken tokenWithId = new AuthToken();
        tokenWithId.setId(UUID.randomUUID());
        tokenWithId.setToken("token-with-id");
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(tokenWithId);

        // Act
        authTokenService.saveToken(tokenWithId);

        // Assert
        verify(authTokenRepository, times(1)).save(tokenWithId);
    }

    @Test
    void testRemoveTokenByTokenWithSpecialCharacters() {
        // Arrange
        String specialToken = "token!@#$%^&*()";
        doNothing().when(authTokenRepository).deleteByToken(anyString());

        // Act
        authTokenService.removeTokenByToken(specialToken);

        // Assert
        verify(authTokenRepository, times(1)).deleteByToken(specialToken);
    }

    @Test
    void testIsTokenValidWithSpecialCharacters() {
        // Arrange
        String specialToken = "token!@#$%^&*()";
        when(authTokenRepository.findByToken(specialToken)).thenReturn(testToken);

        // Act
        boolean result = authTokenService.isTokenValid(specialToken);

        // Assert
        assertTrue(result);
        verify(authTokenRepository, times(1)).findByToken(specialToken);
    }

    @Test
    void testSaveTokenVerifyRepositoryInteraction() {
        // Arrange
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(testToken);

        // Act
        authTokenService.saveToken(testToken);

        // Assert
        verify(authTokenRepository).save(testToken);
        verifyNoMoreInteractions(authTokenRepository);
    }

    @Test
    void testRemoveTokenByTokenVerifyRepositoryInteraction() {
        // Arrange
        String tokenString = "test-token";
        doNothing().when(authTokenRepository).deleteByToken(anyString());

        // Act
        authTokenService.removeTokenByToken(tokenString);

        // Assert
        verify(authTokenRepository).deleteByToken(tokenString);
        verifyNoMoreInteractions(authTokenRepository);
    }

    @Test
    void testIsTokenValidVerifyRepositoryInteraction() {
        // Arrange
        String tokenString = "test-token";
        when(authTokenRepository.findByToken(tokenString)).thenReturn(testToken);

        // Act
        authTokenService.isTokenValid(tokenString);

        // Assert
        verify(authTokenRepository).findByToken(tokenString);
        verifyNoMoreInteractions(authTokenRepository);
    }

    @Test
    void testSaveTokenWithLongToken() {
        // Arrange
        AuthToken longToken = new AuthToken();
        longToken.setToken("very-long-token-" + "x".repeat(100));
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(longToken);

        // Act
        authTokenService.saveToken(longToken);

        // Assert
        verify(authTokenRepository, times(1)).save(longToken);
    }

    @Test
    void testIsTokenValidReturnsTrueForValidToken() {
        // Arrange
        String validToken = "valid-token-123";
        when(authTokenRepository.findByToken(validToken)).thenReturn(testToken);

        // Act
        boolean result = authTokenService.isTokenValid(validToken);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTokenValidReturnsFalseForInvalidToken() {
        // Arrange
        String invalidToken = "invalid-token-456";
        when(authTokenRepository.findByToken(invalidToken)).thenReturn(null);

        // Act
        boolean result = authTokenService.isTokenValid(invalidToken);

        // Assert
        assertFalse(result);
    }
}