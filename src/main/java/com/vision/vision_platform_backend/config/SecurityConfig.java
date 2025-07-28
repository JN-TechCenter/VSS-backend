package com.vision.vision_platform_backend.config;

import com.vision.vision_platform_backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:Content-Type,Authorization,X-Requested-With}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 从配置文件读取允许的源
        String[] origins = allowedOrigins.split(",");
        for (String origin : origins) {
            origin = origin.trim();
            if (!origin.isEmpty()) {
                configuration.addAllowedOriginPattern(origin);
            }
        }
        
        // 从配置文件读取允许的方法
        String[] methods = allowedMethods.split(",");
        for (String method : methods) {
            method = method.trim();
            if (!method.isEmpty()) {
                configuration.addAllowedMethod(method);
            }
        }
        
        // 从配置文件读取允许的头部
        String[] headers = allowedHeaders.split(",");
        for (String header : headers) {
            header = header.trim();
            if (!header.isEmpty()) {
                configuration.addAllowedHeader(header);
            }
        }
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization"
        ));
        
        // 设置是否允许凭证
        configuration.setAllowCredentials(allowCredentials);
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);
        
        // 调试输出
        System.out.println("=== CORS Configuration Debug ===");
        System.out.println("Raw allowedOrigins: " + allowedOrigins);
        System.out.println("Raw allowedMethods: " + allowedMethods);
        System.out.println("Raw allowedHeaders: " + allowedHeaders);
        System.out.println("allowCredentials: " + allowCredentials);
        for (int i = 0; i < origins.length; i++) {
            System.out.println("Origin " + i + ": '" + origins[i].trim() + "'");
        }
        System.out.println("=== CORS Configuration Complete ===");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（因为使用JWT）
            .csrf(csrf -> csrf.disable())
            
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置会话管理为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // 首先配置最具体的公开端点（注册和登录必须在前面）
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/users/login").permitAll()
                
                // 其他公开访问的端点
                .requestMatchers(
                    "/api/users/roles",
                    "/api/devices/*/heartbeat",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                
                // 所有认证用户可访问的端点（具体路径）
                .requestMatchers("/api/users/profile").authenticated()
                .requestMatchers("/api/users/change-password").authenticated()
                .requestMatchers("/api/dashboard/**").authenticated()
                .requestMatchers("/api/analytics/**").authenticated()
                
                // 管理员权限端点（具体路径）
                .requestMatchers(
                    "/api/users/search",
                    "/api/users/{id}",
                    "/api/users/{id}/reset-password",
                    "/api/users/{id}/toggle-status",
                    "/api/users/{id}/unlock",
                    "/api/admin/**"
                ).hasRole("ADMIN")
                
                // 管理员权限端点（/api/users 基础路径，放在最后避免覆盖具体路径）
                .requestMatchers("/api/users").hasRole("ADMIN")
                
                // 管理员和操作员权限端点
                .requestMatchers(
                    "/api/devices/**",
                    "/api/streams/**"
                ).hasAnyRole("ADMIN", "OPERATOR")
                
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置异常处理
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"未授权访问\",\"message\":\"" + authException.getMessage() + "\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"访问被拒绝\",\"message\":\"权限不足\"}");
                })
            );

        // 允许H2控制台的frame
        http.headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
}