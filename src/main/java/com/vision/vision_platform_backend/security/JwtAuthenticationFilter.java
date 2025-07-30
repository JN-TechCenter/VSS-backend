package com.vision.vision_platform_backend.security;

import com.vision.vision_platform_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // 跳过不需要认证的路径
        if (shouldSkipAuthentication(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.error("JWT Token解析失败: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, username)) {
                // 从JWT中获取用户信息
                String role = jwtUtil.getUserRoleFromToken(token);
                UUID userId = jwtUtil.getUserIdFromToken(token);
                
                // 创建权限列表
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 将用户ID添加到认证详情中
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // 可以通过request attribute传递用户ID
                request.setAttribute("userId", userId);
                request.setAttribute("userRole", role);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipAuthentication(String requestPath) {
        return requestPath.equals("/api/users/login") ||
               requestPath.equals("/api/users/register") ||
               requestPath.startsWith("/h2-console") ||
               requestPath.startsWith("/swagger-ui") ||
               requestPath.startsWith("/v3/api-docs") ||
               requestPath.equals("/actuator/health");
    }
}