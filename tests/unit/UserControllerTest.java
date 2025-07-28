package com.vision.vision_platform_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.service.UserService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        
        testUserId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("1234567890");
        testUser.setDepartment("IT");
        testUser.setRole(User.Role.USER);
        testUser.setStatus(User.Status.ACTIVE);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setLastLoginTime(LocalDateTime.now());
    }

    @Test
    void registerUser_Success() throws Exception {
        when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), any(), anyString())).thenReturn(testUser);

        UserController.RegisterRequest request = new UserController.RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPhoneNumber("1234567890");
        request.setDepartment("IT");
        request.setRole(User.UserRole.USER);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        verify(userService).registerUser(eq("testuser"), eq("password123"), eq("test@example.com"),
                eq("Test User"), eq("1234567890"), eq("IT"), eq(User.UserRole.USER), eq("system"));
    }

    @Test
    void registerUser_UsernameExists() throws Exception {
        when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), any(), anyString()))
                .thenThrow(new RuntimeException("用户名已存在"));

        UserController.RegisterRequest request = new UserController.RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("用户名已存在"));

        verify(userService).registerUser(anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), any(), anyString());
    }

    @Test
    void loginUser_Success() throws Exception {
        UserService.LoginResult loginResult = new UserService.LoginResult("jwt-token", testUser);
        when(userService.authenticateUser("testuser", "password123")).thenReturn(loginResult);

        UserController.LoginRequest request = new UserController.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.username").value("testuser"));

        verify(userService).authenticateUser("testuser", "password123");
    }

    @Test
    void loginUser_InvalidCredentials() throws Exception {
        when(userService.authenticateUser("testuser", "wrongpassword"))
                .thenThrow(new RuntimeException("用户名或密码错误"));

        UserController.LoginRequest request = new UserController.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("用户名或密码错误"));

        verify(userService).authenticateUser("testuser", "wrongpassword");
    }

    @Test
    void getCurrentUser_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(jwtUtil).getUsernameFromToken("jwt-token");
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void getCurrentUser_UserNotFound() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));

        verify(jwtUtil).getUsernameFromToken("jwt-token");
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void getCurrentUser_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("获取用户信息失败"));

        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    void updateProfile_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        User updatedUser = new User();
        updatedUser.setId(testUserId);
        updatedUser.setUsername("testuser");
        updatedUser.setFullName("Updated User");
        updatedUser.setEmail("updated@example.com");
        
        when(userService.updateUser(eq(testUserId), eq("Updated User"), eq("updated@example.com"),
                anyString(), anyString(), isNull(), eq("testuser"))).thenReturn(updatedUser);

        UserController.UpdateProfileRequest request = new UserController.UpdateProfileRequest();
        request.setFullName("Updated User");
        request.setEmail("updated@example.com");
        request.setPhoneNumber("9876543210");
        request.setDepartment("HR");

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.user.fullName").value("Updated User"));

        verify(userService).updateUser(eq(testUserId), eq("Updated User"), eq("updated@example.com"),
                eq("9876543210"), eq("HR"), isNull(), eq("testuser"));
    }

    @Test
    void changePassword_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        doNothing().when(userService).changePassword(testUserId, "oldpassword", "newpassword");

        UserController.ChangePasswordRequest request = new UserController.ChangePasswordRequest();
        request.setOldPassword("oldpassword");
        request.setNewPassword("newpassword");

        mockMvc.perform(put("/api/users/change-password")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        verify(userService).changePassword(testUserId, "oldpassword", "newpassword");
    }

    @Test
    void changePassword_WrongOldPassword() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("原密码错误"))
                .when(userService).changePassword(testUserId, "wrongpassword", "newpassword");

        UserController.ChangePasswordRequest request = new UserController.ChangePasswordRequest();
        request.setOldPassword("wrongpassword");
        request.setNewPassword("newpassword");

        mockMvc.perform(put("/api/users/change-password")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("原密码错误"));

        verify(userService).changePassword(testUserId, "wrongpassword", "newpassword");
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).getUserById(testUserId);
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/" + testUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));

        verify(userService).getUserById(testUserId);
    }

    @Test
    void updateUser_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("admin");
        
        User updatedUser = new User();
        updatedUser.setId(testUserId);
        updatedUser.setUsername("testuser");
        updatedUser.setRole(User.Role.ADMIN);
        
        when(userService.updateUser(eq(testUserId), anyString(), anyString(), anyString(), 
                anyString(), any(), eq("admin"))).thenReturn(updatedUser);

        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest();
        request.setFullName("Updated User");
        request.setEmail("updated@example.com");
        request.setRole(User.UserRole.ADMIN);

        mockMvc.perform(put("/api/users/" + testUserId)
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户更新成功"))
                .andExpect(jsonPath("$.user.id").value(testUserId.toString()));

        verify(userService).updateUser(eq(testUserId), eq("Updated User"), eq("updated@example.com"),
                isNull(), isNull(), eq(User.UserRole.ADMIN), eq("admin"));
    }

    @Test
    void resetPassword_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("admin");
        doNothing().when(userService).resetPassword(testUserId, "newpassword", "admin");

        UserController.ResetPasswordRequest request = new UserController.ResetPasswordRequest();
        request.setNewPassword("newpassword");

        mockMvc.perform(put("/api/users/" + testUserId + "/reset-password")
                .header("Authorization", "Bearer jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        verify(userService).resetPassword(testUserId, "newpassword", "admin");
    }

    @Test
    void toggleUserStatus_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("admin");
        doNothing().when(userService).toggleUserStatus(testUserId, "admin");

        mockMvc.perform(put("/api/users/" + testUserId + "/toggle-status")
                .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户状态更新成功"));

        verify(userService).toggleUserStatus(testUserId, "admin");
    }

    @Test
    void unlockUser_Success() throws Exception {
        when(jwtUtil.getUsernameFromToken("jwt-token")).thenReturn("admin");
        doNothing().when(userService).unlockUser(testUserId, "admin");

        mockMvc.perform(put("/api/users/" + testUserId + "/unlock")
                .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户解锁成功"));

        verify(userService).unlockUser(testUserId, "admin");
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService).deleteUser(testUserId);
    }

    @Test
    void deleteUser_UserNotFound() throws Exception {
        doThrow(new RuntimeException("用户不存在")).when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/users/" + testUserId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("用户不存在"));

        verify(userService).deleteUser(testUserId);
    }

    @Test
    void searchUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.searchUsers("test")).thenReturn(users);

        mockMvc.perform(get("/api/users/search")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.total").value(1));

        verify(userService).searchUsers("test");
    }

    @Test
    void getUserRoles_Success() throws Exception {
        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray());

        // 不需要验证service调用，因为这是静态数据
    }
}