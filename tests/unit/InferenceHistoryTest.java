package com.vision.vision_platform_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InferenceHistory实体类单元测试
 */
@ExtendWith(MockitoExtension.class)
class InferenceHistoryTest {

    private InferenceHistory inferenceHistory;

    @BeforeEach
    void setUp() {
        inferenceHistory = new InferenceHistory();
    }

    @Test
    void testDefaultConstructor() {
        // When
        InferenceHistory history = new InferenceHistory();

        // Then
        assertNotNull(history);
        assertNull(history.getId());
        assertNull(history.getTaskId());
        assertNull(history.getInferenceType());
        assertNull(history.getModelName());
        assertFalse(history.getIsDeleted()); // 默认值为false
        assertFalse(history.getIsFavorite()); // 默认值为false
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long id = 1L;
        String taskId = "task-123";
        String inferenceType = "single";
        String modelName = "yolo-v8";
        Double confidenceThreshold = 0.8;
        String originalFilename = "test.jpg";
        Long fileSize = 1024L;
        String imagePath = "/images/test.jpg";
        String inferenceResult = "{\"objects\": []}";
        Integer detectedObjectsCount = 5;
        Long processingTime = 1500L;
        String status = "SUCCESS";
        String errorMessage = null;
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        String deviceInfo = "camera-01";
        String inferenceServer = "server-01";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String tags = "test,demo";
        String notes = "Test inference";
        Boolean isDeleted = false;
        Integer resultRating = 5;
        Boolean isFavorite = true;

        // When
        InferenceHistory history = new InferenceHistory(
            id, taskId, inferenceType, modelName, confidenceThreshold,
            originalFilename, fileSize, imagePath, inferenceResult,
            detectedObjectsCount, processingTime, status, errorMessage,
            userId, username, deviceInfo, inferenceServer,
            createdAt, updatedAt, tags, notes, isDeleted,
            resultRating, isFavorite
        );

        // Then
        assertEquals(id, history.getId());
        assertEquals(taskId, history.getTaskId());
        assertEquals(inferenceType, history.getInferenceType());
        assertEquals(modelName, history.getModelName());
        assertEquals(confidenceThreshold, history.getConfidenceThreshold());
        assertEquals(originalFilename, history.getOriginalFilename());
        assertEquals(fileSize, history.getFileSize());
        assertEquals(imagePath, history.getImagePath());
        assertEquals(inferenceResult, history.getInferenceResult());
        assertEquals(detectedObjectsCount, history.getDetectedObjectsCount());
        assertEquals(processingTime, history.getProcessingTime());
        assertEquals(status, history.getStatus());
        assertEquals(errorMessage, history.getErrorMessage());
        assertEquals(userId, history.getUserId());
        assertEquals(username, history.getUsername());
        assertEquals(deviceInfo, history.getDeviceInfo());
        assertEquals(inferenceServer, history.getInferenceServer());
        assertEquals(createdAt, history.getCreatedAt());
        assertEquals(updatedAt, history.getUpdatedAt());
        assertEquals(tags, history.getTags());
        assertEquals(notes, history.getNotes());
        assertEquals(isDeleted, history.getIsDeleted());
        assertEquals(resultRating, history.getResultRating());
        assertEquals(isFavorite, history.getIsFavorite());
    }

