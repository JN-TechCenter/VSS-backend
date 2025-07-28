package com.vision.vision_platform_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.UserDto;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.service.UserService;
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
class UserManagementControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserManagementController userManagementController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private UserDto testUserDto;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userManagementController).build();
        objectMapper = new ObjectMapper();
        
        testUserId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(User.Role.USER);
        testUser.setStatus(User.Status.ACTIVE);
        testUser.setDepartment("IT");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        
        testUserDto = new UserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setFullName("Test User");
        testUserDto.setRole(User.Role.USER);
        testUserDto.setDepartment("IT");
        testUserDto.setPassword("password123");
    }

    @Test
    void getUsers_WithoutSearch() throws Exception {
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/admin/users")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));

        verify(userService).getAllUsers(any(Pageable.class));
        verify(userService, never()).searchUsers(anyString(), any(Pageable.class));
    }

    @Test
    void getUsers_WithSearch() throws Exception {
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userService.searchUsers(eq("test"), any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/admin/users")
                .param("page", "0")
                .param("size", "10")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService).searchUsers(eq("test"), any(Pageable.class));
        verify(userService, never()).getAllUsers(any(Pageable.class));
    }

    @Test
    void getUsers_WithEmptySearch() throws Exception {
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/admin/users")
                .param("search", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray());

        verify(userService).getAllUsers(any(Pageable.class));
        verify(userService, never()).searchUsers(anyString(), any(Pageable.class));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/admin/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserById(testUserId);
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/" + testUserId))
                .andExpect(status().isInternalServerError());

        verify(userService).getUserById(testUserId);
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void createUser_ValidationError() throws Exception {
        UserDto invalidUserDto = new UserDto();
        // 不设置必需字段，触发验证错误

        mockMvc.perform(post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserDto.class));
    }

    @Test
    void updateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(testUserId);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        
        when(userService.updateUser(eq(testUserId), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/admin/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户信息更新成功"))
                .andExpect(jsonPath("$.user.id").value(testUserId.toString()));

        verify(userService).updateUser(eq(testUserId), any(UserDto.class));
    }

    @Test
    void updateUser_UserNotFound() throws Exception {
        when(userService.updateUser(eq(testUserId), any(UserDto.class)))
                .thenThrow(new RuntimeException("用户不存在"));

        mockMvc.perform(put("/api/admin/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isInternalServerError());

        verify(userService).updateUser(eq(testUserId), any(UserDto.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/admin/users/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService).deleteUser(testUserId);
    }

    @Test
    void deleteUser_UserNotFound() throws Exception {
        doThrow(new RuntimeException("用户不存在")).when(userService).deleteUser(testUserId);

        mockMvc.perform(delete("/api/admin/users/" + testUserId))
                .andExpect(status().isInternalServerError());

        verify(userService).deleteUser(testUserId);
    }

    @Test
    void toggleUserStatus_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(testUserId);
        updatedUser.setStatus(User.Status.INACTIVE);
        
        when(userService.updateUserStatus(testUserId, "INACTIVE")).thenReturn(updatedUser);

        Map<String, String> request = Map.of("status", "INACTIVE");

        mockMvc.perform(put("/api/admin/users/" + testUserId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户状态更新成功"))
                .andExpect(jsonPath("$.user.id").value(testUserId.toString()));

        verify(userService).updateUserStatus(testUserId, "INACTIVE");
    }

    @Test
    void toggleUserStatus_InvalidStatus() throws Exception {
        when(userService.updateUserStatus(testUserId, "INVALID_STATUS"))
                .thenThrow(new RuntimeException("无效的用户状态"));

        Map<String, String> request = Map.of("status", "INVALID_STATUS");

        mockMvc.perform(put("/api/admin/users/" + testUserId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userService).updateUserStatus(testUserId, "INVALID_STATUS");
    }

    @Test
    void resetPassword_Success() throws Exception {
        doNothing().when(userService).resetPassword(testUserId, "newpassword123");

        Map<String, String> request = Map.of("password", "newpassword123");

        mockMvc.perform(put("/api/admin/users/" + testUserId + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        verify(userService).resetPassword(testUserId, "newpassword123");
    }

    @Test
    void resetPassword_UserNotFound() throws Exception {
        doThrow(new RuntimeException("用户不存在"))
                .when(userService).resetPassword(testUserId, "newpassword123");

        Map<String, String> request = Map.of("password", "newpassword123");

        mockMvc.perform(put("/api/admin/users/" + testUserId + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userService).resetPassword(testUserId, "newpassword123");
    }

    @Test
    void getUserStatistics_Success() throws Exception {
        Map<String, Object> statistics = Map.of(
                "totalUsers", 100,
                "activeUsers", 80,
                "inactiveUsers", 15,
                "lockedUsers", 5
        );
        
        when(userService.getUserStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/admin/users/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.activeUsers").value(80))
                .andExpect(jsonPath("$.inactiveUsers").value(15))
                .andExpect(jsonPath("$.lockedUsers").value(5));

        verify(userService).getUserStatistics();
    }

    @Test
    void batchOperation_Success() throws Exception {
        List<UUID> userIds = Arrays.asList(testUserId, UUID.randomUUID());
        doNothing().when(userService).batchOperation("enable", userIds);

        Map<String, Object> request = Map.of(
                "operation", "enable",
                "userIds", userIds
        );

        mockMvc.perform(post("/api/admin/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("批量操作执行成功"));

        verify(userService).batchOperation("enable", userIds);
    }

    @Test
    void batchOperation_InvalidOperation() throws Exception {
        List<UUID> userIds = Arrays.asList(testUserId);
        doThrow(new RuntimeException("无效的操作类型"))
                .when(userService).batchOperation("invalid_operation", userIds);

        Map<String, Object> request = Map.of(
                "operation", "invalid_operation",
                "userIds", userIds
        );

        mockMvc.perform(post("/api/admin/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userService).batchOperation("invalid_operation", userIds);
    }

    @Test
    void batchOperation_EmptyUserIds() throws Exception {
        List<UUID> userIds = Collections.emptyList();
        doThrow(new RuntimeException("用户ID列表不能为空"))
                .when(userService).batchOperation("enable", userIds);

        Map<String, Object> request = Map.of(
                "operation", "enable",
                "userIds", userIds
        );

        mockMvc.perform(post("/api/admin/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userService).batchOperation("enable", userIds);
    }
}