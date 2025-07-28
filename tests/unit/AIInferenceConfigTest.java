package com.vision.vision_platform_backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AI推理配置类单元测试
 */
@ExtendWith(MockitoExtension.class)
class AIInferenceConfigTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private AIInferenceConfig aiInferenceConfig;

    @BeforeEach
    void setUp() {
        aiInferenceConfig = new AIInferenceConfig();
    }

    @Test
    void testRestTemplate_Configuration() {
        // Given
        when(restTemplateBuilder.setConnectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // When
        RestTemplate result = aiInferenceConfig.restTemplate(restTemplateBuilder);

        // Then
        assertNotNull(result);
        assertEquals(restTemplate, result);
        
        // 验证超时配置
        verify(restTemplateBuilder).setConnectTimeout(Duration.ofSeconds(10));
        verify(restTemplateBuilder).setReadTimeout(Duration.ofSeconds(30));
        verify(restTemplateBuilder).build();
    }

    @Test
    void testRestTemplate_WithNullBuilder() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            aiInferenceConfig.restTemplate(null);
        });
    }

    @Test
    void testRestTemplate_BuilderChaining() {
        // Given
        RestTemplateBuilder mockBuilder = mock(RestTemplateBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(restTemplate);

        // When
        RestTemplate result = aiInferenceConfig.restTemplate(mockBuilder);

        // Then
        assertNotNull(result);
        assertEquals(restTemplate, result);
        
        // 验证方法调用顺序
        verify(mockBuilder).setConnectTimeout(Duration.ofSeconds(10));
        verify(mockBuilder).setReadTimeout(Duration.ofSeconds(30));
        verify(mockBuilder).build();
    }

    @Test
    void testRestTemplate_TimeoutValues() {
        // Given
        RestTemplateBuilder realBuilder = new RestTemplateBuilder();

        // When
        RestTemplate result = aiInferenceConfig.restTemplate(realBuilder);

        // Then
        assertNotNull(result);
        // 验证RestTemplate实例被正确创建
        assertTrue(result instanceof RestTemplate);
    }
}