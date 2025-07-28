package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.dto.UserDto;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.repository.UserRepository;
import com.vision.vision_platform_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // 用户注册
    public User registerUser(String username, String password, String email, String fullName, 
                           String phoneNumber, String department, User.UserRole role, String createdBy) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建新用户并加密密码
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setDepartment(department);
        user.setRole(role != null ? role : User.UserRole.OBSERVER);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedBy(createdBy);
        user.setLoginAttempts(0);

        return userRepository.save(user);
    }

    // 用户登录验证
    public LoginResult authenticateUser(String username, String password) {
        // 根据用户名查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查账户状态
        if (user.getStatus() == User.UserStatus.DISABLED) {
            throw new RuntimeException("账户已被禁用");
        }

        if (user.checkAccountLocked()) {
            throw new RuntimeException("账户已被锁定，请稍后再试");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 增加登录失败次数
            user.incrementLoginAttempts();
            userRepository.save(user);
            throw new RuntimeException("密码错误");
        }

        // 登录成功，重置登录尝试次数并更新最后登录时间
        user.resetLoginAttempts();
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        return new LoginResult(user, token);
    }

    // 检查用户是否启用
    public boolean isUserEnabledByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        // 暂时使用status判断，避免isEnabled方法找不到的编译错误
        return userOpt.map(user -> user.getStatus() == User.UserStatus.ACTIVE).orElse(false);
        // return userOpt.map(User::isEnabled).orElse(false);
    }

    // 获取用户信息
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    // 获取所有用户（分页）
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 根据角色获取用户
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    // 根据部门获取用户
    public List<User> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    // 更新用户信息
    public User updateUser(UUID userId, String fullName, String email, String phoneNumber, 
                          String department, User.UserRole role, String updatedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (fullName != null) user.setFullName(fullName);
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("邮箱已存在");
            }
            user.setEmail(email);
        }
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if (department != null) user.setDepartment(department);
        if (role != null) user.setRole(role);
        user.setUpdatedBy(updatedBy);

        return userRepository.save(user);
    }

    // 更改密码
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 重置密码（管理员功能）
    public void resetPassword(UUID userId, String newPassword, String operatorUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedBy(operatorUsername);
        user.resetLoginAttempts(); // 重置登录尝试次数
        userRepository.save(user);
    }

    // 启用/禁用用户
    public void toggleUserStatus(UUID userId, String operatorUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(user.getStatus() == User.UserStatus.ACTIVE ? 
                      User.UserStatus.DISABLED : User.UserStatus.ACTIVE);
        user.setUpdatedBy(operatorUsername);
        userRepository.save(user);
    }

    // 解锁用户账户
    public void unlockUser(UUID userId, String operatorUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.resetLoginAttempts();
        user.setUpdatedBy(operatorUsername);
        userRepository.save(user);
    }

    // 删除用户
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }

    // 搜索用户
    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingOrFullNameContainingOrEmailContaining(
                keyword, keyword, keyword);
    }

    // 搜索用户（分页）
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByUsernameContainingOrFullNameContainingOrEmailContaining(
                keyword, keyword, keyword, pageable);
    }

    // 更新用户状态
    public User updateUserStatus(UUID userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        User.UserStatus userStatus;
        switch (status.toLowerCase()) {
            case "active":
                userStatus = User.UserStatus.ACTIVE;
                break;
            case "disabled":
                userStatus = User.UserStatus.DISABLED;
                break;
            case "locked":
                userStatus = User.UserStatus.LOCKED;
                break;
            default:
                throw new RuntimeException("无效的用户状态");
        }

        user.setStatus(userStatus);
        return userRepository.save(user);
    }

    // 重置密码（管理员功能，重载方法）
    public void resetPassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.resetLoginAttempts(); // 重置登录尝试次数
        userRepository.save(user);
    }

    // 获取用户统计信息
    public java.util.Map<String, Object> getUserStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(User.UserStatus.ACTIVE);
        long disabledUsers = userRepository.countByStatus(User.UserStatus.DISABLED);
        long lockedUsers = userRepository.countByStatus(User.UserStatus.LOCKED);
        
        long adminUsers = userRepository.countByRole(User.UserRole.ADMIN);
        long operatorUsers = userRepository.countByRole(User.UserRole.OPERATOR);
        long observerUsers = userRepository.countByRole(User.UserRole.OBSERVER);
        
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("disabledUsers", disabledUsers);
        stats.put("lockedUsers", lockedUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("operatorUsers", operatorUsers);
        stats.put("observerUsers", observerUsers);
        
        return stats;
    }

    // 批量操作用户
    public void batchOperation(String operation, java.util.List<UUID> userIds) {
        switch (operation.toLowerCase()) {
            case "enable":
                userIds.forEach(id -> updateUserStatus(id, "active"));
                break;
            case "disable":
                userIds.forEach(id -> updateUserStatus(id, "disabled"));
                break;
            case "delete":
                userIds.forEach(this::deleteUser);
                break;
            case "unlock":
                userIds.forEach(id -> {
                    User user = userRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("用户不存在"));
                    user.resetLoginAttempts();
                    userRepository.save(user);
                });
                break;
            default:
                throw new RuntimeException("不支持的批量操作");
        }
    }

    // 使用UserDto创建用户
    public User createUser(UserDto userDto) {
        return registerUser(
            userDto.getUsername(),
            userDto.getPassword(),
            userDto.getEmail(),
            userDto.getFullName(),
            userDto.getPhoneNumber(),
            userDto.getDepartment(),
            userDto.getRole(),
            "admin" // 默认创建者
        );
    }

    // 使用UserDto更新用户
    public User updateUser(UUID userId, UserDto userDto) {
        return updateUser(
            userId,
            userDto.getFullName(),
            userDto.getEmail(),
            userDto.getPhoneNumber(),
            userDto.getDepartment(),
            userDto.getRole(),
            "admin" // 默认更新者
        );
    }

    // 登录结果类
    public static class LoginResult {
        private final User user;
        private final String token;

        public LoginResult(User user, String token) {
            this.user = user;
            this.token = token;
        }

        public User getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }
    }
}