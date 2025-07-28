package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户仓库单元测试
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // 创建测试用户1
        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setPassword("password123");
        testUser1.setEmail("test1@example.com");
        testUser1.setFullName("Test User One");
        testUser1.setPhoneNumber("1234567890");
        testUser1.setDepartment("IT");
        testUser1.setRole(User.UserRole.ADMIN);
        testUser1.setStatus(User.UserStatus.ACTIVE);
        testUser1.setLoginAttempts(0);
        testUser1.setCreatedBy("admin");

        // 创建测试用户2
        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password456");
        testUser2.setEmail("test2@example.com");
        testUser2.setFullName("Test User Two");
        testUser2.setPhoneNumber("9876543210");
        testUser2.setDepartment("HR");
        testUser2.setRole(User.UserRole.OPERATOR);
        testUser2.setStatus(User.UserStatus.DISABLED);
        testUser2.setLoginAttempts(0);
        testUser2.setCreatedBy("admin");

        // 创建测试用户3（被锁定）
        testUser3 = new User();
        testUser3.setUsername("testuser3");
        testUser3.setPassword("password789");
        testUser3.setEmail("test3@example.com");
        testUser3.setFullName("Test User Three");
        testUser3.setPhoneNumber("5555555555");
        testUser3.setDepartment("IT");
        testUser3.setRole(User.UserRole.OBSERVER);
        testUser3.setStatus(User.UserStatus.ACTIVE);
        testUser3.setLoginAttempts(5);
        testUser3.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
        testUser3.setCreatedBy("admin");

        // 保存测试数据
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);
    }

    @Test
    void testFindByUsername_Success() {
        // When
        Optional<User> result = userRepository.findByUsername("testuser1");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser1", result.get().getUsername());
        assertEquals("test1@example.com", result.get().getEmail());
    }

    @Test
    void testFindByUsername_NotFound() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail_Success() {
        // When
        Optional<User> result = userRepository.findByEmail("test2@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser2", result.get().getUsername());
        assertEquals("test2@example.com", result.get().getEmail());
    }

    @Test
    void testExistsByUsername_True() {
        // When
        boolean exists = userRepository.existsByUsername("testuser1");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_False() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_True() {
        // When
        boolean exists = userRepository.existsByEmail("test1@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testFindByRole() {
        // When
        List<User> adminUsers = userRepository.findByRole(User.UserRole.ADMIN);
        List<User> operatorUsers = userRepository.findByRole(User.UserRole.OPERATOR);
        List<User> observerUsers = userRepository.findByRole(User.UserRole.OBSERVER);

        // Then
        assertEquals(1, adminUsers.size());
        assertEquals("testuser1", adminUsers.get(0).getUsername());

        assertEquals(1, operatorUsers.size());
        assertEquals("testuser2", operatorUsers.get(0).getUsername());

        assertEquals(1, observerUsers.size());
        assertEquals("testuser3", observerUsers.get(0).getUsername());
    }

    @Test
    void testFindByDepartment() {
        // When
        List<User> itUsers = userRepository.findByDepartment("IT");
        List<User> hrUsers = userRepository.findByDepartment("HR");

        // Then
        assertEquals(2, itUsers.size());
        assertEquals(1, hrUsers.size());
        assertEquals("testuser2", hrUsers.get(0).getUsername());
    }

    @Test
    void testFindByStatus() {
        // When
        List<User> activeUsers = userRepository.findByStatus(User.UserStatus.ACTIVE);
        List<User> disabledUsers = userRepository.findByStatus(User.UserStatus.DISABLED);

        // Then
        assertEquals(2, activeUsers.size());
        assertEquals(1, disabledUsers.size());
        assertEquals("testuser2", disabledUsers.get(0).getUsername());
    }

    @Test
    void testFindByUsernameContainingOrFullNameContainingOrEmailContaining() {
        // When
        List<User> results = userRepository.findByUsernameContainingOrFullNameContainingOrEmailContaining(
                "user1", "user1", "user1");

        // Then
        assertEquals(1, results.size());
        assertEquals("testuser1", results.get(0).getUsername());
    }

    @Test
    void testFindByUsernameContainingOrFullNameContainingOrEmailContaining_WithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<User> results = userRepository.findByUsernameContainingOrFullNameContainingOrEmailContaining(
                "test", "test", "test", pageable);

        // Then
        assertEquals(3, results.getTotalElements());
        assertEquals(2, results.getContent().size());
        assertEquals(2, results.getTotalPages());
    }

    @Test
    void testFindByRoleAndStatus() {
        // When
        List<User> activeAdmins = userRepository.findByRoleAndStatus(
                User.UserRole.ADMIN, User.UserStatus.ACTIVE);
        List<User> disabledOperators = userRepository.findByRoleAndStatus(
                User.UserRole.OPERATOR, User.UserStatus.DISABLED);

        // Then
        assertEquals(1, activeAdmins.size());
        assertEquals("testuser1", activeAdmins.get(0).getUsername());

        assertEquals(1, disabledOperators.size());
        assertEquals("testuser2", disabledOperators.get(0).getUsername());
    }

    @Test
    void testFindActiveUsers() {
        // When
        List<User> activeUsers = userRepository.findActiveUsers();

        // Then
        // testuser1是活跃的，testuser3虽然状态是ACTIVE但被锁定了，testuser2是DISABLED
        assertEquals(1, activeUsers.size());
        assertEquals("testuser1", activeUsers.get(0).getUsername());
    }

    @Test
    void testFindLockedUsers() {
        // When
        List<User> lockedUsers = userRepository.findLockedUsers();

        // Then
        assertEquals(1, lockedUsers.size());
        assertEquals("testuser3", lockedUsers.get(0).getUsername());
    }

    @Test
    void testFindByCreatedBy() {
        // When
        List<User> usersCreatedByAdmin = userRepository.findByCreatedBy("admin");

        // Then
        assertEquals(3, usersCreatedByAdmin.size());
    }

    @Test
    void testCountUsersByRole() {
        // When
        List<Object[]> roleCounts = userRepository.countUsersByRole();

        // Then
        assertEquals(3, roleCounts.size());
        
        // 验证每个角色的数量
        for (Object[] roleCount : roleCounts) {
            User.UserRole role = (User.UserRole) roleCount[0];
            Long count = (Long) roleCount[1];
            assertEquals(1L, count);
        }
    }

    @Test
    void testCountUsersByStatus() {
        // When
        List<Object[]> statusCounts = userRepository.countUsersByStatus();

        // Then
        assertEquals(2, statusCounts.size());
        
        // 验证状态计数
        for (Object[] statusCount : statusCounts) {
            User.UserStatus status = (User.UserStatus) statusCount[0];
            Long count = (Long) statusCount[1];
            
            if (status == User.UserStatus.ACTIVE) {
                assertEquals(2L, count);
            } else if (status == User.UserStatus.DISABLED) {
                assertEquals(1L, count);
            }
        }
    }

    @Test
    void testCountByStatus() {
        // When
        long activeCount = userRepository.countByStatus(User.UserStatus.ACTIVE);
        long disabledCount = userRepository.countByStatus(User.UserStatus.DISABLED);
        long lockedCount = userRepository.countByStatus(User.UserStatus.LOCKED);

        // Then
        assertEquals(2, activeCount);
        assertEquals(1, disabledCount);
        assertEquals(0, lockedCount);
    }

    @Test
    void testCountByRole() {
        // When
        long adminCount = userRepository.countByRole(User.UserRole.ADMIN);
        long operatorCount = userRepository.countByRole(User.UserRole.OPERATOR);
        long observerCount = userRepository.countByRole(User.UserRole.OBSERVER);

        // Then
        assertEquals(1, adminCount);
        assertEquals(1, operatorCount);
        assertEquals(1, observerCount);
    }

    @Test
    void testSaveAndFindById() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("new@example.com");
        newUser.setFullName("New User");
        newUser.setRole(User.UserRole.OBSERVER);
        newUser.setStatus(User.UserStatus.ACTIVE);
        newUser.setCreatedBy("admin");

        // When
        User savedUser = userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertNotNull(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("newuser", foundUser.get().getUsername());
        assertEquals("new@example.com", foundUser.get().getEmail());
    }

    @Test
    void testDeleteUser() {
        // Given
        UUID userId = testUser1.getId();

        // When
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Then
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        // Given
        User user = userRepository.findByUsername("testuser1").orElseThrow();
        String newEmail = "updated@example.com";

        // When
        user.setEmail(newEmail);
        User updatedUser = userRepository.save(user);

        // Then
        assertEquals(newEmail, updatedUser.getEmail());
        
        // 验证数据库中的数据也被更新
        User reloadedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(newEmail, reloadedUser.getEmail());
    }
}