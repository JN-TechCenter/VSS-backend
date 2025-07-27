package com.vision.vision_platform_backend.controller;

import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.service.UserService;
import com.vision.vision_platform_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(), 
                request.getPassword(), 
                request.getEmail(),
                request.getFullName(),
                request.getPhoneNumber(),
                request.getDepartment(),
                request.getRole(),
                "system" // 创建者
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册成功");
            response.put("user", createUserResponse(user));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 用户登录接口
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            UserService.LoginResult result = userService.authenticateUser(
                request.getUsername(), 
                request.getPassword()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录成功");
            response.put("token", result.getToken());
            response.put("user", createUserResponse(result.getUser()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // 获取当前用户信息
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String username = getCurrentUsername(request);
            Optional<User> userOpt = userService.getUserByUsername(username);
            
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(createUserResponse(userOpt.get()));
            } else {
                return createErrorResponse("用户不存在", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("获取用户信息失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新当前用户信息
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request, 
                                         HttpServletRequest httpRequest) {
        try {
            String username = getCurrentUsername(httpRequest);
            Optional<User> userOpt = userService.getUserByUsername(username);
            
            if (userOpt.isPresent()) {
                User updatedUser = userService.updateUser(
                    userOpt.get().getId(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    request.getDepartment(),
                    null, // 普通用户不能修改自己的角色
                    username
                );
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "更新成功");
                response.put("user", createUserResponse(updatedUser));
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("用户不存在", HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 修改密码
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            String username = getCurrentUsername(httpRequest);
            Optional<User> userOpt = userService.getUserByUsername(username);
            
            if (userOpt.isPresent()) {
                userService.changePassword(
                    userOpt.get().getId(),
                    request.getOldPassword(),
                    request.getNewPassword()
                );
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "密码修改成功");
                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("用户不存在", HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 获取所有用户（管理员权限）
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<User> users = userService.getAllUsers(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users.getContent().stream()
                    .map(this::createUserResponse).toList());
            response.put("totalElements", users.getTotalElements());
            response.put("totalPages", users.getTotalPages());
            response.put("currentPage", users.getNumber());
            response.put("size", users.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取用户列表失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 根据ID获取用户（管理员权限）
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(createUserResponse(userOpt.get()));
            } else {
                return createErrorResponse("用户不存在", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("获取用户信息失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新用户信息（管理员权限）
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, 
                                      @RequestBody UpdateUserRequest request,
                                      HttpServletRequest httpRequest) {
        try {
            String operatorUsername = getCurrentUsername(httpRequest);
            User updatedUser = userService.updateUser(
                id,
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getDepartment(),
                request.getRole(),
                operatorUsername
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户更新成功");
            response.put("user", createUserResponse(updatedUser));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 重置用户密码（管理员权限）
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPassword(@PathVariable UUID id,
                                         @RequestBody ResetPasswordRequest request,
                                         HttpServletRequest httpRequest) {
        try {
            String operatorUsername = getCurrentUsername(httpRequest);
            userService.resetPassword(id, request.getNewPassword(), operatorUsername);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "密码重置成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 启用/禁用用户（管理员权限）
    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable UUID id,
                                            HttpServletRequest httpRequest) {
        try {
            String operatorUsername = getCurrentUsername(httpRequest);
            userService.toggleUserStatus(id, operatorUsername);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户状态更新成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 解锁用户（管理员权限）
    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unlockUser(@PathVariable UUID id,
                                      HttpServletRequest httpRequest) {
        try {
            String operatorUsername = getCurrentUsername(httpRequest);
            userService.unlockUser(id, operatorUsername);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户解锁成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 删除用户（管理员权限）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 搜索用户（管理员权限）
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        try {
            List<User> users = userService.searchUsers(keyword);
            List<Map<String, Object>> userResponses = users.stream()
                    .map(this::createUserResponse).toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", userResponses);
            response.put("total", users.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("搜索用户失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取用户角色列表
    @GetMapping("/roles")
    public ResponseEntity<?> getUserRoles() {
        Map<String, Object> response = new HashMap<>();
        response.put("roles", User.UserRole.values());
        return ResponseEntity.ok(response);
    }

    // 辅助方法：从请求中获取当前用户名
    private String getCurrentUsername(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUsernameFromToken(token);
        }
        throw new RuntimeException("无效的认证令牌");
    }

    // 辅助方法：创建用户响应对象
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("department", user.getDepartment());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());
        response.put("lastLoginTime", user.getLastLoginTime());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        response.put("isAccountLocked", user.isAccountLocked());
        return response;
    }

    // 辅助方法：创建错误响应
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, status);
    }

    // 请求DTO类
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String department;
        private User.UserRole role;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public User.UserRole getRole() { return role; }
        public void setRole(User.UserRole role) { this.role = role; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateProfileRequest {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String department;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }

    public static class UpdateUserRequest extends UpdateProfileRequest {
        private User.UserRole role;

        public User.UserRole getRole() { return role; }
        public void setRole(User.UserRole role) { this.role = role; }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ResetPasswordRequest {
        private String newPassword;

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}