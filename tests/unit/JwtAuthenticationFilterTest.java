package com.vision.vision_platform_backend.security;

import com.vision.vision_platform_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JWT认证过滤器单元测试
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String validToken;
    private String invalidToken;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        validToken = "valid.jwt.token";
        invalidToken = "invalid.jwt.token";
        testUserId = UUID.randomUUID();
    }

    @Test
    void testDoFilterInternal_SkipAuthentication_LoginPath() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/login");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_SkipAuthentication_RegisterPath() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/register");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_SkipAuthentication_H2Console() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/h2-console/login");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_SkipAuthentication_SwaggerUI() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_SkipAuthentication_ApiDocs() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/v3/api-docs");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_SkipAuthentication_HealthCheck() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Basic invalid");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ValidToken_Success() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(jwtUtil.validateToken(validToken, "testuser")).thenReturn(true);
        when(jwtUtil.getUserRoleFromToken(validToken)).thenReturn("ADMIN");
        when(jwtUtil.getUserIdFromToken(validToken)).thenReturn(testUserId);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(request).setAttribute("userId", testUserId);
        verify(request).setAttribute("userRole", "ADMIN");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testDoFilterInternal_ValidToken_InvalidValidation() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(jwtUtil.validateToken(validToken, "testuser")).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).getUserRoleFromToken(anyString());
        verify(jwtUtil, never()).getUserIdFromToken(any());
        verify(request, never()).setAttribute(anyString(), any());
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_TokenParsingException() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtil.getUsernameFromToken(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ExistingAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("testuser");
        
        // 设置已存在的认证
        SecurityContextHolder.getContext().setAuthentication(
                mock(Authentication.class));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(jwtUtil, never()).getUserRoleFromToken(anyString());
    }

    @Test
    void testDoFilterInternal_UserRole() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(jwtUtil.validateToken(validToken, "testuser")).thenReturn(true);
        when(jwtUtil.getUserRoleFromToken(validToken)).thenReturn("USER");
        when(jwtUtil.getUserIdFromToken(validToken)).thenReturn(testUserId);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(request).setAttribute("userId", testUserId);
        verify(request).setAttribute("userRole", "USER");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testDoFilterInternal_OperatorRole() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("operator");
        when(jwtUtil.validateToken(validToken, "operator")).thenReturn(true);
        when(jwtUtil.getUserRoleFromToken(validToken)).thenReturn("OPERATOR");
        when(jwtUtil.getUserIdFromToken(validToken)).thenReturn(testUserId);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(request).setAttribute("userId", testUserId);
        verify(request).setAttribute("userRole", "OPERATOR");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("operator", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OPERATOR")));
    }
}