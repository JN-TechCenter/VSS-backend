package com.vision.vision_platform_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import com.vision.vision_platform_backend.service.InferenceHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 推理历史控制器单元测试
 */
@ExtendWith(MockitoExtension.class)
class InferenceHistoryControllerTest {

    @Mock
    private InferenceHistoryService inferenceHistoryService;

    @InjectMocks
    private InferenceHistoryController inferenceHistoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID testId;
    private InferenceHistoryDto.InferenceHistoryResponse testResponse;
    private InferenceHistoryDto.CreateInferenceHistoryRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inferenceHistoryController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // 注册时间模块
        
        testId = UUID.randomUUID();
        
        testResponse = new InferenceHistoryDto.InferenceHistoryResponse();
        testResponse.setId(testId);
        testResponse.setTaskId("task-001");
        testResponse.setInferenceType("OBJECT_DETECTION");
        testResponse.setModelName("yolo-v8");
        testResponse.setImagePath("/images/test.jpg");
        testResponse.setResultPath("/results/test_result.json");
        testResponse.setStatus("COMPLETED");
        testResponse.setCreatedAt(LocalDateTime.now());
        testResponse.setCompletedAt(LocalDateTime.now());
        
        testCreateRequest = new InferenceHistoryDto.CreateInferenceHistoryRequest();
        testCreateRequest.setTaskId("task-001");
        testCreateRequest.setInferenceType("OBJECT_DETECTION");
        testCreateRequest.setModelName("yolo-v8");
        testCreateRequest.setImagePath("/images/test.jpg");
    }

    @Test
    void testCreateInferenceHistory_Success() throws Exception {
        // Given
        when(inferenceHistoryService.createInferenceHistory(any(InferenceHistoryDto.CreateInferenceHistoryRequest.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/inference-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("推理历史记录创建成功"))
                .andExpect(jsonPath("$.data.taskId").value("task-001"));

        verify(inferenceHistoryService).createInferenceHistory(any(InferenceHistoryDto.CreateInferenceHistoryRequest.class));
    }

    @Test
    void testCreateInferenceHistory_Failure() throws Exception {
        // Given
        when(inferenceHistoryService.createInferenceHistory(any(InferenceHistoryDto.CreateInferenceHistoryRequest.class)))
                .thenThrow(new RuntimeException("任务ID已存在"));

        // When & Then
        mockMvc.perform(post("/api/inference-history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("创建推理历史记录失败: 任务ID已存在"));
    }

    @Test
    void testGetInferenceHistoryById_Success() throws Exception {
        // Given
        when(inferenceHistoryService.getInferenceHistoryById(testId)).thenReturn(Optional.of(testResponse));

        // When & Then
        mockMvc.perform(get("/api/inference-history/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取推理历史记录成功"))
                .andExpect(jsonPath("$.data.id").value(testId.toString()));

        verify(inferenceHistoryService).getInferenceHistoryById(testId);
    }

    @Test
    void testGetInferenceHistoryById_NotFound() throws Exception {
        // Given
        when(inferenceHistoryService.getInferenceHistoryById(testId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/inference-history/{id}", testId))
                .andExpect(status().isNotFound());

        verify(inferenceHistoryService).getInferenceHistoryById(testId);
    }

    @Test
    void testGetInferenceHistoryById_Exception() throws Exception {
        // Given
        when(inferenceHistoryService.getInferenceHistoryById(testId))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // When & Then
        mockMvc.perform(get("/api/inference-history/{id}", testId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取推理历史记录失败: 数据库连接失败"));
    }

    @Test
    void testGetInferenceHistoryByTaskId_Success() throws Exception {
        // Given
        when(inferenceHistoryService.getInferenceHistoryByTaskId("task-001")).thenReturn(Optional.of(testResponse));

        // When & Then
        mockMvc.perform(get("/api/inference-history/task/task-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取推理历史记录成功"))
                .andExpect(jsonPath("$.data.taskId").value("task-001"));

        verify(inferenceHistoryService).getInferenceHistoryByTaskId("task-001");
    }

    @Test
    void testGetInferenceHistoryByTaskId_NotFound() throws Exception {
        // Given
        when(inferenceHistoryService.getInferenceHistoryByTaskId("task-001")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/inference-history/task/task-001"))
                .andExpect(status().isNotFound());

        verify(inferenceHistoryService).getInferenceHistoryByTaskId("task-001");
    }

    @Test
    void testSearchInferenceHistory_Success() throws Exception {
        // Given
        InferenceHistoryDto.SearchInferenceHistoryRequest searchRequest = 
                new InferenceHistoryDto.SearchInferenceHistoryRequest();
        searchRequest.setKeyword("test");
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        InferenceHistoryDto.InferenceHistoryPageResponse pageResponse = 
                new InferenceHistoryDto.InferenceHistoryPageResponse();
        pageResponse.setContent(Arrays.asList(testResponse));
        pageResponse.setTotalElements(1L);
        pageResponse.setTotalPages(1);

        when(inferenceHistoryService.searchInferenceHistory(any(InferenceHistoryDto.SearchInferenceHistoryRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(post("/api/inference-history/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜索推理历史记录成功"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(inferenceHistoryService).searchInferenceHistory(any(InferenceHistoryDto.SearchInferenceHistoryRequest.class));
    }

    @Test
    void testGetInferenceHistoryList_Success() throws Exception {
        // Given
        InferenceHistoryDto.InferenceHistoryPageResponse pageResponse = 
                new InferenceHistoryDto.InferenceHistoryPageResponse();
        pageResponse.setContent(Arrays.asList(testResponse));
        pageResponse.setTotalElements(1L);
        pageResponse.setTotalPages(1);

        when(inferenceHistoryService.searchInferenceHistory(any(InferenceHistoryDto.SearchInferenceHistoryRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/inference-history")
                .param("page", "0")
                .param("size", "20")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取推理历史记录列表成功"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(inferenceHistoryService).searchInferenceHistory(any(InferenceHistoryDto.SearchInferenceHistoryRequest.class));
    }

    @Test
    void testUpdateInferenceHistory_Success() throws Exception {
        // Given
        InferenceHistoryDto.UpdateInferenceHistoryRequest updateRequest = 
                new InferenceHistoryDto.UpdateInferenceHistoryRequest();
        updateRequest.setStatus("COMPLETED");
        updateRequest.setResultPath("/results/updated_result.json");

        when(inferenceHistoryService.updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新推理历史记录成功"))
                .andExpect(jsonPath("$.data.id").value(testId.toString()));

        verify(inferenceHistoryService).updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class));
    }

    @Test
    void testUpdateInferenceHistory_NotFound() throws Exception {
        // Given
        InferenceHistoryDto.UpdateInferenceHistoryRequest updateRequest = 
                new InferenceHistoryDto.UpdateInferenceHistoryRequest();
        
        when(inferenceHistoryService.updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class)))
                .thenThrow(new RuntimeException("推理历史记录不存在"));

        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("更新推理历史记录失败: 推理历史记录不存在"));
    }

    @Test
    void testDeleteInferenceHistory_Success() throws Exception {
        // Given
        doNothing().when(inferenceHistoryService).deleteInferenceHistory(testId);

        // When & Then
        mockMvc.perform(delete("/api/inference-history/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("删除推理历史记录成功"));

        verify(inferenceHistoryService).deleteInferenceHistory(testId);
    }

    @Test
    void testDeleteInferenceHistory_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("推理历史记录不存在")).when(inferenceHistoryService).deleteInferenceHistory(testId);

        // When & Then
        mockMvc.perform(delete("/api/inference-history/{id}", testId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除推理历史记录失败: 推理历史记录不存在"));
    }

    @Test
    void testBatchDeleteInferenceHistory_Success() throws Exception {
        // Given
        List<UUID> ids = Arrays.asList(testId, UUID.randomUUID());
        doNothing().when(inferenceHistoryService).batchDeleteInferenceHistory(anyList());

        // When & Then
        mockMvc.perform(delete("/api/inference-history/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("批量删除推理历史记录成功"));

        verify(inferenceHistoryService).batchDeleteInferenceHistory(anyList());
    }

    @Test
    void testGetInferenceHistoryStats_Success() throws Exception {
        // Given
        InferenceHistoryDto.InferenceHistoryStats stats = new InferenceHistoryDto.InferenceHistoryStats();
        stats.setTotalCount(100L);
        stats.setCompletedCount(95L);
        stats.setFailedCount(5L);
        stats.setAverageProcessingTime(2.5);

        when(inferenceHistoryService.getInferenceHistoryStats(any())).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/inference-history/stats")
                .param("userId", testId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取推理历史统计成功"))
                .andExpect(jsonPath("$.data.totalCount").value(100))
                .andExpect(jsonPath("$.data.completedCount").value(95));

        verify(inferenceHistoryService).getInferenceHistoryStats(any());
    }

    @Test
    void testCleanupInferenceHistory_Success() throws Exception {
        // Given
        InferenceHistoryDto.CleanupInferenceHistoryRequest cleanupRequest = 
                new InferenceHistoryDto.CleanupInferenceHistoryRequest();
        cleanupRequest.setDaysToKeep(30);
        cleanupRequest.setKeepFavorites(true);

        doNothing().when(inferenceHistoryService).cleanupInferenceHistory(any(InferenceHistoryDto.CleanupInferenceHistoryRequest.class));

        // When & Then
        mockMvc.perform(post("/api/inference-history/cleanup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cleanupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("清理推理历史记录成功"));

        verify(inferenceHistoryService).cleanupInferenceHistory(any(InferenceHistoryDto.CleanupInferenceHistoryRequest.class));
    }

    @Test
    void testToggleFavorite_Success() throws Exception {
        // Given
        when(inferenceHistoryService.updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}/favorite", testId)
                .param("favorite", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("添加收藏成功"));

        verify(inferenceHistoryService).updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class));
    }

    @Test
    void testToggleFavorite_RemoveFavorite() throws Exception {
        // Given
        when(inferenceHistoryService.updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}/favorite", testId)
                .param("favorite", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("取消收藏成功"));

        verify(inferenceHistoryService).updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class));
    }

    @Test
    void testRateInferenceResult_Success() throws Exception {
        // Given
        when(inferenceHistoryService.updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}/rating", testId)
                .param("rating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("评分成功"));

        verify(inferenceHistoryService).updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class));
    }

    @Test
    void testRateInferenceResult_InvalidRating() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}/rating", testId)
                .param("rating", "6"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("评分必须在1-5之间"));

        verify(inferenceHistoryService, never()).updateInferenceHistory(any(), any());
    }

    @Test
    void testAddNotes_Success() throws Exception {
        // Given
        when(inferenceHistoryService.updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/inference-history/{id}/notes", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"notes\": \"这是一个测试备注\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("添加备注成功"));

        verify(inferenceHistoryService).updateInferenceHistory(eq(testId), any(InferenceHistoryDto.UpdateInferenceHistoryRequest.class));
    }

    @Test
    void testGetRecentInferenceHistory_Success() throws Exception {
        // Given
        InferenceHistoryDto.InferenceHistoryPageResponse pageResponse = 
                new InferenceHistoryDto.InferenceHistoryPageResponse();
        pageResponse.setContent(Arrays.asList(testResponse));
        pageResponse.setTotalElements(1L);

        when(inferenceHistoryService.searchInferenceHistory(any(InferenceHistoryDto.SearchInferenceHistoryRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/inference-history/recent")
                .param("limit", "10")
                .param("userId", testId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取最近推理记录成功"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(inferenceHistoryService).searchInferenceHistory(any(InferenceHistoryDto.SearchInferenceHistoryRequest.class));
    }
}