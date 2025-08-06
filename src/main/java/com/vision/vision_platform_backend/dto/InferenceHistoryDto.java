package com.vision.vision_platform_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 推理历史记录相关DTO
 */
public class InferenceHistoryDto {

    /**
     * 推理历史记录响应DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InferenceHistoryResponse {
        private Long id;
        private String taskId;
        private String inferenceType;
        private String modelName;
        private Double confidenceThreshold;
        private String originalFilename;
        private Long fileSize;
        private String imagePath;
        private Object inferenceResult; // JSON对象
        private Integer detectedObjectsCount;
        private Long processingTime;
        private String status;
        private String errorMessage;
        private Long userId;
        private String username;
        private String deviceInfo;
        private String inferenceServer;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
        
        private String tags;
        private String notes;
        private Integer resultRating;
        private Boolean isFavorite;
    }

    /**
     * 推理历史记录创建请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateInferenceHistoryRequest {
        private String taskId;
        private String inferenceType;
        private String modelName;
        private Double confidenceThreshold;
        private String originalFilename;
        private Long fileSize;
        private String imagePath;
        private Object inferenceResult;
        private Integer detectedObjectsCount;
        private Long processingTime;
        private String status;
        private String errorMessage;
        private Long userId;
        private String username;
        private String deviceInfo;
        private String inferenceServer;
        private String tags;
        private String notes;
    }

    /**
     * 推理历史记录更新请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateInferenceHistoryRequest {
        private String status;
        private String errorMessage;
        private Object inferenceResult;
        private Integer detectedObjectsCount;
        private Long processingTime;
        private String tags;
        private String notes;
        private Integer resultRating;
        private Boolean isFavorite;
    }

    /**
     * 推理历史记录搜索请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchInferenceHistoryRequest {
        private String keyword;
        private String inferenceType;
        private String modelName;
        private String status;
        private Long userId;
        private String username;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
        
        private Boolean isFavorite;
        private Integer minRating;
        private List<String> tags;
        
        // 分页参数
        private Integer page = 0;
        private Integer size = 20;
        private String sortBy = "createdAt";
        private String sortDirection = "desc";
    }

    /**
     * 推理历史记录分页响应DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InferenceHistoryPageResponse {
        private List<InferenceHistoryResponse> content;
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Boolean first;
        private Boolean last;
        private Boolean empty;
    }

    /**
     * 推理历史统计DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InferenceHistoryStats {
        private Long totalInferences;
        private Long successfulInferences;
        private Long failedInferences;
        private Double successRate;
        private Double averageProcessingTime;
        private Double averageDetectedObjects;
        private List<ModelUsageStats> modelUsageStats;
        private List<TypeUsageStats> typeUsageStats;
        private List<DailyStats> dailyStats;
        private List<InferenceHistoryResponse> recentInferences;
        private List<InferenceHistoryResponse> slowestInferences;
        private List<InferenceHistoryResponse> mostDetectedInferences;
    }

    /**
     * 模型使用统计DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelUsageStats {
        private String modelName;
        private Long usageCount;
        private Double usagePercentage;
    }

    /**
     * 推理类型使用统计DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TypeUsageStats {
        private String inferenceType;
        private Long usageCount;
        private Double usagePercentage;
    }

    /**
     * 每日统计DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyStats {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime date;
        private Long inferenceCount;
    }

    /**
     * 批量操作请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatchOperationRequest {
        private List<Long> ids;
        private String operation; // delete, favorite, unfavorite, rate
        private Integer rating; // 用于批量评分
        private String tags; // 用于批量添加标签
    }

    /**
     * 推理历史导出请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExportInferenceHistoryRequest {
        private List<Long> ids; // 如果为空，则导出所有符合条件的记录
        private String format; // json, csv, excel
        private SearchInferenceHistoryRequest searchCriteria;
        private Boolean includeImages; // 是否包含图片文件
    }

    /**
     * 推理历史导入请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImportInferenceHistoryRequest {
        private String format; // json, csv
        private String data; // 导入的数据内容
        private Boolean overwriteExisting; // 是否覆盖已存在的记录
    }

    /**
     * 推理历史清理请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CleanupInferenceHistoryRequest {
        private Integer daysToKeep; // 保留多少天的记录
        private Boolean onlyDeleteFailed; // 是否只删除失败的记录
        private Boolean physicalDelete; // 是否物理删除（否则软删除）
    }

    /**
     * 推理历史备份请求DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BackupInferenceHistoryRequest {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
        
        private String backupFormat; // json, sql
        private Boolean includeImages;
        private String backupLocation; // local, cloud
    }
}