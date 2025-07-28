package com.vision.vision_platform_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户实体类单元测试
 */
class UserTest {

    private User user;
    private UUID testId;

    @BeforeEach
    void setUp() {
        user = new User();
        testId = UUID.randomUUID();
        
        user.setId(testId);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");
        user.setDepartment("IT");
        user.setRole(User.UserRole.OBSERVER);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setLoginAttempts(0);
        user.setCreatedBy("admin");
        user.setUpdatedBy("admin");
    }

    @Test
    void testUserCreation() {
        // Then
        assertEquals(testId, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("IT", user.getDepartment());
        assertEquals(User.UserRole.OBSERVER, user.getRole());
        assertEquals(User.UserStatus.ACTIVE, user.getStatus());
        assertEquals(0, user.getLoginAttempts());
        assertEquals("admin", user.getCreatedBy());
        assertEquals("admin", user.getUpdatedBy());
    }

    @Test
    void testUserRoleEnum() {
        // Test enum values
        assertEquals("管理员", User.UserRole.ADMIN.getDisplayName());
        assertEquals("操作员", User.UserRole.OPERATOR.getDisplayName());
        assertEquals("观察者", User.UserRole.OBSERVER.getDisplayName());
        
        // Test setting different roles
        user.setRole(User.UserRole.ADMIN);
        assertEquals(User.UserRole.ADMIN, user.getRole());
        
        user.setRole(User.UserRole.OPERATOR);
        assertEquals(User.UserRole.OPERATOR, user.getRole());
    }

    @Test
    void testUserStatusEnum() {
        // Test enum values
        assertEquals("启用", User.UserStatus.ACTIVE.getDisplayName());
        assertEquals("禁用", User.UserStatus.DISABLED.getDisplayName());
        assertEquals("锁定", User.UserStatus.LOCKED.getDisplayName());
        
        // Test setting different statuses
        user.setStatus(User.UserStatus.DISABLED);
        assertEquals(User.UserStatus.DISABLED, user.getStatus());
        
        user.setStatus(User.UserStatus.LOCKED);
        assertEquals(User.UserStatus.LOCKED, user.getStatus());
    }

    @Test
    void testCheckAccountLocked_NotLocked() {
        // Given
        user.setAccountLockedUntil(null);

        // When
        boolean isLocked = user.checkAccountLocked();

        // Then
        assertFalse(isLocked);
    }

    @Test
    void testCheckAccountLocked_LockedInFuture() {
        // Given
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));

        // When
        boolean isLocked = user.checkAccountLocked();

        // Then
        assertTrue(isLocked);
    }

    @Test
    void testCheckAccountLocked_LockExpired() {
        // Given
        user.setAccountLockedUntil(LocalDateTime.now().minusMinutes(10));

        // When
        boolean isLocked = user.checkAccountLocked();

        // Then
        assertFalse(isLocked);
    }

    @Test
    void testCheckEnabled_ActiveAndNotLocked() {
        // Given
        user.setStatus(User.UserStatus.ACTIVE);
        user.setAccountLockedUntil(null);

        // When
        boolean isEnabled = user.checkEnabled();

        // Then
        assertTrue(isEnabled);
    }

    @Test
    void testCheckEnabled_DisabledStatus() {
        // Given
        user.setStatus(User.UserStatus.DISABLED);
        user.setAccountLockedUntil(null);

        // When
        boolean isEnabled = user.checkEnabled();

        // Then
        assertFalse(isEnabled);
    }

    @Test
    void testCheckEnabled_ActiveButLocked() {
        // Given
        user.setStatus(User.UserStatus.ACTIVE);
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));

        // When
        boolean isEnabled = user.checkEnabled();

        // Then
        assertFalse(isEnabled);
    }

    @Test
    void testResetLoginAttempts() {
        // Given
        user.setLoginAttempts(5);
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));

        // When
        user.resetLoginAttempts();

        // Then
        assertEquals(0, user.getLoginAttempts());
        assertNull(user.getAccountLockedUntil());
    }

    @Test
    void testIncrementLoginAttempts_BelowThreshold() {
        // Given
        user.setLoginAttempts(2);

        // When
        user.incrementLoginAttempts();

        // Then
        assertEquals(3, user.getLoginAttempts());
        assertNull(user.getAccountLockedUntil());
    }

    @Test
    void testIncrementLoginAttempts_ReachThreshold() {
        // Given
        user.setLoginAttempts(4);

        // When
        user.incrementLoginAttempts();

        // Then
        assertEquals(5, user.getLoginAttempts());
        assertNotNull(user.getAccountLockedUntil());
        assertTrue(user.getAccountLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void testIncrementLoginAttempts_ExceedThreshold() {
        // Given
        user.setLoginAttempts(5);

        // When
        user.incrementLoginAttempts();

        // Then
        assertEquals(6, user.getLoginAttempts());
        assertNotNull(user.getAccountLockedUntil());
        assertTrue(user.getAccountLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void testSettersAndGetters() {
        // Test all setters and getters
        UUID newId = UUID.randomUUID();
        user.setId(newId);
        assertEquals(newId, user.getId());

        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());

        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());

        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());

        user.setFullName("New User");
        assertEquals("New User", user.getFullName());

        user.setPhoneNumber("9876543210");
        assertEquals("9876543210", user.getPhoneNumber());

        user.setDepartment("HR");
        assertEquals("HR", user.getDepartment());

        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginTime(now);
        assertEquals(now, user.getLastLoginTime());

        user.setLoginAttempts(3);
        assertEquals(3, user.getLoginAttempts());

        LocalDateTime lockTime = LocalDateTime.now().plusMinutes(30);
        user.setAccountLockedUntil(lockTime);
        assertEquals(lockTime, user.getAccountLockedUntil());

        LocalDateTime createdAt = LocalDateTime.now();
        user.setCreatedAt(createdAt);
        assertEquals(createdAt, user.getCreatedAt());

        LocalDateTime updatedAt = LocalDateTime.now();
        user.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, user.getUpdatedAt());

        user.setCreatedBy("creator");
        assertEquals("creator", user.getCreatedBy());

        user.setUpdatedBy("updater");
        assertEquals("updater", user.getUpdatedBy());
    }

    @Test
    void testDefaultValues() {
        // Given
        User newUser = new User();

        // Then
        assertEquals(User.UserRole.OBSERVER, newUser.getRole());
        assertEquals(User.UserStatus.ACTIVE, newUser.getStatus());
        assertEquals(0, newUser.getLoginAttempts());
    }

    @Test
    void testAccountLockingScenario() {
        // Given
        User newUser = new User();
        newUser.setStatus(User.UserStatus.ACTIVE);

        // Initially enabled
        assertTrue(newUser.checkEnabled());

        // After 4 failed attempts, still enabled
        for (int i = 0; i < 4; i++) {
            newUser.incrementLoginAttempts();
        }
        assertTrue(newUser.checkEnabled());

        // After 5th failed attempt, account gets locked
        newUser.incrementLoginAttempts();
        assertFalse(newUser.checkEnabled());
        assertTrue(newUser.checkAccountLocked());

        // Reset attempts unlocks the account
        newUser.resetLoginAttempts();
        assertTrue(newUser.checkEnabled());
        assertFalse(newUser.checkAccountLocked());
    }
}