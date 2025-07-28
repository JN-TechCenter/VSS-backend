package com.vision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.vision.vision_platform_backend.repository")
@EntityScan(basePackages = {"com.vision.vision_platform_backend.model", "com.vision.vision_platform_backend.entity"})
public class VisionPlatformBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisionPlatformBackendApplication.class, args);
    }
}
