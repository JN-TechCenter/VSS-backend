package com.vision.vision_platform_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI推理相关的数据传输对象
 */
public class AIInferenceDto {

    /**
     * 推理请求
     */
    public static class InferenceRequest {
        private String task = "detect"; // detect, segment
        @JsonProperty("model_name")
        private String modelName;
        @JsonProperty("image_data")
        private String imageData;
        @JsonProperty("confidence_threshold")
        private Double confidenceThreshold = 0.5;
        @JsonProperty("nms_threshold")
        private Double nmsThreshold = 0.4;
        @JsonProperty("max_detections")
        private Integer maxDetections = 100;
        @JsonProperty("return_visualization")
        private Boolean returnVisualization = true;

        // Getters and Setters
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }
        
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        
        public String getImageData() { return imageData; }
        public void setImageData(String imageData) { this.imageData = imageData; }
        
        public Double getConfidenceThreshold() { return confidenceThreshold; }
        public void setConfidenceThreshold(Double confidenceThreshold) { this.confidenceThreshold = confidenceThreshold; }
        
        public Double getNmsThreshold() { return nmsThreshold; }
        public void setNmsThreshold(Double nmsThreshold) { this.nmsThreshold = nmsThreshold; }
        
        public Integer getMaxDetections() { return maxDetections; }
        public void setMaxDetections(Integer maxDetections) { this.maxDetections = maxDetections; }
        
