package com.vision.vision_platform_backend.controller;

import com.vision.vision_platform_backend.dto.UserDto;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage;
        
        if (search != null && !search.trim().isEmpty()) {
            userPage = userService.searchUsers(search, pageable);
        } else {
            userPage = userService.getAllUsers(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("totalElements", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("currentPage", userPage.getNumber());
        response.put("pageSize", userPage.getSize());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(user);
    }

    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDto userDto) {
        User createdUser = userService.createUser(userDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户创建成功");
        response.put("user", createdUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable UUID id, 
            @Valid @RequestBody UserDto userDto) {
        
        User updatedUser = userService.updateUser(id, userDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户信息更新成功");
        response.put("user", updatedUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "用户删除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String status = request.get("status");
        User updatedUser = userService.updateUserStatus(id, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户状态更新成功");
        response.put("user", updatedUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        
        String newPassword = request.get("password");
        userService.resetPassword(id, newPassword);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "密码重置成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * 批量操作用户
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, String>> batchOperation(
            @RequestBody Map<String, Object> request) {
        
        String operation = (String) request.get("operation");
        @SuppressWarnings("unchecked")
        java.util.List<UUID> userIds = (java.util.List<UUID>) request.get("userIds");
        
        userService.batchOperation(operation, userIds);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "批量操作执行成功");
        
        return ResponseEntity.ok(response);
    }
}