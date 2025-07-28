package com.vision.vision_platform_backend.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * InferenceHistoryDto 单元测试
 */
@DisplayName("InferenceHistoryDto 测试")
public class InferenceHistoryDtoTest {

    @Nested
    @DisplayName("InferenceHistoryResponse 测试")
    class InferenceHistoryResponseTest {
        
        private InferenceHistoryDto.InferenceHistoryResponse response;
        
        @BeforeEach
        void setUp() {
            response = new InferenceHistoryDto.InferenceHistoryResponse();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(response);
            assertNull(response.getId());
            assertNull(response.getTaskId());
            assertNull(response.getInferenceType());
            assertNull(response.getModelName());
            assertNull(response.getConfidenceThreshold());
            assertNull(response.getOriginalFilename());
            assertNull(response.getFileSize());
            assertNull(response.getImagePath());
            assertNull(response.getInferenceResult());
            assertNull(response.getDetectedObjectsCount());
            assertNull(response.getProcessingTime());
            assertNull(response.getStatus());
            assertNull(response.getErrorMessage());
            assertNull(response.getUserId());
            assertNull(response.getUsername());
            assertNull(response.getDeviceInfo());
            assertNull(response.getInferenceServer());
            assertNull(response.getCreatedAt());
            assertNull(response.getUpdatedAt());
            assertNull(response.getTags());
            assertNull(response.getNotes());
            assertNull(response.getResultRating());
            assertNull(response.getIsFavorite());
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            UUID userId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            
            response = InferenceHistoryDto.InferenceHistoryResponse.builder()
                    .id(1L)
                    .taskId("task-123")
                    .inferenceType("detection")
                    .modelName("yolov8n")
                    .confidenceThreshold(0.5)
                    .originalFilename("test.jpg")
                    .fileSize(1024L)
                    .imagePath("/images/test.jpg")
                    .inferenceResult("{\"detections\": []}")
                    .detectedObjectsCount(5)
                    .processingTime(150L)
                    .status("COMPLETED")
                    .userId(userId)
                    .username("testuser")
                    .deviceInfo("Camera-001")
                    .inferenceServer("server-1")
                    .createdAt(now)
                    .updatedAt(now)
                    .tags("test,demo")
                    .notes("Test inference")
                    .resultRating(5)
                    .isFavorite(true)
                    .build();
            
            assertEquals(1L, response.getId());
            assertEquals("task-123", response.getTaskId());
            assertEquals("detection", response.getInferenceType());
            assertEquals("yolov8n", response.getModelName());
            assertEquals(0.5, response.getConfidenceThreshold());
            assertEquals("test.jpg", response.getOriginalFilename());
            assertEquals(1024L, response.getFileSize());
            assertEquals("/images/test.jpg", response.getImagePath());
            assertEquals("{\"detections\": []}", response.getInferenceResult());
            assertEquals(5, response.getDetectedObjectsCount());
            assertEquals(150L, response.getProcessingTime());
            assertEquals("COMPLETED", response.getStatus());
            assertEquals(userId, response.getUserId());
            assertEquals("testuser", response.getUsername());
            assertEquals("Camera-001", response.getDeviceInfo());
            assertEquals("server-1", response.getInferenceServer());
            assertEquals(now, response.getCreatedAt());
            assertEquals(now, response.getUpdatedAt());
            assertEquals("test,demo", response.getTags());
            assertEquals("Test inference", response.getNotes());
            assertEquals(5, response.getResultRating());
            assertTrue(response.getIsFavorite());
        }
    }
    
    @Nested
    @DisplayName("CreateInferenceHistoryRequest 测试")
    class CreateInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.CreateInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.CreateInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(request);
            assertNull(request.getTaskId());
            assertNull(request.getInferenceType());
            assertNull(request.getModelName());
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            UUID userId = UUID.randomUUID();
            
            request = InferenceHistoryDto.CreateInferenceHistoryRequest.builder()
                    .taskId("task-456")
                    .inferenceType("segmentation")
                    .modelName("yolov8n-seg")
                    .confidenceThreshold(0.6)
                    .originalFilename("image.png")
                    .fileSize(2048L)
                    .imagePath("/images/image.png")
                    .inferenceResult("{\"segments\": []}")
                    .detectedObjectsCount(3)
                    .processingTime(200L)
                    .status("PROCESSING")
                    .userId(userId)
                    .username("user2")
                    .deviceInfo("Camera-002")
                    .inferenceServer("server-2")
                    .tags("production")
                    .notes("Production inference")
                    .build();
            