        public Boolean getReturnVisualization() { return returnVisualization; }
        public void setReturnVisualization(Boolean returnVisualization) { this.returnVisualization = returnVisualization; }
    }

    /**
     * 批量推理请求
     */
    public static class BatchInferenceRequest {
        private String task = "detect";
        @JsonProperty("model_name")
        private String modelName;
        private List<String> images;
        @JsonProperty("confidence_threshold")
        private Double confidenceThreshold = 0.5;
        @JsonProperty("nms_threshold")
        private Double nmsThreshold = 0.4;
        @JsonProperty("max_detections")
        private Integer maxDetections = 100;
        @JsonProperty("return_visualization")
        private Boolean returnVisualization = true;
        @JsonProperty("batch_size")
        private Integer batchSize;

        // Getters and Setters
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }
        
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
        
        public Double getConfidenceThreshold() { return confidenceThreshold; }
        public void setConfidenceThreshold(Double confidenceThreshold) { this.confidenceThreshold = confidenceThreshold; }
        
        public Double getNmsThreshold() { return nmsThreshold; }
        public void setNmsThreshold(Double nmsThreshold) { this.nmsThreshold = nmsThreshold; }
        
        public Integer getMaxDetections() { return maxDetections; }
        public void setMaxDetections(Integer maxDetections) { this.maxDetections = maxDetections; }
        
        public Boolean getReturnVisualization() { return returnVisualization; }
        public void setReturnVisualization(Boolean returnVisualization) { this.returnVisualization = returnVisualization; }
        
        public Integer getBatchSize() { return batchSize; }
        public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
    }

    /**
     * 目标检测结果
     */
    public static class DetectionResult {
        private List<Double> bbox; // [x1, y1, x2, y2]
        private Double confidence;
        @JsonProperty("class_id")
        private Integer classId;
        @JsonProperty("class_name")
        private String className;

        // Getters and Setters
        public List<Double> getBbox() { return bbox; }
        public void setBbox(List<Double> bbox) { this.bbox = bbox; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public Integer getClassId() { return classId; }
        public void setClassId(Integer classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
    }

    /**
     * 图像分割结果
     */
    public static class SegmentationResult {
        @JsonProperty("class_id")
        private Integer classId;
        @JsonProperty("class_name")
        private String className;
        private Double confidence;
        private String mask; // base64编码的掩码
        private List<Double> bbox; // [x1, y1, x2, y2]

        // Getters and Setters
        public Integer getClassId() { return classId; }
        public void setClassId(Integer classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public String getMask() { return mask; }
        public void setMask(String mask) { this.mask = mask; }
        
        public List<Double> getBbox() { return bbox; }
        public void setBbox(List<Double> bbox) { this.bbox = bbox; }
    }

    /**
     * 推理响应
     */
    public static class InferenceResponse {
        private Boolean success;
        private String task;
        @JsonProperty("model_name")
        private String modelName;
        @JsonProperty("inference_time")
        private Double inferenceTime;
        @JsonProperty("image_size")
        private List<Integer> imageSize; // [width, height]
        private List<DetectionResult> detections;
        private List<SegmentationResult> segmentations;
        @JsonProperty("visualization_image")
        private String visualizationImage; // base64编码的可视化图片
        @JsonProperty("error_message")
        private String errorMessage;
        private LocalDateTime timestamp;

        // Getters and Setters
        public Boolean getSuccess() { return success; }
        public void setSuccess(Boolean success) { this.success = success; }
        
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }
        
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        
        public Double getInferenceTime() { return inferenceTime; }
        public void setInferenceTime(Double inferenceTime) { this.inferenceTime = inferenceTime; }
        
        public List<Integer> getImageSize() { return imageSize; }
        public void setImageSize(List<Integer> imageSize) { this.imageSize = imageSize; }
        
        public List<DetectionResult> getDetections() { return detections; }
        public void setDetections(List<DetectionResult> detections) { this.detections = detections; }
        
        public List<SegmentationResult> getSegmentations() { return segmentations; }
        public void setSegmentations(List<SegmentationResult> segmentations) { this.segmentations = segmentations; }
        
        public String getVisualizationImage() { return visualizationImage; }
        public void setVisualizationImage(String visualizationImage) { this.visualizationImage = visualizationImage; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 模型信息
     */
    public static class ModelInfo {
        private String name;
        private String type; // detection, segmentation
        private String description;
        private String version;
        @JsonProperty("input_size")
        private List<Integer> inputSize; // [width, height]
        @JsonProperty("class_names")
        private List<String> classNames;
        @JsonProperty("is_loaded")
        private Boolean isLoaded;
        @JsonProperty("load_time")
        private LocalDateTime loadTime;
        @JsonProperty("model_size")
        private Long modelSize; // 模型文件大小（字节）

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public List<Integer> getInputSize() { return inputSize; }
        public void setInputSize(List<Integer> inputSize) { this.inputSize = inputSize; }
        
        public List<String> getClassNames() { return classNames; }
        public void setClassNames(List<String> classNames) { this.classNames = classNames; }
        
        public Boolean getIsLoaded() { return isLoaded; }
        public void setIsLoaded(Boolean isLoaded) { this.isLoaded = isLoaded; }
        
        public LocalDateTime getLoadTime() { return loadTime; }
        public void setLoadTime(LocalDateTime loadTime) { this.loadTime = loadTime; }
        
        public Long getModelSize() { return modelSize; }
        public void setModelSize(Long modelSize) { this.modelSize = modelSize; }
    }

    /**
     * 推理统计信息
     */
    public static class InferenceStats {
        @JsonProperty("total_requests")
        private Long totalRequests;
        @JsonProperty("successful_requests")
        private Long successfulRequests;
        @JsonProperty("failed_requests")
        private Long failedRequests;
        @JsonProperty("average_inference_time")
        private Double averageInferenceTime;
        @JsonProperty("total_inference_time")
        private Double totalInferenceTime;
        @JsonProperty("start_time")
        private LocalDateTime startTime;
        @JsonProperty("last_request_time")
        private LocalDateTime lastRequestTime;
        @JsonProperty("requests_per_minute")
        private Double requestsPerMinute;

        // Getters and Setters
        public Long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
        
        public Long getSuccessfulRequests() { return successfulRequests; }
        public void setSuccessfulRequests(Long successfulRequests) { this.successfulRequests = successfulRequests; }
        
        public Long getFailedRequests() { return failedRequests; }
        public void setFailedRequests(Long failedRequests) { this.failedRequests = failedRequests; }
        
        public Double getAverageInferenceTime() { return averageInferenceTime; }
        public void setAverageInferenceTime(Double averageInferenceTime) { this.averageInferenceTime = averageInferenceTime; }
        
        public Double getTotalInferenceTime() { return totalInferenceTime; }
        public void setTotalInferenceTime(Double totalInferenceTime) { this.totalInferenceTime = totalInferenceTime; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getLastRequestTime() { return lastRequestTime; }
        public void setLastRequestTime(LocalDateTime lastRequestTime) { this.lastRequestTime = lastRequestTime; }
        
        public Double getRequestsPerMinute() { return requestsPerMinute; }
        public void setRequestsPerMinute(Double requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    }

    /**
     * 模型加载请求
     */
    public static class ModelLoadRequest {
        @JsonProperty("model_name")
        private String modelName;

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
    }

    /**
     * 模型卸载请求
     */
    public static class ModelUnloadRequest {
        @JsonProperty("model_name")
        private String modelName;

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
    }

    /**
     * 配置更新请求
     */
    public static class ConfigUpdateRequest {
        private String section;
        private Map<String, Object> values;

        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        
        public Map<String, Object> getValues() { return values; }
        public void setValues(Map<String, Object> values) { this.values = values; }
    }
}