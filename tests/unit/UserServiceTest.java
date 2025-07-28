package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.repository.UserRepository;
import com.vision.vision_platform_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("1234567890");
        testUser.setDepartment("IT");
        testUser.setRole(User.UserRole.OBSERVER);
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser.setLoginAttempts(0);
        testUser.setCreatedBy("admin");
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        String username = "newuser";
        String password = "password123";
        String email = "newuser@example.com";
        String fullName = "New User";
        String phoneNumber = "9876543210";
        String department = "HR";
        User.UserRole role = User.UserRole.OPERATOR;
        String createdBy = "admin";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.registerUser(username, password, email, fullName, 
                                             phoneNumber, department, role, createdBy);

        // Then
        assertNotNull(result);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Given
        String username = "existinguser";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, "password", "email@test.com", 
                                   "Full Name", "123456", "IT", User.UserRole.OBSERVER, "admin");
        });
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void testRegisterUser_EmailExists() {
        // Given
        String username = "newuser";
        String email = "existing@example.com";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, "password", email, 
                                   "Full Name", "123456", "IT", User.UserRole.OBSERVER, "admin");
        });
        assertEquals("邮箱已存在", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_Success() {
        // Given
        String username = "testuser";
        String password = "password123";
        String token = "jwt-token";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any(UUID.class), anyString())).thenReturn(token);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserService.LoginResult result = userService.authenticateUser(username, password);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(token, result.getToken());
        verify(userRepository).save(testUser);
        assertEquals(0, testUser.getLoginAttempts());
        assertNotNull(testUser.getLastLoginTime());
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser(username, "password");
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_AccountDisabled() {
        // Given
        testUser.setStatus(User.UserStatus.DISABLED);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser("testuser", "password");
        });
        assertEquals("账户已被禁用", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser(username, password);
        });
        assertEquals("密码错误", exception.getMessage());
        verify(userRepository).save(testUser);
    }

    @Test
    void testIsUserEnabledByUsername_UserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.isUserEnabledByUsername("testuser");

        // Then
        assertTrue(result);
    }

    @Test
    void testIsUserEnabledByUsername_UserNotExists() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        boolean result = userService.isUserEnabledByUsername("nonexistent");

        // Then
        assertFalse(result);
    }

    @Test
    void testIsUserEnabledByUsername_UserDisabled() {
        // Given
        testUser.setStatus(User.UserStatus.DISABLED);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.isUserEnabledByUsername("testuser");

        // Then
        assertFalse(result);
    }

    @Test
    void testGetUserByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(testUserId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetAllUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser, result.getContent().get(0));
    }

    @Test
    void testGetUsersByRole() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRole(User.UserRole.OBSERVER)).thenReturn(users);

        // When
        List<User> result = userService.getUsersByRole(User.UserRole.OBSERVER);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
    }

    @Test
    void testGetUsersByDepartment() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByDepartment("IT")).thenReturn(users);

        // When
        List<User> result = userService.getUsersByDepartment("IT");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        String newFullName = "Updated Name";
        String newEmail = "updated@example.com";
        String newPhoneNumber = "9999999999";
        String newDepartment = "HR";
        User.UserRole newRole = User.UserRole.ADMINISTRATOR;
        String updatedBy = "admin";

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(testUserId, newFullName, newEmail, 
                                           newPhoneNumber, newDepartment, newRole, updatedBy);

        // Then
        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(testUserId, "New Name", "new@email.com", 
                                 "123456", "IT", User.UserRole.OBSERVER, "admin");
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testUpdateUser_EmailExists() {
        // Given
        String existingEmail = "existing@example.com";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(testUserId, "New Name", existingEmail, 
                                 "123456", "IT", User.UserRole.OBSERVER, "admin");
        });
        assertEquals("邮箱已存在", exception.getMessage());
    }

    @Test
    void testChangePassword_Success() {
        // Given
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(testUserId, oldPassword, newPassword);

        // Then
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
    }

    @Test
    void testChangePassword_UserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword(testUserId, "oldpass", "newpass");
        });
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testChangePassword_WrongOldPassword() {
        // Given
        String oldPassword = "wrongoldpassword";
        String newPassword = "newpassword";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPassword())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword(testUserId, oldPassword, newPassword);
        });
        assertEquals("原密码错误", exception.getMessage());
    }

    @Test
    void testResetPassword_Success() {
        // Given
        String newPassword = "resetpassword";
        String operatorUsername = "admin";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("resetEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.resetPassword(testUserId, newPassword, operatorUsername);

        // Then
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
        assertEquals(0, testUser.getLoginAttempts());
    }

    @Test
    void testToggleUserStatus_Success() {
        // Given
        String operatorUsername = "admin";
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.toggleUserStatus(testUserId, operatorUsername);

        // Then
        verify(userRepository).save(testUser);
        assertEquals(User.UserStatus.DISABLED, testUser.getStatus());
    }

    @Test
    void testUnlockUser_Success() {
        // Given
        String operatorUsername = "admin";
        testUser.setLoginAttempts(5);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.unlockUser(testUserId, operatorUsername);

        // Then
        verify(userRepository).save(testUser);
        assertEquals(0, testUser.getLoginAttempts());
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.existsById(testUserId)).thenReturn(true);

        // When
        userService.deleteUser(testUserId);

        // Then
        verify(userRepository).deleteById(testUserId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        when(userRepository.existsById(testUserId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(testUserId);
        });
        assertEquals("用户不存在", exception.getMessage());
    }
}