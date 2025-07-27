package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // 根据角色查询用户
    List<User> findByRole(User.UserRole role);
    
    // 根据部门查询用户
    List<User> findByDepartment(String department);
    
    // 根据状态查询用户
    List<User> findByStatus(User.UserStatus status);
    
    // 搜索用户（用户名、全名、邮箱）
    List<User> findByUsernameContainingOrFullNameContainingOrEmailContaining(
            String username, String fullName, String email);
    
    // 搜索用户（分页）
    Page<User> findByUsernameContainingOrFullNameContainingOrEmailContaining(
            String username, String fullName, String email, Pageable pageable);
    
    // 根据角色和状态查询用户
    List<User> findByRoleAndStatus(User.UserRole role, User.UserStatus status);
    
    // 查询启用的用户
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND (u.accountLockedUntil IS NULL OR u.accountLockedUntil < CURRENT_TIMESTAMP)")
    List<User> findActiveUsers();
    
    // 查询被锁定的用户
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > CURRENT_TIMESTAMP")
    List<User> findLockedUsers();
    
    // 根据创建者查询用户
    List<User> findByCreatedBy(String createdBy);
    
    // 统计各角色用户数量
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();
    
    // 统计各状态用户数量
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();
    
    // 按状态统计用户数量
    long countByStatus(User.UserStatus status);
    
    // 按角色统计用户数量
    long countByRole(User.UserRole role);
}