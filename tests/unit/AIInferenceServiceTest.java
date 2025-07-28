package com.vision.vision_platform_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.AIInferenceDto;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIInferenceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InferenceHistoryService inferenceHistoryService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AIInferenceService aiInferenceService;

    private AIInferenceDto.InferenceRequest testRequest;
    private AIInferenceDto.InferenceResponse testResponse;

    @BeforeEach
    void setUp() {
        // 设置配置值
        ReflectionTestUtils.setField(aiInferenceService, "inferenceServerUrl", "http://localhost:8000");
        ReflectionTestUtils.setField(aiInferenceService, "timeout", 30000);

        // 创建测试请求
        testRequest = new AIInferenceDto.InferenceRequest();
        testRequest.setTask("detect");
        testRequest.setModelName("test_model");
        testRequest.setImageData("base64_image_data");

        // 创建测试响应
        testResponse = new AIInferenceDto.InferenceResponse();
        testResponse.setSuccess(true);
        testResponse.setTask("detect");
        testResponse.setModelName("test_model");
        testResponse.setTimestamp(LocalDateTime.now());
        testResponse.setInferenceTime(150.0);

        // 设置安全上下文
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCheckHealth_Success() {
        // Given
        ResponseEntity<Map> response = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.getForEntity("http://localhost:8000/health", Map.class)).thenReturn(response);

        // When
        boolean result = aiInferenceService.checkHealth();

        // Then
        assertTrue(result);
        verify(restTemplate).getForEntity("http://localhost:8000/health", Map.class);
    }

    @Test
    void testCheckHealth_Failure() {
        // Given
        when(restTemplate.getForEntity("http://localhost:8000/health", Map.class))
            .thenThrow(new RuntimeException("Connection failed"));

        // When
        boolean result = aiInferenceService.checkHealth();

        // Then
        assertFalse(result);
    }

    @Test
    void testGetHealthStatus_Success() {
        // Given
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "healthy");
        ResponseEntity<Map> response = new ResponseEntity<>(healthData, HttpStatus.OK);
        when(restTemplate.getForEntity("http://localhost:8000/health", Map.class)).thenReturn(response);

        // When
        Map<String, Object> result = aiInferenceService.getHealthStatus();

        // Then
        assertNotNull(result);
        assertEquals("healthy", result.get("status"));
    }

    @Test
    void testGetHealthStatus_Failure() {
        // Given
        when(restTemplate.getForEntity("http://localhost:8000/health", Map.class))
            .thenThrow(new RuntimeException("Connection failed"));

        // When
        Map<String, Object> result = aiInferenceService.getHealthStatus();

        // Then
        assertNotNull(result);
        assertEquals("unhealthy", result.get("status"));
        assertEquals("Connection failed", result.get("error"));
    }

    @Test
    void testInference_Success() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("{\"success\":true}", HttpStatus.OK);
        when(restTemplate.postForEntity(eq("http://localhost:8000/inference"), any(HttpEntity.class), eq(String.class)))
            .thenReturn(response);
        when(objectMapper.readValue("{\"success\":true}", AIInferenceDto.InferenceResponse.class))
            .thenReturn(testResponse);

        // When
        AIInferenceDto.InferenceResponse result = aiInferenceService.inference(testRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("detect", result.getTask());
        verify(inferenceHistoryService).createInferenceHistory(any());
    }

    @Test
    void testInference_ServerError() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.postForEntity(eq("http://localhost:8000/inference"), any(HttpEntity.class), eq(String.class)))
            .thenReturn(response);

        // When
        AIInferenceDto.InferenceResponse result = aiInferenceService.inference(testRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        verify(inferenceHistoryService).createInferenceHistory(any());
    }

    @Test
    void testInference_ConnectionError() {
        // Given
        when(restTemplate.postForEntity(eq("http://localhost:8000/inference"), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new ResourceAccessException("Connection timeout"));

        // When
        AIInferenceDto.InferenceResponse result = aiInferenceService.inference(testRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        verify(inferenceHistoryService).createInferenceHistory(any());
    }

    @Test
    void testInferSingle_Success() {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getSize()).thenReturn(1024L);

        // When
        AIInferenceDto.InferenceResponse result = aiInferenceService.inferSingle(multipartFile, testRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("detect", result.getTask());
        assertNotNull(result.getDetections());
        assertEquals(2, result.getDetections().size());
        verify(inferenceHistoryService).createInferenceHistory(any());
    }

    @Test
    void testInferSingle_Exception() {
        // Given
        when(multipartFile.getOriginalFilename()).thenThrow(new RuntimeException("File error"));

        // When
        AIInferenceDto.InferenceResponse result = aiInferenceService.inferSingle(multipartFile, testRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        verify(inferenceHistoryService).createInferenceHistory(any());
    }

    @Test
    void testInferBatch_Success() {
        // Given
        List<MultipartFile> files = Arrays.asList(multipartFile, multipartFile);
        AIInferenceDto.BatchInferenceRequest batchRequest = new AIInferenceDto.BatchInferenceRequest();
        batchRequest.setTask("detect");
        batchRequest.setModelName("test_model");

        when(multipartFile.getOriginalFilename()).thenReturn("test1.jpg", "test2.jpg");
        when(multipartFile.getSize()).thenReturn(1024L);

        // When
        Map<String, Object> result = aiInferenceService.inferBatch(files, batchRequest);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals(2, result.get("total_files"));
        assertEquals(2, result.get("processed_files"));
        assertEquals(0, result.get("failed_files"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("results");
        assertEquals(2, results.size());
        
        verify(inferenceHistoryService, times(3)).createInferenceHistory(any()); // 2个文件 + 1个批量记录
    }

    @Test
    void testBatchInference_Success() throws Exception {
        // Given
        AIInferenceDto.BatchInferenceRequest batchRequest = new AIInferenceDto.BatchInferenceRequest();
        ResponseEntity<String> response = new ResponseEntity<>("[{\"success\":true}]", HttpStatus.OK);
        List<AIInferenceDto.InferenceResponse> expectedResponses = Arrays.asList(testResponse);

        when(restTemplate.postForEntity(eq("http://localhost:8000/batch_inference"), any(HttpEntity.class), eq(String.class)))
            .thenReturn(response);
        when(objectMapper.readValue(eq("[{\"success\":true}]"), any(TypeReference.class)))
            .thenReturn(expectedResponses);

        // When
        List<AIInferenceDto.InferenceResponse> result = aiInferenceService.batchInference(batchRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isSuccess());
    }

    @Test
    void testBatchInference_Failure() {
        // Given
        AIInferenceDto.BatchInferenceRequest batchRequest = new AIInferenceDto.BatchInferenceRequest();
        when(restTemplate.postForEntity(eq("http://localhost:8000/batch_inference"), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("Server error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> aiInferenceService.batchInference(batchRequest));
        assertTrue(exception.getMessage().contains("批量推理过程中发生错误"));
    }

    @Test
    void testGetModels_Success() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("[{\"name\":\"model1\"}]", HttpStatus.OK);
        List<AIInferenceDto.ModelInfo> expectedModels = Arrays.asList(new AIInferenceDto.ModelInfo());

        when(restTemplate.getForEntity("http://localhost:8000/models", String.class)).thenReturn(response);
        when(objectMapper.readValue(eq("[{\"name\":\"model1\"}]"), any(TypeReference.class)))
            .thenReturn(expectedModels);

        // When
        List<AIInferenceDto.ModelInfo> result = aiInferenceService.getModels();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetModelInfo_Success() throws Exception {
        // Given
        AIInferenceDto.ModelInfo modelInfo = new AIInferenceDto.ModelInfo();
        ResponseEntity<String> response = new ResponseEntity<>("{\"name\":\"test_model\"}", HttpStatus.OK);

        when(restTemplate.getForEntity("http://localhost:8000/models/test_model", String.class)).thenReturn(response);
        when(objectMapper.readValue("{\"name\":\"test_model\"}", AIInferenceDto.ModelInfo.class))
            .thenReturn(modelInfo);

        // When
        AIInferenceDto.ModelInfo result = aiInferenceService.getModelInfo("test_model");

        // Then
        assertNotNull(result);
    }

    @Test
    void testLoadModel_Success() {
        // Given
        ResponseEntity<Map> response = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.postForEntity(eq("http://localhost:8000/models/load"), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        // When
        boolean result = aiInferenceService.loadModel("test_model");

        // Then
        assertTrue(result);
    }

    @Test
    void testLoadModel_Failure() {
        // Given
        when(restTemplate.postForEntity(eq("http://localhost:8000/models/load"), any(HttpEntity.class), eq(Map.class)))
            .thenThrow(new RuntimeException("Load failed"));

        // When
        boolean result = aiInferenceService.loadModel("test_model");

        // Then
        assertFalse(result);
    }

    @Test
    void testUnloadModel_Success() {
        // Given
        ResponseEntity<Map> response = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.postForEntity(eq("http://localhost:8000/models/unload"), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(response);

        // When
        boolean result = aiInferenceService.unloadModel("test_model");

        // Then
        assertTrue(result);
    }

    @Test
    void testGetLoadedModels_Success() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("[{\"name\":\"loaded_model\"}]", HttpStatus.OK);
        List<AIInferenceDto.ModelInfo> expectedModels = Arrays.asList(new AIInferenceDto.ModelInfo());

        when(restTemplate.getForEntity("http://localhost:8000/models/loaded", String.class)).thenReturn(response);
        when(objectMapper.readValue(eq("[{\"name\":\"loaded_model\"}]"), any(TypeReference.class)))
            .thenReturn(expectedModels);

        // When
        List<AIInferenceDto.ModelInfo> result = aiInferenceService.getLoadedModels();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetStats_Success() throws Exception {
        // Given
        AIInferenceDto.InferenceStats stats = new AIInferenceDto.InferenceStats();
        ResponseEntity<String> response = new ResponseEntity<>("{\"total\":100}", HttpStatus.OK);

        when(restTemplate.getForEntity("http://localhost:8000/stats", String.class)).thenReturn(response);
        when(objectMapper.readValue("{\"total\":100}", AIInferenceDto.InferenceStats.class))
            .thenReturn(stats);

        // When
        AIInferenceDto.InferenceStats result = aiInferenceService.getStats();

        // Then
        assertNotNull(result);
    }

    @Test
    void testGetConfig_Success() throws Exception {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("max_batch_size", 10);
        ResponseEntity<String> response = new ResponseEntity<>("{\"max_batch_size\":10}", HttpStatus.OK);

        when(restTemplate.getForEntity("http://localhost:8000/config", String.class)).thenReturn(response);
        when(objectMapper.readValue(eq("{\"max_batch_size\":10}"), any(TypeReference.class)))
            .thenReturn(config);

        // When
        Map<String, Object> result = aiInferenceService.getConfig();

        // Then
        assertNotNull(result);
        assertEquals(10, result.get("max_batch_size"));
    }

    @Test
    void testUpdateConfig_Success() {
        // Given
        AIInferenceDto.ConfigUpdateRequest configRequest = new AIInferenceDto.ConfigUpdateRequest();
        ResponseEntity<Map> response = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);

        when(restTemplate.exchange(eq("http://localhost:8000/config"), eq(HttpMethod.PUT), 
            any(HttpEntity.class), eq(Map.class))).thenReturn(response);

        // When
        boolean result = aiInferenceService.updateConfig(configRequest);

        // Then
        assertTrue(result);
    }

    @Test
    void testResetInferenceStats_Success() {
        // Given
        ResponseEntity<Map> response = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.postForEntity("http://localhost:8000/stats/reset", null, Map.class))
            .thenReturn(response);

        // When
        boolean result = aiInferenceService.resetInferenceStats();

        // Then
        assertTrue(result);
    }

    @Test
    void testResetInferenceStats_Failure() {
        // Given
        when(restTemplate.postForEntity("http://localhost:8000/stats/reset", null, Map.class))
            .thenThrow(new RuntimeException("Reset failed"));

        // When
        boolean result = aiInferenceService.resetInferenceStats();

        // Then
        assertFalse(result);
    }

    @Test
    void testGetInferenceStats() throws Exception {
        // Given
        AIInferenceDto.InferenceStats stats = new AIInferenceDto.InferenceStats();
        ResponseEntity<String> response = new ResponseEntity<>("{\"total\":100}", HttpStatus.OK);

        when(restTemplate.getForEntity("http://localhost:8000/stats", String.class)).thenReturn(response);
        when(objectMapper.readValue("{\"total\":100}", AIInferenceDto.InferenceStats.class))
            .thenReturn(stats);

        // When
        AIInferenceDto.InferenceStats result = aiInferenceService.getInferenceStats();

        // Then
        assertNotNull(result);
        // 验证它调用了getStats方法
        verify(restTemplate).getForEntity("http://localhost:8000/stats", String.class);
    }
}