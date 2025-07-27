package com.vision.vision_platform_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA审计配置，支持@CreatedDate和@LastModifiedDate注解
}