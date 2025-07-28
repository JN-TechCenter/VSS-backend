package com.vision.vision_platform_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类单元测试
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private Long testExpiration;
    private String testUsername;
    private UUID testUserId;
    private String testRole;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testSecret = "test-secret-key-for-jwt-testing-this-key-must-be-at-least-512-bits-long-for-hs512-algorithm-security";
        testExpiration = 3600000L; // 1小时
        testUsername = "testuser";
        testUserId = UUID.randomUUID();
        testRole = "OBSERVER";

        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void testGenerateToken_Success() {
        // When
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void testGetUsernameFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        String username = jwtUtil.getUsernameFromToken(token);

        // Then
        assertEquals(testUsername, username);
    }

    @Test
    void testGetUserIdFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        UUID userId = jwtUtil.getUserIdFromToken(token);

        // Then
        assertEquals(testUserId, userId);
    }

    @Test
    void testGetUserRoleFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        String role = jwtUtil.getUserRoleFromToken(token);

        // Then
        assertEquals(testRole, role);
    }

    @Test
    void testGetExpirationDateFromToken_Success() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Then
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        Boolean isValid = jwtUtil.validateToken(token, testUsername);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_WrongUsername() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        Boolean isValid = jwtUtil.validateToken(token, "wronguser");

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Boolean isValid = jwtUtil.validateToken(invalidToken, testUsername);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testIsValidToken_ValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        Boolean isValid = jwtUtil.isValidToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testIsValidToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Boolean isValid = jwtUtil.isValidToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testIsValidToken_NullToken() {
        // When
        Boolean isValid = jwtUtil.isValidToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        String originalToken = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        String refreshedToken = jwtUtil.refreshToken(originalToken);

        // Then
        assertNotNull(refreshedToken);
        assertNotEquals(originalToken, refreshedToken);
        assertEquals(testUsername, jwtUtil.getUsernameFromToken(refreshedToken));
        assertEquals(testUserId, jwtUtil.getUserIdFromToken(refreshedToken));
        assertEquals(testRole, jwtUtil.getUserRoleFromToken(refreshedToken));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        String refreshedToken = jwtUtil.refreshToken(invalidToken);

        // Then
        assertNull(refreshedToken);
    }

    @Test
    void testGetTokenRemainingTime_ValidToken() {
        // Given
        String token = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(token);

        // Then
        assertNotNull(remainingTime);
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= testExpiration);
    }

    @Test
    void testGetTokenRemainingTime_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(invalidToken);

        // Then
        assertEquals(0L, remainingTime);
    }

    @Test
    void testGetTokenRemainingTime_ExpiredToken() {
        // Given
        // 创建一个已过期的token（设置很短的过期时间）
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String expiredToken = jwtUtil.generateToken(testUsername, testUserId, testRole);
        
        // 恢复正常的过期时间
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(expiredToken);

        // Then
        assertTrue(remainingTime <= 0);
    }

    @Test
    void testGetUsernameFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(JwtException.class, () -> {
            jwtUtil.getUsernameFromToken(invalidToken);
        });
    }

    @Test
    void testGetUserIdFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(JwtException.class, () -> {
            jwtUtil.getUserIdFromToken(invalidToken);
        });
    }

    @Test
    void testTokenWithSpecialCharacters() {
        // Given
        String specialUsername = "user@domain.com";
        String specialRole = "ADMIN_ROLE";

        // When
        String token = jwtUtil.generateToken(specialUsername, testUserId, specialRole);

        // Then
        assertNotNull(token);
        assertEquals(specialUsername, jwtUtil.getUsernameFromToken(token));
        assertEquals(specialRole, jwtUtil.getUserRoleFromToken(token));
        assertTrue(jwtUtil.validateToken(token, specialUsername));
    }

    @Test
    void testTokenConsistency() {
        // Given
        String token1 = jwtUtil.generateToken(testUsername, testUserId, testRole);
        String token2 = jwtUtil.generateToken(testUsername, testUserId, testRole);

        // Then
        // 两个token应该不同（因为时间戳不同）
        assertNotEquals(token1, token2);
        
        // 但是包含的信息应该相同
        assertEquals(jwtUtil.getUsernameFromToken(token1), jwtUtil.getUsernameFromToken(token2));
        assertEquals(jwtUtil.getUserIdFromToken(token1), jwtUtil.getUserIdFromToken(token2));
        assertEquals(jwtUtil.getUserRoleFromToken(token1), jwtUtil.getUserRoleFromToken(token2));
    }
}