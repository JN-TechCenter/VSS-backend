package com.vision.vision_platform_backend.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JPA配置类单元测试
 */
@ExtendWith(MockitoExtension.class)
class JpaConfigTest {

    @Test
    void testJpaConfigAnnotations() {
        // Given
        JpaConfig jpaConfig = new JpaConfig();

        // When & Then
        assertNotNull(jpaConfig);
        
        // 验证类上的注解
        assertTrue(JpaConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(JpaConfig.class.isAnnotationPresent(EnableJpaAuditing.class));
    }

    @Test
    void testJpaConfigInstantiation() {
        // When
        JpaConfig jpaConfig = new JpaConfig();

        // Then
        assertNotNull(jpaConfig);
        assertEquals(JpaConfig.class, jpaConfig.getClass());
    }
}