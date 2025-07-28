package com.vision.vision_platform_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.UserDto;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户认证和管理集成测试
 * 测试完整的用户注册、登录、权限验证流程
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("用户认证和管理集成测试")
public class UserAuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    @DisplayName("1. 用户注册测试")
    void testUserRegistration() throws Exception {
        // 准备注册数据
        UserDto registerDto = new UserDto();
        registerDto.setUsername("testuser");
        registerDto.setPassword("password123");
        registerDto.setEmail("test@example.com");
        registerDto.setFullName("测试用户");
        registerDto.setPhoneNumber("13800138000");
        registerDto.setDepartment("技术部");
        registerDto.setRole("OBSERVER");

        // 执行注册请求
        MvcResult result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("测试用户"))
                .andExpect(jsonPath("$.role").value("OBSERVER"))
                .andReturn();

        // 验证用户已保存到数据库
        assertTrue(userRepository.existsByUsername("testuser"));
        assertTrue(userRepository.existsByEmail("test@example.com"));

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("注册响应: " + responseContent);
    }

    @Test
    @Order(2)
    @DisplayName("2. 重复注册测试")
    void testDuplicateRegistration() throws Exception {
        // 先注册一个用户
        UserDto registerDto = new UserDto();
        registerDto.setUsername("duplicate");
        registerDto.setPassword("password123");
        registerDto.setEmail("duplicate@example.com");
        registerDto.setFullName("重复用户");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated());

        // 尝试重复注册相同用户名
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        // 尝试重复注册相同邮箱
        registerDto.setUsername("different");
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Order(3)
    @DisplayName("3. 用户登录测试")
    void testUserLogin() throws Exception {
        // 先注册用户
        UserDto registerDto = new UserDto();
        registerDto.setUsername("logintest");
        registerDto.setPassword("password123");
        registerDto.setEmail("login@example.com");
        registerDto.setFullName("登录测试用户");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated());

        // 准备登录数据
        Map<String, String> loginData = Map.of(
                "username", "logintest",
                "password", "password123"
        );

        // 执行登录请求
        MvcResult result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("logintest"))
                .andExpect(jsonPath("$.user.email").value("login@example.com"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        userToken = (String) response.get("token");
        
        assertNotNull(userToken);
        assertTrue(userToken.length() > 0);
        
        System.out.println("登录成功，获取Token: " + userToken.substring(0, 20) + "...");
    }

    @Test
    @Order(4)
    @DisplayName("4. 错误登录测试")
    void testInvalidLogin() throws Exception {
        // 测试错误用户名
        Map<String, String> invalidUser = Map.of(
                "username", "nonexistent",
                "password", "password123"
        );

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());

        // 测试错误密码
        Map<String, String> invalidPassword = Map.of(
                "username", "logintest",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Order(5)
    @DisplayName("5. 管理员登录测试")
    void testAdminLogin() throws Exception {
        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.5fO1aVvS"); // admin123
        admin.setEmail("admin@example.com");
        admin.setFullName("系统管理员");
        admin.setRole(User.UserRole.ADMIN);
        admin.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(admin);

        // 管理员登录
        Map<String, String> adminLogin = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        MvcResult result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        adminToken = (String) response.get("token");
        
        assertNotNull(adminToken);
        System.out.println("管理员登录成功");
    }

    @Test
    @Order(6)
    @DisplayName("6. 权限验证测试")
    void testAuthorizationWithToken() throws Exception {
        // 确保有有效的用户token
        if (userToken == null) {
            testUserLogin();
        }

        // 测试需要认证的端点
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists());

        // 测试无token访问
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());

        // 测试无效token
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("7. 管理员权限测试")
    void testAdminAuthorization() throws Exception {
        // 确保有有效的管理员token
        if (adminToken == null) {
            testAdminLogin();
        }

        // 测试管理员专用端点
        mockMvc.perform(get("/api/users/search")
                .param("keyword", "test")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 测试普通用户访问管理员端点
        if (userToken == null) {
            testUserLogin();
        }

        mockMvc.perform(get("/api/users/search")
                .param("keyword", "test")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(8)
    @DisplayName("8. 用户信息更新测试")
    void testUserProfileUpdate() throws Exception {
        // 确保有有效的用户token
        if (userToken == null) {
            testUserLogin();
        }

        // 准备更新数据
        Map<String, String> updateData = Map.of(
                "fullName", "更新后的姓名",
                "phoneNumber", "13900139000",
                "department", "产品部"
        );

        // 执行更新请求
        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("更新后的姓名"))
                .andExpect(jsonPath("$.phoneNumber").value("13900139000"))
                .andExpect(jsonPath("$.department").value("产品部"));
    }

    @Test
    @Order(9)
    @DisplayName("9. 密码修改测试")
    void testPasswordChange() throws Exception {
        // 确保有有效的用户token
        if (userToken == null) {
            testUserLogin();
        }

        // 准备密码修改数据
        Map<String, String> passwordData = Map.of(
                "currentPassword", "password123",
                "newPassword", "newpassword456"
        );

        // 执行密码修改请求
        mockMvc.perform(post("/api/users/change-password")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 验证新密码可以登录
        Map<String, String> newLoginData = Map.of(
                "username", "logintest",
                "password", "newpassword456"
        );

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLoginData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // 验证旧密码不能登录
        Map<String, String> oldLoginData = Map.of(
                "username", "logintest",
                "password", "password123"
        );

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oldLoginData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("10. 完整用户生命周期测试")
    void testCompleteUserLifecycle() throws Exception {
        String testUsername = "lifecycle";
        
        // 1. 注册
        UserDto registerDto = new UserDto();
        registerDto.setUsername(testUsername);
        registerDto.setPassword("password123");
        registerDto.setEmail("lifecycle@example.com");
        registerDto.setFullName("生命周期测试用户");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated());

        // 2. 登录
        Map<String, String> loginData = Map.of(
                "username", testUsername,
                "password", "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        String token = (String) response.get("token");

        // 3. 获取用户信息
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUsername));

        // 4. 更新用户信息
        Map<String, String> updateData = Map.of(
                "fullName", "更新的生命周期用户",
                "department", "测试部"
        );

        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("更新的生命周期用户"));

        // 5. 验证更新后的信息
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("更新的生命周期用户"))
                .andExpect(jsonPath("$.department").value("测试部"));

        System.out.println("完整用户生命周期测试完成");
    }
}