            assertEquals("task-456", request.getTaskId());
            assertEquals("segmentation", request.getInferenceType());
            assertEquals("yolov8n-seg", request.getModelName());
            assertEquals(0.6, request.getConfidenceThreshold());
            assertEquals("image.png", request.getOriginalFilename());
            assertEquals(2048L, request.getFileSize());
            assertEquals("/images/image.png", request.getImagePath());
            assertEquals("{\"segments\": []}", request.getInferenceResult());
            assertEquals(3, request.getDetectedObjectsCount());
            assertEquals(200L, request.getProcessingTime());
            assertEquals("PROCESSING", request.getStatus());
            assertEquals(userId, request.getUserId());
            assertEquals("user2", request.getUsername());
            assertEquals("Camera-002", request.getDeviceInfo());
            assertEquals("server-2", request.getInferenceServer());
            assertEquals("production", request.getTags());
            assertEquals("Production inference", request.getNotes());
        }
    }
    
    @Nested
    @DisplayName("UpdateInferenceHistoryRequest 测试")
    class UpdateInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.UpdateInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.UpdateInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(request);
            assertNull(request.getStatus());
            assertNull(request.getErrorMessage());
            assertNull(request.getInferenceResult());
            assertNull(request.getDetectedObjectsCount());
            assertNull(request.getProcessingTime());
            assertNull(request.getTags());
            assertNull(request.getNotes());
            assertNull(request.getResultRating());
            assertNull(request.getIsFavorite());
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            request = InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                    .status("COMPLETED")
                    .inferenceResult("{\"updated\": true}")
                    .detectedObjectsCount(10)
                    .processingTime(300L)
                    .tags("updated,final")
                    .notes("Updated notes")
                    .resultRating(4)
                    .isFavorite(false)
                    .build();
            
            assertEquals("COMPLETED", request.getStatus());
            assertEquals("{\"updated\": true}", request.getInferenceResult());
            assertEquals(10, request.getDetectedObjectsCount());
            assertEquals(300L, request.getProcessingTime());
            assertEquals("updated,final", request.getTags());
            assertEquals("Updated notes", request.getNotes());
            assertEquals(4, request.getResultRating());
            assertFalse(request.getIsFavorite());
        }
    }
    
    @Nested
    @DisplayName("SearchInferenceHistoryRequest 测试")
    class SearchInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.SearchInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.SearchInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(request);
            assertNull(request.getKeyword());
            assertNull(request.getInferenceType());
            assertNull(request.getModelName());
            assertNull(request.getStatus());
            assertNull(request.getUserId());
            assertNull(request.getUsername());
            assertNull(request.getStartTime());
            assertNull(request.getEndTime());
            assertNull(request.getIsFavorite());
            assertNull(request.getMinRating());
            assertNull(request.getTags());
            assertEquals(0, request.getPage());
            assertEquals(20, request.getSize());
            assertEquals("createdAt", request.getSortBy());
            assertEquals("desc", request.getSortDirection());
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            UUID userId = UUID.randomUUID();
            LocalDateTime startTime = LocalDateTime.now().minusDays(7);
            LocalDateTime endTime = LocalDateTime.now();
            List<String> tags = Arrays.asList("test", "demo");
            
            request = InferenceHistoryDto.SearchInferenceHistoryRequest.builder()
                    .keyword("test")
                    .inferenceType("detection")
                    .modelName("yolov8n")
                    .status("COMPLETED")
                    .userId(userId)
                    .username("testuser")
                    .startTime(startTime)
                    .endTime(endTime)
                    .isFavorite(true)
                    .minRating(3)
                    .tags(tags)
                    .page(1)
                    .size(10)
                    .sortBy("updatedAt")
                    .sortDirection("asc")
                    .build();
            
            assertEquals("test", request.getKeyword());
            assertEquals("detection", request.getInferenceType());
            assertEquals("yolov8n", request.getModelName());
            assertEquals("COMPLETED", request.getStatus());
            assertEquals(userId, request.getUserId());
            assertEquals("testuser", request.getUsername());
            assertEquals(startTime, request.getStartTime());
            assertEquals(endTime, request.getEndTime());
            assertTrue(request.getIsFavorite());
            assertEquals(3, request.getMinRating());
            assertEquals(tags, request.getTags());
            assertEquals(1, request.getPage());
            assertEquals(10, request.getSize());
            assertEquals("updatedAt", request.getSortBy());
            assertEquals("asc", request.getSortDirection());
        }
    }
    
    @Nested
    @DisplayName("InferenceHistoryPageResponse 测试")
    class InferenceHistoryPageResponseTest {
        
        private InferenceHistoryDto.InferenceHistoryPageResponse response;
        
        @BeforeEach
        void setUp() {
            response = new InferenceHistoryDto.InferenceHistoryPageResponse();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(response);
            assertNull(response.getContent());
            assertNull(response.getPage());
            assertNull(response.getSize());
            assertNull(response.getTotalElements());
            assertNull(response.getTotalPages());
            assertNull(response.getFirst());
            assertNull(response.getLast());
            assertNull(response.getEmpty());
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            List<InferenceHistoryDto.InferenceHistoryResponse> content = Arrays.asList(
                    new InferenceHistoryDto.InferenceHistoryResponse(),
                    new InferenceHistoryDto.InferenceHistoryResponse()
            );
            
            response = InferenceHistoryDto.InferenceHistoryPageResponse.builder()
                    .content(content)
                    .page(0)
                    .size(20)
                    .totalElements(100L)
                    .totalPages(5)
                    .first(true)
                    .last(false)
                    .empty(false)
                    .build();
            
            assertEquals(content, response.getContent());
            assertEquals(0, response.getPage());
            assertEquals(20, response.getSize());
            assertEquals(100L, response.getTotalElements());
            assertEquals(5, response.getTotalPages());
            assertTrue(response.getFirst());
            assertFalse(response.getLast());
            assertFalse(response.getEmpty());
        }
    }
    
    @Nested
    @DisplayName("InferenceHistoryStats 测试")
    class InferenceHistoryStatsTest {
        
        private InferenceHistoryDto.InferenceHistoryStats stats;
        
        @BeforeEach
        void setUp() {
            stats = new InferenceHistoryDto.InferenceHistoryStats();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(stats);
            assertNull(stats.getTotalInferences());
            assertNull(stats.getSuccessfulInferences());
            assertNull(stats.getFailedInferences());
            assertNull(stats.getSuccessRate());
            assertNull(stats.getAverageProcessingTime());
            assertNull(stats.getAverageDetectedObjects());
            assertNull(stats.getModelUsageStats());
            assertNull(stats.getTypeUsageStats());
            assertNull(stats.getDailyStats());
            assertNull(stats.getRecentInferences());
            assertNull(stats.getSlowestInferences());
            assertNull(stats.getMostDetectedInferences());
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            List<InferenceHistoryDto.ModelUsageStats> modelStats = Arrays.asList(
                    InferenceHistoryDto.ModelUsageStats.builder()
                            .modelName("yolov8n")
                            .usageCount(50L)
                            .usagePercentage(50.0)
                            .build()
            );
            
            stats = InferenceHistoryDto.InferenceHistoryStats.builder()
                    .totalInferences(100L)
                    .successfulInferences(95L)
                    .failedInferences(5L)
                    .successRate(95.0)
                    .averageProcessingTime(150.5)
                    .averageDetectedObjects(3.2)
                    .modelUsageStats(modelStats)
                    .build();
            
            assertEquals(100L, stats.getTotalInferences());
            assertEquals(95L, stats.getSuccessfulInferences());
            assertEquals(5L, stats.getFailedInferences());
            assertEquals(95.0, stats.getSuccessRate());
            assertEquals(150.5, stats.getAverageProcessingTime());
            assertEquals(3.2, stats.getAverageDetectedObjects());
            assertEquals(modelStats, stats.getModelUsageStats());
        }
    }
    
    @Nested
    @DisplayName("ModelUsageStats 测试")
    class ModelUsageStatsTest {
        
        private InferenceHistoryDto.ModelUsageStats stats;
        
        @BeforeEach
        void setUp() {
            stats = new InferenceHistoryDto.ModelUsageStats();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            stats = InferenceHistoryDto.ModelUsageStats.builder()
                    .modelName("yolov8n")
                    .usageCount(75L)
                    .usagePercentage(75.0)
                    .build();
            
            assertEquals("yolov8n", stats.getModelName());
            assertEquals(75L, stats.getUsageCount());
            assertEquals(75.0, stats.getUsagePercentage());
        }
    }
    
    @Nested
    @DisplayName("TypeUsageStats 测试")
    class TypeUsageStatsTest {
        
        private InferenceHistoryDto.TypeUsageStats stats;
        
        @BeforeEach
        void setUp() {
            stats = new InferenceHistoryDto.TypeUsageStats();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            stats = InferenceHistoryDto.TypeUsageStats.builder()
                    .inferenceType("detection")
                    .usageCount(80L)
                    .usagePercentage(80.0)
                    .build();
            
            assertEquals("detection", stats.getInferenceType());
            assertEquals(80L, stats.getUsageCount());
            assertEquals(80.0, stats.getUsagePercentage());
        }
    }
    
    @Nested
    @DisplayName("DailyStats 测试")
    class DailyStatsTest {
        
        private InferenceHistoryDto.DailyStats stats;
        
        @BeforeEach
        void setUp() {
            stats = new InferenceHistoryDto.DailyStats();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            LocalDateTime date = LocalDateTime.now();
            
            stats = InferenceHistoryDto.DailyStats.builder()
                    .date(date)
                    .inferenceCount(25L)
                    .build();
            
            assertEquals(date, stats.getDate());
            assertEquals(25L, stats.getInferenceCount());
        }
    }
    
    @Nested
    @DisplayName("BatchOperationRequest 测试")
    class BatchOperationRequestTest {
        
        private InferenceHistoryDto.BatchOperationRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.BatchOperationRequest();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            List<UUID> ids = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
            
            request = InferenceHistoryDto.BatchOperationRequest.builder()
                    .ids(ids)
                    .operation("favorite")
                    .rating(5)
                    .tags("batch,operation")
                    .build();
            
            assertEquals(ids, request.getIds());
            assertEquals("favorite", request.getOperation());
            assertEquals(5, request.getRating());
            assertEquals("batch,operation", request.getTags());
        }
    }
    
    @Nested
    @DisplayName("ExportInferenceHistoryRequest 测试")
    class ExportInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.ExportInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.ExportInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            List<UUID> ids = Arrays.asList(UUID.randomUUID());
            InferenceHistoryDto.SearchInferenceHistoryRequest searchCriteria = 
                    new InferenceHistoryDto.SearchInferenceHistoryRequest();
            
            request = InferenceHistoryDto.ExportInferenceHistoryRequest.builder()
                    .ids(ids)
                    .format("json")
                    .searchCriteria(searchCriteria)
                    .includeImages(true)
                    .build();
            
            assertEquals(ids, request.getIds());
            assertEquals("json", request.getFormat());
            assertEquals(searchCriteria, request.getSearchCriteria());
            assertTrue(request.getIncludeImages());
        }
    }
    
    @Nested
    @DisplayName("ImportInferenceHistoryRequest 测试")
    class ImportInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.ImportInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.ImportInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            request = InferenceHistoryDto.ImportInferenceHistoryRequest.builder()
                    .format("json")
                    .data("{\"data\": \"test\"}")
                    .overwriteExisting(true)
                    .build();
            
            assertEquals("json", request.getFormat());
            assertEquals("{\"data\": \"test\"}", request.getData());
            assertTrue(request.getOverwriteExisting());
        }
    }
    
    @Nested
    @DisplayName("CleanupInferenceHistoryRequest 测试")
    class CleanupInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.CleanupInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.CleanupInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            request = InferenceHistoryDto.CleanupInferenceHistoryRequest.builder()
                    .daysToKeep(30)
                    .onlyDeleteFailed(false)
                    .physicalDelete(true)
                    .build();
            
            assertEquals(30, request.getDaysToKeep());
            assertFalse(request.getOnlyDeleteFailed());
            assertTrue(request.getPhysicalDelete());
        }
    }
    
    @Nested
    @DisplayName("BackupInferenceHistoryRequest 测试")
    class BackupInferenceHistoryRequestTest {
        
        private InferenceHistoryDto.BackupInferenceHistoryRequest request;
        
        @BeforeEach
        void setUp() {
            request = new InferenceHistoryDto.BackupInferenceHistoryRequest();
        }
        
        @Test
        @DisplayName("Builder模式")
        void testBuilderPattern() {
            LocalDateTime startTime = LocalDateTime.now().minusDays(30);
            LocalDateTime endTime = LocalDateTime.now();
            
            request = InferenceHistoryDto.BackupInferenceHistoryRequest.builder()
                    .startTime(startTime)
                    .endTime(endTime)
                    .backupFormat("json")
                    .includeImages(false)
                    .backupLocation("cloud")
                    .build();
            
            assertEquals(startTime, request.getStartTime());
            assertEquals(endTime, request.getEndTime());
            assertEquals("json", request.getBackupFormat());
            assertFalse(request.getIncludeImages());
            assertEquals("cloud", request.getBackupLocation());
        }
    }
}