package com.vision.vision_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI推理历史记录实体
 */
@Entity
@Table(name = "inference_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InferenceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 推理任务ID（唯一标识）
     */
    @Column(name = "task_id", unique = true, nullable = false)
    private String taskId;

    /**
     * 推理类型：single（单张图片）、batch（批量推理）、realtime（实时推理）
     */
    @Column(name = "inference_type", nullable = false)
    private String inferenceType;

    /**
     * 使用的模型名称
     */
    @Column(name = "model_name", nullable = false)
    private String modelName;

    /**
     * 置信度阈值
     */
    @Column(name = "confidence_threshold")
    private Double confidenceThreshold;

    /**
     * 原始图片文件名
     */
    @Column(name = "original_filename")
    private String originalFilename;

    /**
     * 图片文件大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 图片存储路径
     */
    @Column(name = "image_path")
    private String imagePath;

    /**
     * 推理结果（JSON格式存储）
     */
    @Column(name = "inference_result", columnDefinition = "TEXT")
    private String inferenceResult;

    /**
     * 检测到的目标数量
     */
    @Column(name = "detected_objects_count")
    private Integer detectedObjectsCount;

    /**
     * 推理处理时间（毫秒）
     */
    @Column(name = "processing_time")
    private Long processingTime;

    /**
     * 推理状态：SUCCESS、FAILED、PROCESSING
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * 错误信息（如果推理失败）
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 用户ID（执行推理的用户）
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 设备信息（如果是从特定设备推理）
     */
    @Column(name = "device_info")
    private String deviceInfo;

    /**
     * 推理服务器信息
     */
    @Column(name = "inference_server")
    private String inferenceServer;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 标签（用于分类和搜索）
     */
    @Column(name = "tags")
    private String tags;

    /**
     * 备注信息
     */
    @Column(name = "notes")
    private String notes;

    /**
     * 是否已删除（软删除）
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    /**
     * 推理结果评分（用户可以对结果进行评分）
     */
    @Column(name = "result_rating")
    private Integer resultRating;

    /**
     * 是否收藏
     */
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (isFavorite == null) {
            isFavorite = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 推理状态枚举
     */
    public enum InferenceStatus {
        PROCESSING("PROCESSING", "处理中"),
        SUCCESS("SUCCESS", "成功"),
        FAILED("FAILED", "失败"),
        CANCELLED("CANCELLED", "已取消");

        private final String code;
        private final String description;

        InferenceStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 推理类型枚举
     */
    public enum InferenceType {
        SINGLE("single", "单张图片推理"),
        BATCH("batch", "批量推理"),
        REALTIME("realtime", "实时推理"),
        VIDEO("video", "视频推理");

        private final String code;
        private final String description;

        InferenceType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}