package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.entity.InferenceHistory;
import com.vision.vision_platform_backend.repository.InferenceHistoryRepository;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 推理历史记录服务类
 */
@Service
@Slf4j
public class InferenceHistoryService {

    private static final Logger log = LoggerFactory.getLogger(InferenceHistoryService.class);
    private final InferenceHistoryRepository inferenceHistoryRepository;

    public InferenceHistoryService(InferenceHistoryRepository inferenceHistoryRepository) {
        this.inferenceHistoryRepository = inferenceHistoryRepository;
    }

    /**
     * 创建推理历史记录
     */
    @Transactional
    public InferenceHistoryDto.InferenceHistoryResponse createInferenceHistory(
            InferenceHistoryDto.CreateInferenceHistoryRequest request) {
        try {
            InferenceHistory history = InferenceHistory.builder()
                    .taskId(request.getTaskId())
                    .inferenceType(request.getInferenceType())
                    .modelName(request.getModelName())
                    .confidenceThreshold(request.getConfidenceThreshold())
                    .originalFilename(request.getOriginalFilename())
                    .fileSize(request.getFileSize())
                    .imagePath(request.getImagePath())
                    .inferenceResult(request.getInferenceResult() != null ? request.getInferenceResult().toString() : null)
                    .detectedObjectsCount(request.getDetectedObjectsCount())
                    .processingTime(request.getProcessingTime())
                    .status(request.getStatus())
                    .errorMessage(request.getErrorMessage())
                    .userId(request.getUserId())
                    .username(request.getUsername())
                    .deviceInfo(request.getDeviceInfo())
                    .inferenceServer(request.getInferenceServer())
                    .tags(request.getTags())
                    .notes(request.getNotes())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .isFavorite(false)
                    .build();

            InferenceHistory savedHistory = inferenceHistoryRepository.save(history);
            log.info("创建推理历史记录成功: taskId={}", savedHistory.getTaskId());
            
            return convertToResponse(savedHistory);
        } catch (Exception e) {
            log.error("创建推理历史记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建推理历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取推理历史记录
     */
    public Optional<InferenceHistoryDto.InferenceHistoryResponse> getInferenceHistoryById(Long id) {
        return inferenceHistoryRepository.findByIdAndIsDeletedFalse(id)
                .map(this::convertToResponse);
    }

    /**
     * 根据任务ID获取推理历史记录
     */
    public Optional<InferenceHistoryDto.InferenceHistoryResponse> getInferenceHistoryByTaskId(String taskId) {
        return inferenceHistoryRepository.findByTaskIdAndIsDeletedFalse(taskId)
                .map(this::convertToResponse);
    }

    /**
     * 搜索推理历史记录
     */
    public InferenceHistoryDto.InferenceHistoryPageResponse searchInferenceHistory(
            InferenceHistoryDto.SearchInferenceHistoryRequest request) {
        try {
            // 构建分页和排序
            Sort sort = Sort.by(
                    "desc".equalsIgnoreCase(request.getSortDirection()) 
                            ? Sort.Direction.DESC 
                            : Sort.Direction.ASC,
                    request.getSortBy()
            );
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

            Page<InferenceHistory> page;

            // 根据搜索条件选择查询方法
            if (hasComplexSearchCriteria(request)) {
                page = inferenceHistoryRepository.searchWithComplexCriteria(
                        request.getKeyword(),
                        request.getInferenceType(),
                        request.getModelName(),
                        request.getStatus(),
                        request.getUserId(),
                        request.getUsername(),
                        request.getStartTime(),
                        request.getEndTime(),
                        request.getIsFavorite(),
                        request.getMinRating(),
                        pageable
                );
            } else {
                // 简单查询
                page = inferenceHistoryRepository.findByIsDeletedFalse(pageable);
            }

            List<InferenceHistoryDto.InferenceHistoryResponse> content = page.getContent()
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return InferenceHistoryDto.InferenceHistoryPageResponse.builder()
                    .content(content)
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .first(page.isFirst())
                    .last(page.isLast())
                    .empty(page.isEmpty())
                    .build();

        } catch (Exception e) {
            log.error("搜索推理历史记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索推理历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 更新推理历史记录
     */
    @Transactional
    public InferenceHistoryDto.InferenceHistoryResponse updateInferenceHistory(
            Long id, InferenceHistoryDto.UpdateInferenceHistoryRequest request) {
        try {
            InferenceHistory history = inferenceHistoryRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("推理历史记录不存在: " + id));

            // 更新字段
            if (request.getStatus() != null) {
                history.setStatus(request.getStatus());
            }
            if (request.getErrorMessage() != null) {
                history.setErrorMessage(request.getErrorMessage());
            }
            if (request.getInferenceResult() != null) {
                history.setInferenceResult(request.getInferenceResult().toString());
            }
            if (request.getDetectedObjectsCount() != null) {
                history.setDetectedObjectsCount(request.getDetectedObjectsCount());
            }
            if (request.getProcessingTime() != null) {
                history.setProcessingTime(request.getProcessingTime());
            }
            if (request.getTags() != null) {
                history.setTags(request.getTags());
            }
            if (request.getNotes() != null) {
                history.setNotes(request.getNotes());
            }
            if (request.getResultRating() != null) {
                history.setResultRating(request.getResultRating());
            }
            if (request.getIsFavorite() != null) {
                history.setIsFavorite(request.getIsFavorite());
            }

            history.setUpdatedAt(LocalDateTime.now());
            InferenceHistory updatedHistory = inferenceHistoryRepository.save(history);
            
            log.info("更新推理历史记录成功: id={}", id);
            return convertToResponse(updatedHistory);

        } catch (Exception e) {
            log.error("更新推理历史记录失败: id={}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("更新推理历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 软删除推理历史记录
     */
    @Transactional
    public void deleteInferenceHistory(Long id) {
        try {
            InferenceHistory history = inferenceHistoryRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("推理历史记录不存在: " + id));

            history.setIsDeleted(true);
            history.setUpdatedAt(LocalDateTime.now());
            inferenceHistoryRepository.save(history);
            
            log.info("删除推理历史记录成功: id={}", id);

        } catch (Exception e) {
            log.error("删除推理历史记录失败: id={}, error={}", id, e.getMessage(), e);
            throw new RuntimeException("删除推理历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 批量软删除推理历史记录
     */
    @Transactional
    public void batchDeleteInferenceHistory(List<Long> ids) {
        try {
            List<InferenceHistory> histories = inferenceHistoryRepository.findByIdInAndIsDeletedFalse(ids);
            
            histories.forEach(history -> {
                history.setIsDeleted(true);
                history.setUpdatedAt(LocalDateTime.now());
            });
            
            inferenceHistoryRepository.saveAll(histories);
            log.info("批量删除推理历史记录成功: count={}", histories.size());

        } catch (Exception e) {
            log.error("批量删除推理历史记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量删除推理历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取推理历史统计信息
     */
    public InferenceHistoryDto.InferenceHistoryStats getInferenceHistoryStats(
            Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 基础统计
            Long totalInferences = inferenceHistoryRepository.countTotalInferences(userId, startTime, endTime);
            Long successfulInferences = inferenceHistoryRepository.countSuccessfulInferences(userId, startTime, endTime);
            Long failedInferences = inferenceHistoryRepository.countFailedInferences(userId, startTime, endTime);
            
            Double successRate = totalInferences > 0 ? (double) successfulInferences / totalInferences * 100 : 0.0;
            Double averageProcessingTime = inferenceHistoryRepository.getAverageProcessingTime(userId, startTime, endTime);
            Double averageDetectedObjects = inferenceHistoryRepository.getAverageDetectedObjects(userId, startTime, endTime);

            // 模型使用统计
            List<Object[]> modelStats = inferenceHistoryRepository.getModelUsageStats(userId, startTime, endTime);
            List<InferenceHistoryDto.ModelUsageStats> modelUsageStats = modelStats.stream()
                    .map(stat -> InferenceHistoryDto.ModelUsageStats.builder()
                            .modelName((String) stat[0])
                            .usageCount((Long) stat[1])
                            .usagePercentage(totalInferences > 0 ? (Long) stat[1] * 100.0 / totalInferences : 0.0)
                            .build())
                    .collect(Collectors.toList());

            // 类型使用统计
            List<Object[]> typeStats = inferenceHistoryRepository.getTypeUsageStats(userId, startTime, endTime);
            List<InferenceHistoryDto.TypeUsageStats> typeUsageStats = typeStats.stream()
                    .map(stat -> InferenceHistoryDto.TypeUsageStats.builder()
                            .inferenceType((String) stat[0])
                            .usageCount((Long) stat[1])
                            .usagePercentage(totalInferences > 0 ? (Long) stat[1] * 100.0 / totalInferences : 0.0)
                            .build())
                    .collect(Collectors.toList());

            // 每日统计
            List<Object[]> dailyStats = inferenceHistoryRepository.getDailyInferenceStats(userId, startTime, endTime);
            List<InferenceHistoryDto.DailyStats> dailyStatsList = dailyStats.stream()
                    .map(stat -> {
                        // 处理日期类型转换
                        Object dateObj = stat[0];
                        LocalDateTime date;
                        if (dateObj instanceof java.sql.Date) {
                            date = ((java.sql.Date) dateObj).toLocalDate().atStartOfDay();
                        } else if (dateObj instanceof LocalDateTime) {
                            date = (LocalDateTime) dateObj;
                        } else {
                            date = LocalDateTime.now(); // 默认值
                        }
                        
                        return InferenceHistoryDto.DailyStats.builder()
                                .date(date)
                                .inferenceCount((Long) stat[1])
                                .build();
                    })
                    .collect(Collectors.toList());

            // 最近推理记录
            Pageable recentPageable = PageRequest.of(0, 10);
            List<InferenceHistory> recentHistories = inferenceHistoryRepository.findRecentInferences(recentPageable);
            List<InferenceHistoryDto.InferenceHistoryResponse> recentInferences = recentHistories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return InferenceHistoryDto.InferenceHistoryStats.builder()
                    .totalInferences(totalInferences)
                    .successfulInferences(successfulInferences)
                    .failedInferences(failedInferences)
                    .successRate(successRate)
                    .averageProcessingTime(averageProcessingTime)
                    .averageDetectedObjects(averageDetectedObjects)
                    .modelUsageStats(modelUsageStats)
                    .typeUsageStats(typeUsageStats)
                    .dailyStats(dailyStatsList)
                    .recentInferences(recentInferences)
                    .build();

        } catch (Exception e) {
            log.error("获取推理历史统计失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取推理历史统计失败: " + e.getMessage());
        }
    }

    /**
     * 清理推理历史记录
     */
    @Transactional
    public void cleanupInferenceHistory(InferenceHistoryDto.CleanupInferenceHistoryRequest request) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(request.getDaysToKeep());
            
            if (request.getPhysicalDelete()) {
                // 物理删除
                if (request.getOnlyDeleteFailed()) {
                    // 只删除失败的记录
                    List<InferenceHistory> failedRecords = inferenceHistoryRepository
                        .findByStatusAndCreatedAtBeforeAndIsDeletedFalse("FAILED", cutoffDate);
                    inferenceHistoryRepository.deleteAll(failedRecords);
                } else {
                    // 删除所有记录
                    List<InferenceHistory> recordsToDelete = inferenceHistoryRepository
                        .findByCreatedAtBeforeAndIsDeletedFalse(cutoffDate);
                    inferenceHistoryRepository.deleteAll(recordsToDelete);
                }
            } else {
                // 软删除
                if (request.getOnlyDeleteFailed()) {
                    // 只软删除失败的记录
                    List<InferenceHistory> failedRecords = inferenceHistoryRepository
                        .findByStatusAndCreatedAtBeforeAndIsDeletedFalse("FAILED", cutoffDate);
                    failedRecords.forEach(record -> {
                        record.setIsDeleted(true);
                        record.setUpdatedAt(LocalDateTime.now());
                    });
                    inferenceHistoryRepository.saveAll(failedRecords);
                } else {
                    // 软删除所有记录
                    List<InferenceHistory> recordsToDelete = inferenceHistoryRepository
                        .findByCreatedAtBeforeAndIsDeletedFalse(cutoffDate);
                    recordsToDelete.forEach(record -> {
                        record.setIsDeleted(true);
                        record.setUpdatedAt(LocalDateTime.now());
                    });
                    inferenceHistoryRepository.saveAll(recordsToDelete);
                }
            }
            
            log.info("清理推理历史记录完成: daysToKeep={}, onlyDeleteFailed={}, physicalDelete={}", 
                    request.getDaysToKeep(), request.getOnlyDeleteFailed(), request.getPhysicalDelete());

        } catch (Exception e) {
            log.error("清理推理历史记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("清理推理历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 转换实体为响应DTO
     */
    private InferenceHistoryDto.InferenceHistoryResponse convertToResponse(InferenceHistory history) {
        return InferenceHistoryDto.InferenceHistoryResponse.builder()
                .id(history.getId())
                .taskId(history.getTaskId())
                .inferenceType(history.getInferenceType())
                .modelName(history.getModelName())
                .confidenceThreshold(history.getConfidenceThreshold())
                .originalFilename(history.getOriginalFilename())
                .fileSize(history.getFileSize())
                .imagePath(history.getImagePath())
                .inferenceResult(history.getInferenceResult())
                .detectedObjectsCount(history.getDetectedObjectsCount())
                .processingTime(history.getProcessingTime())
                .status(history.getStatus())
                .errorMessage(history.getErrorMessage())
                .userId(history.getUserId())
                .username(history.getUsername())
                .deviceInfo(history.getDeviceInfo())
                .inferenceServer(history.getInferenceServer())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .tags(history.getTags())
                .notes(history.getNotes())
                .resultRating(history.getResultRating())
                .isFavorite(history.getIsFavorite())
                .build();
    }

    /**
     * 检查是否有复杂搜索条件
     */
    private boolean hasComplexSearchCriteria(InferenceHistoryDto.SearchInferenceHistoryRequest request) {
        return request.getKeyword() != null ||
               request.getInferenceType() != null ||
               request.getModelName() != null ||
               request.getStatus() != null ||
               request.getUserId() != null ||
               request.getUsername() != null ||
               request.getStartTime() != null ||
               request.getEndTime() != null ||
               request.getIsFavorite() != null ||
               request.getMinRating() != null;
    }
}