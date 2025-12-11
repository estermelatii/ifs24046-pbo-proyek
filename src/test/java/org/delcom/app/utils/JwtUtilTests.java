package org.delcom.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "mySecretKeyForJWTTokenGenerationAndValidation12345678901234567890";
    private final Long testExpiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void testGenerateTokenWithUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testGenerateTokenWithUUID() {
        UUID userId = UUID.randomUUID();
        String token = jwtUtil.generateToken(userId);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(userId.toString(), jwtUtil.extractUserId(token));
    }

    @Test
    void testGenerateTokenWithStringUserIdAndUsername() {
        String userId = UUID.randomUUID().toString();
        String username = "testuser";
        String token = jwtUtil.generateToken(userId, username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(userId, jwtUtil.extractUserId(token));
    }

    @Test
    void testGenerateTokenWithUUIDAndUsername() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String token = jwtUtil.generateToken(userId, username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(userId.toString(), jwtUtil.extractUserId(token));
    }

    @Test
    void testExtractUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUserId() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String token = jwtUtil.generateToken(userId, username);
        
        String extractedUserId = jwtUtil.extractUserId(token);
        assertEquals(userId.toString(), extractedUserId);
    }

    @Test
    void testExtractExpiration() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractClaim() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        String subject = jwtUtil.extractClaim(token, Claims::getSubject);
        assertEquals(username, subject);
    }

    @Test
    void testValidateTokenWithValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, username);
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithInvalidUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, "wronguser");
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithExpiredToken() {
        // Create expired token
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1000L);
        
        String username = "testuser";
        String expiredToken = expiredJwtUtil.generateToken(username);
        
        // Wait a bit to ensure token is expired
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // ExpiredJwtException will be thrown when trying to parse expired token
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateToken(expiredToken, username);
        });
    }

    @Test
    void testValidateTokenWithCheckExpirationTrue() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, username, true);
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithCheckExpirationFalse() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, username, false);
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithCheckExpirationFalseAndExpiredToken() {
        // Create expired token
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1000L);
        
        String username = "testuser";
        String expiredToken = expiredJwtUtil.generateToken(username);
        
        // Wait a bit to ensure token is expired
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // ExpiredJwtException will be thrown when trying to parse expired token
        // Even with checkExpiration=false, parsing still throws exception
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateToken(expiredToken, username, false);
        });
    }

    @Test
    void testValidateTokenWithCheckExpirationTrueAndExpiredToken() {
        // Create expired token
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1000L);
        
        String username = "testuser";
        String expiredToken = expiredJwtUtil.generateToken(username);
        
        // Wait a bit to ensure token is expired
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // ExpiredJwtException will be thrown when trying to parse expired token
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateToken(expiredToken, username, true);
        });
    }

    @Test
    void testValidateTokenWithWrongUsernameAndCheckExpirationFalse() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, "wronguser", false);
        assertFalse(isValid);
    }

    @Test
    void testTokenContainsIssuedAtDate() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()) || issuedAt.equals(new Date()));
    }

    @Test
    void testTokenExpirationIsCorrect() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtUtil.extractExpiration(token);
        
        long difference = expiration.getTime() - issuedAt.getTime();
        assertEquals(testExpiration, difference);
    }

    @Test
    void testExtractUserIdReturnsNullWhenNotPresent() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        String userId = jwtUtil.extractUserId(token);
        assertNull(userId);
    }

    @Test
    void testMultipleTokenGeneration() {
        String username1 = "user1";
        String username2 = "user2";
        
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);
        
        assertNotEquals(token1, token2);
        assertEquals(username1, jwtUtil.extractUsername(token1));
        assertEquals(username2, jwtUtil.extractUsername(token2));
    }

    @Test
    void testValidateTokenWithValidUsernameButDifferentCase() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        // Test with different case - should be invalid
        Boolean isValid = jwtUtil.validateToken(token, "TestUser");
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithEmptyUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, "");
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithNullUsernameComparison() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        // The method will return false when comparing with null
        // because extractedUsername.equals(null) returns false
        Boolean isValid = jwtUtil.validateToken(token, null);
        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpiredWithValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        // Token should be valid (not expired)
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractAllClaimsComponents() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String token = jwtUtil.generateToken(userId, username);
        
        // Extract multiple claims
        String extractedUsername = jwtUtil.extractUsername(token);
        String extractedUserId = jwtUtil.extractUserId(token);
        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtUtil.extractExpiration(token);
        
        assertEquals(username, extractedUsername);
        assertEquals(userId.toString(), extractedUserId);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));
    }

    @Test
    void testValidateTokenBothMethodVariants() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        // Test 2-parameter method
        Boolean isValid1 = jwtUtil.validateToken(token, username);
        assertTrue(isValid1);
        
        // Test 3-parameter method with true
        Boolean isValid2 = jwtUtil.validateToken(token, username, true);
        assertTrue(isValid2);
        
        // Test 3-parameter method with false
        Boolean isValid3 = jwtUtil.validateToken(token, username, false);
        assertTrue(isValid3);
        
        // All should return the same result for valid token
        assertEquals(isValid1, isValid2);
        assertEquals(isValid1, isValid3);
    }

    @Test
    void testCreateTokenWithEmptyClaims() {
        String username = "testuser";
        Map<String, Object> emptyClaims = new HashMap<>();
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testTokenSignatureIntegrity() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        // Modify token slightly to break signature
        String[] parts = token.split("\\.");
        if (parts.length == 3) {
            String tamperedToken = parts[0] + "." + parts[1] + ".tampered";
            
            assertThrows(Exception.class, () -> {
                jwtUtil.extractUsername(tamperedToken);
            });
        }
    }

    @Test
    void testGenerateTokenWithSpecialCharactersInUsername() {
        String username = "test@user.com";
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertEquals(username, jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testGenerateTokenWithLongUsername() {
        String username = "a".repeat(100);
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testValidateTokenReturnsFalseForWrongUsername() {
        String username = "testuser";
        String wrongUsername = "wronguser";
        String token = jwtUtil.generateToken(username);
        
        Boolean isValid = jwtUtil.validateToken(token, wrongUsername);
        assertFalse(isValid);
        
        // Also test with checkExpiration parameter
        Boolean isValid2 = jwtUtil.validateToken(token, wrongUsername, false);
        assertFalse(isValid2);
        
        Boolean isValid3 = jwtUtil.validateToken(token, wrongUsername, true);
        assertFalse(isValid3);
    }
}