package com.vision.vision_platform_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import com.vision.vision_platform_backend.model.InferenceHistory;
import com.vision.vision_platform_backend.service.AIInferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AI推理控制器单元测试
 */
@ExtendWith(MockitoExtension.class)
class AIInferenceControllerTest {

    @Mock
    private AIInferenceService aiInferenceService;

    @InjectMocks
    private AIInferenceController aiInferenceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private InferenceHistoryDto testHistoryDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiInferenceController).build();
        objectMapper = new ObjectMapper();
        
        testHistoryDto = new InferenceHistoryDto();
        testHistoryDto.setId(1L);
        testHistoryDto.setTaskId("task-001");
        testHistoryDto.setModelName("yolo-v8");
        testHistoryDto.setImagePath("/images/test.jpg");
        testHistoryDto.setResultPath("/results/test_result.json");
        testHistoryDto.setStatus(InferenceHistory.InferenceStatus.COMPLETED);
        testHistoryDto.setCreatedAt(LocalDateTime.now());
        testHistoryDto.setCompletedAt(LocalDateTime.now());
    }

    @Test
    void testHealthCheck_Success() throws Exception {
        // Given
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("timestamp", "2024-01-01T10:00:00");
        when(aiInferenceService.getHealthStatus()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"));

        verify(aiInferenceService).getHealthStatus();
    }

    @Test
    void testHealthCheck_Unhealthy() throws Exception {
        // Given
        when(aiInferenceService.getHealthStatus()).thenThrow(new RuntimeException("服务不可用"));

        // When & Then
        mockMvc.perform(get("/api/ai-inference/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("unhealthy"))
                .andExpect(jsonPath("$.error").value("服务不可用"));
    }

    @Test
    void testInferSingleImage_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", "task-001");
        result.put("status", "processing");
        when(aiInferenceService.inferSingleImage(any(), eq("yolo-v8"), eq(0.5f))).thenReturn(result);

        // When & Then
        mockMvc.perform(multipart("/api/ai-inference/infer")
                .file(file)
                .param("modelName", "yolo-v8")
                .param("confidence", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value("task-001"))
                .andExpect(jsonPath("$.status").value("processing"));

        verify(aiInferenceService).inferSingleImage(any(), eq("yolo-v8"), eq(0.5f));
    }

    @Test
    void testInferSingleImage_InvalidFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("image", "test.txt", "text/plain", "not an image".getBytes());
        when(aiInferenceService.inferSingleImage(any(), eq("yolo-v8"), eq(0.5f)))
                .thenThrow(new RuntimeException("不支持的文件格式"));

        // When & Then
        mockMvc.perform(multipart("/api/ai-inference/infer")
                .file(file)
                .param("modelName", "yolo-v8")
                .param("confidence", "0.5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("推理失败: 不支持的文件格式"));
    }

    @Test
    void testInferBatchImages_Success() throws Exception {
        // Given
        MockMultipartFile file1 = new MockMultipartFile("images", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("images", "test2.jpg", "image/jpeg", "test image 2".getBytes());
        Map<String, Object> result = new HashMap<>();
        result.put("batchId", "batch-001");
        result.put("totalImages", 2);
        when(aiInferenceService.inferBatchImages(any(), eq("yolo-v8"), eq(0.5f))).thenReturn(result);

        // When & Then
        mockMvc.perform(multipart("/api/ai-inference/infer/batch")
                .file(file1)
                .file(file2)
                .param("modelName", "yolo-v8")
                .param("confidence", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value("batch-001"))
                .andExpect(jsonPath("$.totalImages").value(2));

        verify(aiInferenceService).inferBatchImages(any(), eq("yolo-v8"), eq(0.5f));
    }

    @Test
    void testGetAvailableModels_Success() throws Exception {
        // Given
        List<Map<String, Object>> models = Arrays.asList(
                Map.of("name", "yolo-v8", "version", "1.0", "status", "loaded"),
                Map.of("name", "resnet-50", "version", "2.0", "status", "available")
        );
        when(aiInferenceService.getAvailableModels()).thenReturn(models);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("yolo-v8"))
                .andExpect(jsonPath("$[1].name").value("resnet-50"));

        verify(aiInferenceService).getAvailableModels();
    }

    @Test
    void testGetModelInfo_Success() throws Exception {
        // Given
        Map<String, Object> modelInfo = Map.of(
                "name", "yolo-v8",
                "version", "1.0",
                "description", "YOLO v8 object detection model",
                "status", "loaded"
        );
        when(aiInferenceService.getModelInfo("yolo-v8")).thenReturn(modelInfo);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/models/yolo-v8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("yolo-v8"))
                .andExpect(jsonPath("$.version").value("1.0"))
                .andExpect(jsonPath("$.status").value("loaded"));

        verify(aiInferenceService).getModelInfo("yolo-v8");
    }

    @Test
    void testGetModelInfo_NotFound() throws Exception {
        // Given
        when(aiInferenceService.getModelInfo("unknown-model"))
                .thenThrow(new RuntimeException("模型不存在"));

        // When & Then
        mockMvc.perform(get("/api/ai-inference/models/unknown-model"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("获取模型信息失败: 模型不存在"));
    }

    @Test
    void testGetLoadedModels_Success() throws Exception {
        // Given
        List<Map<String, Object>> models = Arrays.asList(
                Map.of("name", "yolo-v8", "status", "loaded", "loadTime", "2024-01-01T10:00:00")
        );
        when(aiInferenceService.getLoadedModels()).thenReturn(models);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/models/loaded"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("yolo-v8"))
                .andExpect(jsonPath("$[0].status").value("loaded"));

        verify(aiInferenceService).getLoadedModels();
    }

    @Test
    void testLoadModel_Success() throws Exception {
        // Given
        Map<String, Object> result = Map.of("message", "模型加载成功", "modelName", "yolo-v8");
        when(aiInferenceService.loadModel("yolo-v8")).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/ai-inference/models/yolo-v8/load"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("模型加载成功"))
                .andExpect(jsonPath("$.modelName").value("yolo-v8"));

        verify(aiInferenceService).loadModel("yolo-v8");
    }

    @Test
    void testLoadModel_AlreadyLoaded() throws Exception {
        // Given
        when(aiInferenceService.loadModel("yolo-v8"))
                .thenThrow(new RuntimeException("模型已加载"));

        // When & Then
        mockMvc.perform(post("/api/ai-inference/models/yolo-v8/load"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("加载模型失败: 模型已加载"));
    }

    @Test
    void testUnloadModel_Success() throws Exception {
        // Given
        Map<String, Object> result = Map.of("message", "模型卸载成功", "modelName", "yolo-v8");
        when(aiInferenceService.unloadModel("yolo-v8")).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/ai-inference/models/yolo-v8/unload"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("模型卸载成功"))
                .andExpect(jsonPath("$.modelName").value("yolo-v8"));

        verify(aiInferenceService).unloadModel("yolo-v8");
    }

    @Test
    void testGetInferenceStatistics_Success() throws Exception {
        // Given
        Map<String, Object> stats = Map.of(
                "totalInferences", 100,
                "successfulInferences", 95,
                "failedInferences", 5,
                "averageProcessingTime", 2.5
        );
        when(aiInferenceService.getInferenceStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInferences").value(100))
                .andExpect(jsonPath("$.successfulInferences").value(95))
                .andExpect(jsonPath("$.averageProcessingTime").value(2.5));

        verify(aiInferenceService).getInferenceStatistics();
    }

    @Test
    void testResetStatistics_Success() throws Exception {
        // Given
        doNothing().when(aiInferenceService).resetStatistics();

        // When & Then
        mockMvc.perform(post("/api/ai-inference/statistics/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("统计信息重置成功"));

        verify(aiInferenceService).resetStatistics();
    }

    @Test
    void testGetInferenceConfig_Success() throws Exception {
        // Given
        Map<String, Object> config = Map.of(
                "maxConcurrentInferences", 10,
                "defaultConfidence", 0.5,
                "enableGpu", true
        );
        when(aiInferenceService.getInferenceConfig()).thenReturn(config);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxConcurrentInferences").value(10))
                .andExpect(jsonPath("$.defaultConfidence").value(0.5))
                .andExpect(jsonPath("$.enableGpu").value(true));

        verify(aiInferenceService).getInferenceConfig();
    }

    @Test
    void testUpdateInferenceConfig_Success() throws Exception {
        // Given
        Map<String, Object> config = Map.of("maxConcurrentInferences", 15);
        doNothing().when(aiInferenceService).updateInferenceConfig(any());

        // When & Then
        mockMvc.perform(put("/api/ai-inference/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("推理配置更新成功"));

        verify(aiInferenceService).updateInferenceConfig(any());
    }

    @Test
    void testSearchInferenceHistory_Success() throws Exception {
        // Given
        List<InferenceHistoryDto> histories = Arrays.asList(testHistoryDto);
        Page<InferenceHistoryDto> page = new PageImpl<>(histories, PageRequest.of(0, 10), 1);
        when(aiInferenceService.searchInferenceHistory(eq("test"), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/history/search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(aiInferenceService).searchInferenceHistory(eq("test"), any(Pageable.class));
    }

    @Test
    void testGetInferenceHistory_Success() throws Exception {
        // Given
        List<InferenceHistoryDto> histories = Arrays.asList(testHistoryDto);
        Page<InferenceHistoryDto> page = new PageImpl<>(histories, PageRequest.of(0, 10), 1);
        when(aiInferenceService.getInferenceHistory(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/history")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(aiInferenceService).getInferenceHistory(any(Pageable.class));
    }

    @Test
    void testGetInferenceHistoryById_Success() throws Exception {
        // Given
        when(aiInferenceService.getInferenceHistoryById(1L)).thenReturn(testHistoryDto);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskId").value("task-001"));

        verify(aiInferenceService).getInferenceHistoryById(1L);
    }

    @Test
    void testGetInferenceHistoryById_NotFound() throws Exception {
        // Given
        when(aiInferenceService.getInferenceHistoryById(1L))
                .thenThrow(new RuntimeException("推理历史不存在"));

        // When & Then
        mockMvc.perform(get("/api/ai-inference/history/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("获取推理历史失败: 推理历史不存在"));
    }

    @Test
    void testGetInferenceHistoryByTaskId_Success() throws Exception {
        // Given
        when(aiInferenceService.getInferenceHistoryByTaskId("task-001")).thenReturn(testHistoryDto);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/history/task/task-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value("task-001"));

        verify(aiInferenceService).getInferenceHistoryByTaskId("task-001");
    }

    @Test
    void testDeleteInferenceHistory_Success() throws Exception {
        // Given
        doNothing().when(aiInferenceService).deleteInferenceHistory(1L);

        // When & Then
        mockMvc.perform(delete("/api/ai-inference/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("推理历史删除成功"));

        verify(aiInferenceService).deleteInferenceHistory(1L);
    }

    @Test
    void testBatchDeleteInferenceHistory_Success() throws Exception {
        // Given
        doNothing().when(aiInferenceService).batchDeleteInferenceHistory(anyList());

        // When & Then
        mockMvc.perform(delete("/api/ai-inference/history/batch")
                .param("ids", "1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("批量删除推理历史成功"));

        verify(aiInferenceService).batchDeleteInferenceHistory(anyList());
    }

    @Test
    void testGetInferenceHistoryStatistics_Success() throws Exception {
        // Given
        Map<String, Object> stats = Map.of(
                "totalRecords", 100,
                "completedRecords", 95,
                "failedRecords", 5
        );
        when(aiInferenceService.getInferenceHistoryStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/ai-inference/history/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(100))
                .andExpect(jsonPath("$.completedRecords").value(95));

        verify(aiInferenceService).getInferenceHistoryStatistics();
    }

    @Test
    void testCleanupInferenceHistory_Success() throws Exception {
        // Given
        when(aiInferenceService.cleanupInferenceHistory(30)).thenReturn(10);

        // When & Then
        mockMvc.perform(delete("/api/ai-inference/history/cleanup")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("推理历史清理成功"))
                .andExpect(jsonPath("$.deletedCount").value(10));

        verify(aiInferenceService).cleanupInferenceHistory(30);
    }

    @Test
    void testToggleFavorite_Success() throws Exception {
        // Given
        when(aiInferenceService.toggleFavorite(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/ai-inference/history/1/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("收藏状态更新成功"))
                .andExpect(jsonPath("$.isFavorite").value(true));

        verify(aiInferenceService).toggleFavorite(1L);
    }

    @Test
    void testRateInferenceHistory_Success() throws Exception {
        // Given
        doNothing().when(aiInferenceService).rateInferenceHistory(1L, 5);

        // When & Then
        mockMvc.perform(post("/api/ai-inference/history/1/rate")
                .param("rating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("评分成功"));

        verify(aiInferenceService).rateInferenceHistory(1L, 5);
    }

    @Test
    void testAddNote_Success() throws Exception {
        // Given
        doNothing().when(aiInferenceService).addNote(1L, "测试备注");

        // When & Then
        mockMvc.perform(post("/api/ai-inference/history/1/note")
                .param("note", "测试备注"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("备注添加成功"));

        verify(aiInferenceService).addNote(1L, "测试备注");
    }
}