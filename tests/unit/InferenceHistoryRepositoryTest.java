package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.entity.InferenceHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InferenceHistoryRepository 单元测试
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InferenceHistoryRepository 测试")
public class InferenceHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InferenceHistoryRepository repository;

    private InferenceHistory testHistory1;
    private InferenceHistory testHistory2;
    private InferenceHistory testHistory3;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testHistory1 = InferenceHistory.builder()
                .taskId("task-001")
                .inferenceType("single")
                .modelName("yolov8n")
                .confidenceThreshold(0.5)
                .originalFilename("test1.jpg")
                .fileSize(1024L)
                .imagePath("/images/test1.jpg")
                .inferenceResult("{\"detections\": []}")
                .detectedObjectsCount(5)
                .processingTime(1000L)
                .status("SUCCESS")
                .userId(testUserId)
                .username("testuser")
                .deviceInfo("camera-001")
                .inferenceServer("server-001")
                .tags("test,object-detection")
                .notes("Test inference 1")
                .isDeleted(false)
                .resultRating(4)
                .isFavorite(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        testHistory2 = InferenceHistory.builder()
                .taskId("task-002")
                .inferenceType("batch")
                .modelName("yolov8s")
                .confidenceThreshold(0.6)
                .originalFilename("test2.jpg")
                .fileSize(2048L)
                .imagePath("/images/test2.jpg")
                .inferenceResult("{\"detections\": []}")
                .detectedObjectsCount(3)
                .processingTime(2000L)
                .status("FAILED")
                .errorMessage("Model not found")
                .userId(testUserId)
                .username("testuser")
                .deviceInfo("camera-002")
                .inferenceServer("server-002")
                .tags("test,batch")
                .notes("Test inference 2")
                .isDeleted(false)
                .resultRating(2)
                .isFavorite(false)
                .createdAt(LocalDateTime.now().minusHours(12))
                .updatedAt(LocalDateTime.now().minusHours(12))
                .build();

        testHistory3 = InferenceHistory.builder()
                .taskId("task-003")
                .inferenceType("realtime")
                .modelName("yolov8n")
                .confidenceThreshold(0.7)
                .originalFilename("test3.jpg")
                .fileSize(3072L)
                .imagePath("/images/test3.jpg")
                .inferenceResult("{\"detections\": []}")
                .detectedObjectsCount(8)
                .processingTime(500L)
                .status("SUCCESS")
                .userId(UUID.randomUUID())
                .username("anotheruser")
                .deviceInfo("camera-003")
                .inferenceServer("server-001")
                .tags("test,realtime")
                .notes("Test inference 3")
                .isDeleted(true)
                .resultRating(5)
                .isFavorite(true)
                .createdAt(LocalDateTime.now().minusHours(6))
                .updatedAt(LocalDateTime.now().minusHours(6))
                .build();

        entityManager.persistAndFlush(testHistory1);
        entityManager.persistAndFlush(testHistory2);
        entityManager.persistAndFlush(testHistory3);
    }

    @Nested
    @DisplayName("基本查询测试")
    class BasicQueryTest {

        @Test
        @DisplayName("根据任务ID查找")
        void testFindByTaskId() {
            Optional<InferenceHistory> result = repository.findByTaskId("task-001");
            assertTrue(result.isPresent());
            assertEquals("task-001", result.get().getTaskId());
            assertEquals("testuser", result.get().getUsername());
        }

        @Test
        @DisplayName("根据任务ID查找 - 不存在")
        void testFindByTaskIdNotFound() {
            Optional<InferenceHistory> result = repository.findByTaskId("non-existent");
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("根据用户ID查找推理历史")
        void testFindByUserIdAndIsDeletedFalse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(testUserId, pageable);
            
            assertEquals(2, result.getContent().size());
            assertEquals("task-002", result.getContent().get(0).getTaskId()); // 最新的在前
            assertEquals("task-001", result.getContent().get(1).getTaskId());
        }

        @Test
        @DisplayName("根据用户名查找推理历史")
        void testFindByUsernameAndIsDeletedFalse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByUsernameAndIsDeletedFalseOrderByCreatedAtDesc("testuser", pageable);
            
            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().stream().allMatch(h -> "testuser".equals(h.getUsername())));
        }

        @Test
        @DisplayName("查找所有未删除的推理历史")
        void testFindByIsDeletedFalse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
            
            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().stream().allMatch(h -> !h.getIsDeleted()));
        }

        @Test
        @DisplayName("根据推理类型查找")
        void testFindByInferenceType() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByInferenceTypeAndIsDeletedFalseOrderByCreatedAtDesc("single", pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("single", result.getContent().get(0).getInferenceType());
        }

        @Test
        @DisplayName("根据模型名称查找")
        void testFindByModelName() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByModelNameAndIsDeletedFalseOrderByCreatedAtDesc("yolov8n", pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("yolov8n", result.getContent().get(0).getModelName());
        }

        @Test
        @DisplayName("根据状态查找")
        void testFindByStatus() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByStatusAndIsDeletedFalseOrderByCreatedAtDesc("SUCCESS", pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("SUCCESS", result.getContent().get(0).getStatus());
        }

        @Test
        @DisplayName("根据时间范围查找")
        void testFindByCreatedAtBetween() {
            LocalDateTime startTime = LocalDateTime.now().minusDays(2);
            LocalDateTime endTime = LocalDateTime.now().minusHours(10);
            Pageable pageable = PageRequest.of(0, 10);
            
            Page<InferenceHistory> result = repository.findByCreatedAtBetweenAndIsDeletedFalseOrderByCreatedAtDesc(
                    startTime, endTime, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("task-001", result.getContent().get(0).getTaskId());
        }

        @Test
        @DisplayName("查找收藏的推理记录")
        void testFindByIsFavoriteTrue() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(pageable);
            
            assertEquals(1, result.getContent().size());
            assertTrue(result.getContent().get(0).getIsFavorite());
        }

        @Test
        @DisplayName("根据用户查找收藏的推理记录")
        void testFindByUserIdAndIsFavoriteTrue() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByUserIdAndIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(
                    testUserId, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals(testUserId, result.getContent().get(0).getUserId());
            assertTrue(result.getContent().get(0).getIsFavorite());
        }
    }

    @Nested
    @DisplayName("复合搜索测试")
    class SearchTest {

        @Test
        @DisplayName("关键词搜索")
        void testSearchInferenceHistoryByKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    "test1", null, null, null, null, null, null, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("test1.jpg", result.getContent().get(0).getOriginalFilename());
        }

        @Test
        @DisplayName("按推理类型搜索")
        void testSearchInferenceHistoryByType() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    null, "batch", null, null, null, null, null, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("batch", result.getContent().get(0).getInferenceType());
        }

        @Test
        @DisplayName("按模型名称搜索")
        void testSearchInferenceHistoryByModel() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    null, null, "yolov8s", null, null, null, null, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("yolov8s", result.getContent().get(0).getModelName());
        }

        @Test
        @DisplayName("按状态搜索")
        void testSearchInferenceHistoryByStatus() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    null, null, null, "FAILED", null, null, null, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("FAILED", result.getContent().get(0).getStatus());
        }

        @Test
        @DisplayName("按用户ID搜索")
        void testSearchInferenceHistoryByUserId() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    null, null, null, null, testUserId, null, null, pageable);
            
            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().stream().allMatch(h -> testUserId.equals(h.getUserId())));
        }

        @Test
        @DisplayName("按时间范围搜索")
        void testSearchInferenceHistoryByTimeRange() {
            LocalDateTime startTime = LocalDateTime.now().minusDays(1);
            LocalDateTime endTime = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    null, null, null, null, null, startTime, endTime, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("task-002", result.getContent().get(0).getTaskId());
        }

        @Test
        @DisplayName("复合条件搜索")
        void testSearchInferenceHistoryMultipleConditions() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.searchInferenceHistory(
                    "test", "single", "yolov8n", "SUCCESS", testUserId, null, null, pageable);
            
            assertEquals(1, result.getContent().size());
            InferenceHistory history = result.getContent().get(0);
            assertEquals("single", history.getInferenceType());
            assertEquals("yolov8n", history.getModelName());
            assertEquals("SUCCESS", history.getStatus());
            assertEquals(testUserId, history.getUserId());
        }
    }

    @Nested
    @DisplayName("统计查询测试")
    class StatisticsTest {

        @Test
        @DisplayName("统计总推理次数")
        void testCountTotalInferences() {
            Long count = repository.countTotalInferences();
            assertEquals(2L, count); // 只计算未删除的
        }

        @Test
        @DisplayName("统计成功推理次数")
        void testCountSuccessfulInferences() {
            Long count = repository.countSuccessfulInferences();
            assertEquals(1L, count);
        }

        @Test
        @DisplayName("统计失败推理次数")
        void testCountFailedInferences() {
            Long count = repository.countFailedInferences();
            assertEquals(1L, count);
        }

        @Test
        @DisplayName("统计用户推理次数")
        void testCountInferencesByUser() {
            Long count = repository.countInferencesByUser(testUserId);
            assertEquals(2L, count);
        }

        @Test
        @DisplayName("统计各模型使用次数")
        void testCountInferencesByModel() {
            List<Object[]> results = repository.countInferencesByModel();
            assertEquals(2, results.size());
            
            // 验证结果包含预期的模型
            boolean hasYolov8n = results.stream().anyMatch(r -> "yolov8n".equals(r[0]));
            boolean hasYolov8s = results.stream().anyMatch(r -> "yolov8s".equals(r[0]));
            assertTrue(hasYolov8n);
            assertTrue(hasYolov8s);
        }

        @Test
        @DisplayName("统计各推理类型使用次数")
        void testCountInferencesByType() {
            List<Object[]> results = repository.countInferencesByType();
            assertEquals(2, results.size());
            
            // 验证结果包含预期的类型
            boolean hasSingle = results.stream().anyMatch(r -> "single".equals(r[0]));
            boolean hasBatch = results.stream().anyMatch(r -> "batch".equals(r[0]));
            assertTrue(hasSingle);
            assertTrue(hasBatch);
        }

        @Test
        @DisplayName("统计每日推理次数")
        void testCountDailyInferences() {
            LocalDateTime startDate = LocalDateTime.now().minusDays(2);
            List<Object[]> results = repository.countDailyInferences(startDate);
            assertFalse(results.isEmpty());
        }

        @Test
        @DisplayName("计算平均处理时间")
        void testGetAverageProcessingTime() {
            Double avgTime = repository.getAverageProcessingTime();
            assertNotNull(avgTime);
            assertEquals(1000.0, avgTime); // 只有一个成功的记录，处理时间为1000ms
        }

        @Test
        @DisplayName("计算平均检测目标数量")
        void testGetAverageDetectedObjectsCount() {
            Double avgCount = repository.getAverageDetectedObjectsCount();
            assertNotNull(avgCount);
            assertEquals(5.0, avgCount); // 只有一个成功的记录，检测到5个目标
        }
    }

    @Nested
    @DisplayName("特殊查询测试")
    class SpecialQueryTest {

        @Test
        @DisplayName("查找最近的推理记录")
        void testFindRecentInferences() {
            Pageable pageable = PageRequest.of(0, 5);
            List<InferenceHistory> results = repository.findRecentInferences(pageable);
            
            assertEquals(2, results.size());
            // 验证按时间倒序排列
            assertTrue(results.get(0).getCreatedAt().isAfter(results.get(1).getCreatedAt()) ||
                      results.get(0).getCreatedAt().isEqual(results.get(1).getCreatedAt()));
        }

        @Test
        @DisplayName("查找处理时间最长的推理记录")
        void testFindSlowestInferences() {
            Pageable pageable = PageRequest.of(0, 5);
            List<InferenceHistory> results = repository.findSlowestInferences(pageable);
            
            assertEquals(1, results.size()); // 只有一个成功的记录
            assertEquals(1000L, results.get(0).getProcessingTime());
        }

        @Test
        @DisplayName("查找检测目标最多的推理记录")
        void testFindMostDetectedInferences() {
            Pageable pageable = PageRequest.of(0, 5);
            List<InferenceHistory> results = repository.findMostDetectedInferences(pageable);
            
            assertEquals(1, results.size()); // 只有一个成功的记录
            assertEquals(5, results.get(0).getDetectedObjectsCount());
        }

        @Test
        @DisplayName("根据评分查找")
        void testFindByResultRating() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findByResultRatingAndIsDeletedFalseOrderByCreatedAtDesc(4, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals(4, result.getContent().get(0).getResultRating());
        }

        @Test
        @DisplayName("查找高评分推理记录")
        void testFindHighRatedInferences() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<InferenceHistory> result = repository.findHighRatedInferences(3, pageable);
            
            assertEquals(1, result.getContent().size());
            assertTrue(result.getContent().get(0).getResultRating() >= 3);
        }
    }

    @Test
    @DisplayName("分页功能测试")
    void testPagination() {
        // 添加更多测试数据
        for (int i = 4; i <= 15; i++) {
            InferenceHistory history = InferenceHistory.builder()
                    .taskId("task-" + String.format("%03d", i))
                    .inferenceType("single")
                    .modelName("yolov8n")
                    .status("SUCCESS")
                    .userId(testUserId)
                    .username("testuser")
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now().minusMinutes(i))
                    .updatedAt(LocalDateTime.now().minusMinutes(i))
                    .build();
            entityManager.persistAndFlush(history);
        }

        // 测试第一页
        Pageable firstPage = PageRequest.of(0, 5);
        Page<InferenceHistory> firstResult = repository.findByIsDeletedFalseOrderByCreatedAtDesc(firstPage);
        
        assertEquals(5, firstResult.getContent().size());
        assertEquals(0, firstResult.getNumber());
        assertTrue(firstResult.hasNext());
        assertFalse(firstResult.hasPrevious());

        // 测试第二页
        Pageable secondPage = PageRequest.of(1, 5);
        Page<InferenceHistory> secondResult = repository.findByIsDeletedFalseOrderByCreatedAtDesc(secondPage);
        
        assertEquals(5, secondResult.getContent().size());
        assertEquals(1, secondResult.getNumber());
        assertTrue(secondResult.hasNext());
        assertTrue(secondResult.hasPrevious());
    }

    @Test
    @DisplayName("空结果测试")
    void testEmptyResults() {
        // 测试不存在的用户ID
        UUID nonExistentUserId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<InferenceHistory> result = repository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(nonExistentUserId, pageable);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        // 测试不存在的模型名称
        Page<InferenceHistory> modelResult = repository.findByModelNameAndIsDeletedFalseOrderByCreatedAtDesc("non-existent-model", pageable);
        assertTrue(modelResult.getContent().isEmpty());
    }
}