    @Test
    void testBuilder() {
        // Given
        String taskId = "task-456";
        String modelName = "resnet-50";
        String status = "PROCESSING";

        // When
        InferenceHistory history = InferenceHistory.builder()
            .taskId(taskId)
            .modelName(modelName)
            .status(status)
            .build();

        // Then
        assertEquals(taskId, history.getTaskId());
        assertEquals(modelName, history.getModelName());
        assertEquals(status, history.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Long id = 1L;
        String taskId = "task-789";
        String inferenceType = "batch";
        String modelName = "efficientnet";
        Double confidenceThreshold = 0.9;
        String originalFilename = "batch.zip";
        Long fileSize = 2048L;
        String imagePath = "/images/batch/";
        String inferenceResult = "{\"batch_results\": []}";
        Integer detectedObjectsCount = 10;
        Long processingTime = 3000L;
        String status = "FAILED";
        String errorMessage = "Model not found";
        UUID userId = UUID.randomUUID();
        String username = "batchuser";
        String deviceInfo = "batch-processor";
        String inferenceServer = "server-02";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String tags = "batch,production";
        String notes = "Batch processing test";
        Boolean isDeleted = true;
        Integer resultRating = 3;
        Boolean isFavorite = false;

        // When
        inferenceHistory.setId(id);
        inferenceHistory.setTaskId(taskId);
        inferenceHistory.setInferenceType(inferenceType);
        inferenceHistory.setModelName(modelName);
        inferenceHistory.setConfidenceThreshold(confidenceThreshold);
        inferenceHistory.setOriginalFilename(originalFilename);
        inferenceHistory.setFileSize(fileSize);
        inferenceHistory.setImagePath(imagePath);
        inferenceHistory.setInferenceResult(inferenceResult);
        inferenceHistory.setDetectedObjectsCount(detectedObjectsCount);
        inferenceHistory.setProcessingTime(processingTime);
        inferenceHistory.setStatus(status);
        inferenceHistory.setErrorMessage(errorMessage);
        inferenceHistory.setUserId(userId);
        inferenceHistory.setUsername(username);
        inferenceHistory.setDeviceInfo(deviceInfo);
        inferenceHistory.setInferenceServer(inferenceServer);
        inferenceHistory.setCreatedAt(createdAt);
        inferenceHistory.setUpdatedAt(updatedAt);
        inferenceHistory.setTags(tags);
        inferenceHistory.setNotes(notes);
        inferenceHistory.setIsDeleted(isDeleted);
        inferenceHistory.setResultRating(resultRating);
        inferenceHistory.setIsFavorite(isFavorite);

        // Then
        assertEquals(id, inferenceHistory.getId());
        assertEquals(taskId, inferenceHistory.getTaskId());
        assertEquals(inferenceType, inferenceHistory.getInferenceType());
        assertEquals(modelName, inferenceHistory.getModelName());
        assertEquals(confidenceThreshold, inferenceHistory.getConfidenceThreshold());
        assertEquals(originalFilename, inferenceHistory.getOriginalFilename());
        assertEquals(fileSize, inferenceHistory.getFileSize());
        assertEquals(imagePath, inferenceHistory.getImagePath());
        assertEquals(inferenceResult, inferenceHistory.getInferenceResult());
        assertEquals(detectedObjectsCount, inferenceHistory.getDetectedObjectsCount());
        assertEquals(processingTime, inferenceHistory.getProcessingTime());
        assertEquals(status, inferenceHistory.getStatus());
        assertEquals(errorMessage, inferenceHistory.getErrorMessage());
        assertEquals(userId, inferenceHistory.getUserId());
        assertEquals(username, inferenceHistory.getUsername());
        assertEquals(deviceInfo, inferenceHistory.getDeviceInfo());
        assertEquals(inferenceServer, inferenceHistory.getInferenceServer());
        assertEquals(createdAt, inferenceHistory.getCreatedAt());
        assertEquals(updatedAt, inferenceHistory.getUpdatedAt());
        assertEquals(tags, inferenceHistory.getTags());
        assertEquals(notes, inferenceHistory.getNotes());
        assertEquals(isDeleted, inferenceHistory.getIsDeleted());
        assertEquals(resultRating, inferenceHistory.getResultRating());
        assertEquals(isFavorite, inferenceHistory.getIsFavorite());
    }

    @Test
    void testPrePersist() {
        // Given
        InferenceHistory history = new InferenceHistory();
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

        // When
        history.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertNotNull(history.getCreatedAt());
        assertNotNull(history.getUpdatedAt());
        assertTrue(history.getCreatedAt().isAfter(beforeCreate));
        assertTrue(history.getCreatedAt().isBefore(afterCreate));
        assertEquals(history.getCreatedAt(), history.getUpdatedAt());
        assertFalse(history.getIsDeleted());
        assertFalse(history.getIsFavorite());
    }

    @Test
    void testPrePersist_WithExistingValues() {
        // Given
        InferenceHistory history = new InferenceHistory();
        history.setIsDeleted(true);
        history.setIsFavorite(true);

        // When
        history.onCreate();

        // Then
        assertTrue(history.getIsDeleted()); // 保持原有值
        assertTrue(history.getIsFavorite()); // 保持原有值
    }

    @Test
    void testPreUpdate() {
        // Given
        InferenceHistory history = new InferenceHistory();
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusHours(1);
        history.setCreatedAt(originalCreatedAt);
        history.setUpdatedAt(originalCreatedAt);
        
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        history.onUpdate();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertEquals(originalCreatedAt, history.getCreatedAt()); // 创建时间不变
        assertNotNull(history.getUpdatedAt());
        assertTrue(history.getUpdatedAt().isAfter(beforeUpdate));
        assertTrue(history.getUpdatedAt().isBefore(afterUpdate));
        assertNotEquals(history.getCreatedAt(), history.getUpdatedAt());
    }

    @Test
    void testInferenceStatusEnum() {
        // Test all enum values
        assertEquals("PROCESSING", InferenceHistory.InferenceStatus.PROCESSING.getCode());
        assertEquals("处理中", InferenceHistory.InferenceStatus.PROCESSING.getDescription());
        
        assertEquals("SUCCESS", InferenceHistory.InferenceStatus.SUCCESS.getCode());
        assertEquals("成功", InferenceHistory.InferenceStatus.SUCCESS.getDescription());
        
        assertEquals("FAILED", InferenceHistory.InferenceStatus.FAILED.getCode());
        assertEquals("失败", InferenceHistory.InferenceStatus.FAILED.getDescription());
        
        assertEquals("CANCELLED", InferenceHistory.InferenceStatus.CANCELLED.getCode());
        assertEquals("已取消", InferenceHistory.InferenceStatus.CANCELLED.getDescription());
    }

    @Test
    void testInferenceTypeEnum() {
        // Test all enum values
        assertEquals("single", InferenceHistory.InferenceType.SINGLE.getCode());
        assertEquals("单张图片推理", InferenceHistory.InferenceType.SINGLE.getDescription());
        
        assertEquals("batch", InferenceHistory.InferenceType.BATCH.getCode());
        assertEquals("批量推理", InferenceHistory.InferenceType.BATCH.getDescription());
        
        assertEquals("realtime", InferenceHistory.InferenceType.REALTIME.getCode());
        assertEquals("实时推理", InferenceHistory.InferenceType.REALTIME.getDescription());
        
        assertEquals("video", InferenceHistory.InferenceType.VIDEO.getCode());
        assertEquals("视频推理", InferenceHistory.InferenceType.VIDEO.getDescription());
    }

    @Test
    void testEnumValues() {
        // Test that all enum values are accessible
        InferenceHistory.InferenceStatus[] statuses = InferenceHistory.InferenceStatus.values();
        assertEquals(4, statuses.length);
        
        InferenceHistory.InferenceType[] types = InferenceHistory.InferenceType.values();
        assertEquals(4, types.length);
    }

    @Test
    void testCompleteInferenceHistory() {
        // Given
        InferenceHistory history = InferenceHistory.builder()
            .taskId("complete-task-001")
            .inferenceType(InferenceHistory.InferenceType.SINGLE.getCode())
            .modelName("yolo-v8-complete")
            .confidenceThreshold(0.85)
            .originalFilename("complete-test.jpg")
            .fileSize(4096L)
            .imagePath("/complete/images/test.jpg")
            .inferenceResult("{\"objects\": [{\"class\": \"person\", \"confidence\": 0.95}]}")
            .detectedObjectsCount(1)
            .processingTime(2000L)
            .status(InferenceHistory.InferenceStatus.SUCCESS.getCode())
            .userId(UUID.randomUUID())
            .username("completeuser")
            .deviceInfo("complete-camera")
            .inferenceServer("complete-server")
            .tags("complete,test,production")
            .notes("Complete inference test")
            .resultRating(5)
            .isFavorite(true)
            .build();

        // When
        history.onCreate();

        // Then
        assertNotNull(history.getTaskId());
        assertNotNull(history.getInferenceType());
        assertNotNull(history.getModelName());
        assertNotNull(history.getStatus());
        assertNotNull(history.getCreatedAt());
        assertNotNull(history.getUpdatedAt());
        assertFalse(history.getIsDeleted());
        assertTrue(history.getIsFavorite());
        assertEquals(1, history.getDetectedObjectsCount());
        assertEquals(5, history.getResultRating());
    }
}