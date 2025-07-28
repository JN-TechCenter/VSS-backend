package com.vision.vision_platform_backend.dto;

import com.vision.vision_platform_backend.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserDto单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserDtoTest {

    private UserDto userDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        UserDto dto = new UserDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getFullName());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getDepartment());
        assertNull(dto.getPosition());
        assertNull(dto.getRole());
        assertNull(dto.getPassword());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String fullName = "Test User";
        String phoneNumber = "1234567890";
        String department = "IT";
        String position = "Developer";
        User.UserRole role = User.UserRole.ADMIN;

        // When
        UserDto dto = new UserDto(username, email, fullName, phoneNumber, department, position, role);

        // Then
        assertEquals(username, dto.getUsername());
        assertEquals(email, dto.getEmail());
        assertEquals(fullName, dto.getFullName());
        assertEquals(phoneNumber, dto.getPhoneNumber());
        assertEquals(department, dto.getDepartment());
        assertEquals(position, dto.getPosition());
        assertEquals(role, dto.getRole());
        assertNull(dto.getPassword()); // 构造函数中不设置密码
    }

    @Test
    void testSettersAndGetters() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String fullName = "Test User";
        String phoneNumber = "1234567890";
        String department = "Engineering";
        String position = "Senior Developer";
        User.UserRole role = User.UserRole.OPERATOR;
        String password = "password123";

        // When
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setFullName(fullName);
        userDto.setPhoneNumber(phoneNumber);
        userDto.setDepartment(department);
        userDto.setPosition(position);
        userDto.setRole(role);
        userDto.setPassword(password);

        // Then
        assertEquals(username, userDto.getUsername());
        assertEquals(email, userDto.getEmail());
        assertEquals(fullName, userDto.getFullName());
        assertEquals(phoneNumber, userDto.getPhoneNumber());
        assertEquals(department, userDto.getDepartment());
        assertEquals(position, userDto.getPosition());
        assertEquals(role, userDto.getRole());
        assertEquals(password, userDto.getPassword());
    }

    @Test
    void testValidUserDto() {
        // Given
        userDto.setUsername("validuser");
        userDto.setEmail("valid@example.com");
        userDto.setFullName("Valid User");
        userDto.setRole(User.UserRole.OBSERVER);

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidUsername_Null() {
        // Given
        userDto.setUsername(null);
        userDto.setEmail("test@example.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名不能为空")));
    }

    @Test
    void testInvalidUsername_Empty() {
        // Given
        userDto.setUsername("");
        userDto.setEmail("test@example.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名不能为空")));
    }

    @Test
    void testInvalidUsername_TooShort() {
        // Given
        userDto.setUsername("ab"); // 只有2个字符
        userDto.setEmail("test@example.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名长度必须在3-50个字符之间")));
    }

    @Test
    void testInvalidUsername_TooLong() {
        // Given
        userDto.setUsername("a".repeat(51)); // 51个字符
        userDto.setEmail("test@example.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("用户名长度必须在3-50个字符之间")));
    }

    @Test
    void testValidUsername_MinLength() {
        // Given
        userDto.setUsername("abc"); // 3个字符
        userDto.setEmail("test@example.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidUsername_MaxLength() {
        // Given
        userDto.setUsername("a".repeat(50)); // 50个字符
        userDto.setEmail("test@example.com");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail_Null() {
        // Given
        userDto.setUsername("testuser");
        userDto.setEmail(null);

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("邮箱不能为空")));
    }

    @Test
    void testInvalidEmail_Empty() {
        // Given
        userDto.setUsername("testuser");
        userDto.setEmail("");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("邮箱不能为空")));
    }

    @Test
    void testInvalidEmail_Format() {
        // Given
        userDto.setUsername("testuser");
        userDto.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("邮箱格式不正确")));
    }

    @Test
    void testValidEmail_Formats() {
        // Test various valid email formats
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@example.com"
        };

        for (String email : validEmails) {
            // Given
            userDto.setUsername("testuser");
            userDto.setEmail(email);

            // When
            Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

            // Then
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    @Test
    void testUserRoleAssignment() {
        // Test all user roles
        User.UserRole[] roles = User.UserRole.values();
        
        for (User.UserRole role : roles) {
            // Given
            userDto.setRole(role);

            // When & Then
            assertEquals(role, userDto.getRole());
        }
    }

    @Test
    void testOptionalFields() {
        // Given
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        // 不设置可选字段

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertTrue(violations.isEmpty()); // 可选字段为null应该是有效的
        assertNull(userDto.getFullName());
        assertNull(userDto.getPhoneNumber());
        assertNull(userDto.getDepartment());
        assertNull(userDto.getPosition());
        assertNull(userDto.getRole());
        assertNull(userDto.getPassword());
    }

    @Test
    void testPasswordField() {
        // Given
        String password = "securePassword123";
        userDto.setPassword(password);

        // When & Then
        assertEquals(password, userDto.getPassword());
    }

    @Test
    void testCompleteUserDto() {
        // Given
        userDto.setUsername("completeuser");
        userDto.setEmail("complete@example.com");
        userDto.setFullName("Complete User");
        userDto.setPhoneNumber("9876543210");
        userDto.setDepartment("Research");
        userDto.setPosition("Lead Researcher");
        userDto.setRole(User.UserRole.ADMIN);
        userDto.setPassword("complexPassword123");

        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals("completeuser", userDto.getUsername());
        assertEquals("complete@example.com", userDto.getEmail());
        assertEquals("Complete User", userDto.getFullName());
        assertEquals("9876543210", userDto.getPhoneNumber());
        assertEquals("Research", userDto.getDepartment());
        assertEquals("Lead Researcher", userDto.getPosition());
        assertEquals(User.UserRole.ADMIN, userDto.getRole());
        assertEquals("complexPassword123", userDto.getPassword());
    }
}