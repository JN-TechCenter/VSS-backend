package com.vision.vision_platform_backend.config;

import com.vision.vision_platform_backend.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 安全配置类单元测试
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "jwtAuthenticationFilter", jwtAuthenticationFilter);
        
        // 设置默认配置值
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", "http://localhost:3000");
        ReflectionTestUtils.setField(securityConfig, "allowedMethods", "GET,POST,PUT,DELETE,OPTIONS");
        ReflectionTestUtils.setField(securityConfig, "allowedHeaders", "Content-Type,Authorization,X-Requested-With");
        ReflectionTestUtils.setField(securityConfig, "allowCredentials", true);
    }

    @Test
    void testPasswordEncoder() {
        // When
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Then
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testPasswordEncoder_EncodePassword() {
        // Given
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testAuthenticationManager() throws Exception {
        // Given
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // When
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Then
        assertNotNull(result);
        assertEquals(authenticationManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void testCorsConfigurationSource_DefaultValues() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        
        // 验证默认配置
        assertTrue(corsConfiguration.getAllowCredentials());
        assertEquals(3600L, corsConfiguration.getMaxAge());
        assertNotNull(corsConfiguration.getAllowedOriginPatterns());
        assertNotNull(corsConfiguration.getAllowedMethods());
        assertNotNull(corsConfiguration.getAllowedHeaders());
    }

    @Test
    void testCorsConfigurationSource_CustomOrigins() {
        // Given
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", "http://localhost:3000,https://example.com");

        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        assertTrue(corsConfiguration.getAllowedOriginPatterns().size() >= 2);
    }

    @Test
    void testCorsConfigurationSource_CustomMethods() {
        // Given
        ReflectionTestUtils.setField(securityConfig, "allowedMethods", "GET,POST");

        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        assertTrue(corsConfiguration.getAllowedMethods().contains("GET"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("POST"));
    }

    @Test
    void testCorsConfigurationSource_CustomHeaders() {
        // Given
        ReflectionTestUtils.setField(securityConfig, "allowedHeaders", "Content-Type,Authorization");

        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Content-Type"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Authorization"));
    }

    @Test
    void testCorsConfigurationSource_DisableCredentials() {
        // Given
        ReflectionTestUtils.setField(securityConfig, "allowCredentials", false);

        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        assertFalse(corsConfiguration.getAllowCredentials());
    }

    @Test
    void testCorsConfigurationSource_EmptyOrigins() {
        // Given
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", "");

        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        // 空字符串应该被忽略
        assertTrue(corsConfiguration.getAllowedOriginPatterns().isEmpty());
    }

    @Test
    void testCorsConfigurationSource_WhitespaceHandling() {
        // Given
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", " http://localhost:3000 , https://example.com ");
        ReflectionTestUtils.setField(securityConfig, "allowedMethods", " GET , POST ");
        ReflectionTestUtils.setField(securityConfig, "allowedHeaders", " Content-Type , Authorization ");

        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        
        // 验证空格被正确处理
        assertTrue(corsConfiguration.getAllowedOriginPatterns().contains("http://localhost:3000"));
        assertTrue(corsConfiguration.getAllowedOriginPatterns().contains("https://example.com"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("GET"));
        assertTrue(corsConfiguration.getAllowedMethods().contains("POST"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Content-Type"));
        assertTrue(corsConfiguration.getAllowedHeaders().contains("Authorization"));
    }

    @Test
    void testCorsConfigurationSource_ExposedHeaders() {
        // When
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Then
        assertNotNull(corsConfigurationSource);
        
        CorsConfiguration corsConfiguration = corsConfigurationSource.getCorsConfiguration("/**");
        assertNotNull(corsConfiguration);
        
        // 验证暴露的响应头
        assertTrue(corsConfiguration.getExposedHeaders().contains("Access-Control-Allow-Origin"));
        assertTrue(corsConfiguration.getExposedHeaders().contains("Access-Control-Allow-Credentials"));
        assertTrue(corsConfiguration.getExposedHeaders().contains("Authorization"));
    }

    @Test
    void testSecurityConfigAnnotations() {
        // Then
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class));
    }

    @Test
    void testSecurityConfigInstantiation() {
        // When
        SecurityConfig config = new SecurityConfig();

        // Then
        assertNotNull(config);
        assertEquals(SecurityConfig.class, config.getClass());
    }
}