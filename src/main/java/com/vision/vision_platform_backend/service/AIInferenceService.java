package com.vision.vision_platform_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.AIInferenceDto;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import com.vision.vision_platform_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI推理服务
 * 负责与MindSpore推理服务器通信
 */
@Service
public class AIInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(AIInferenceService.class);

    @Value("${ai.inference.server.url:http://localhost:8000}")
    private String inferenceServerUrl;

    @Value("${ai.inference.timeout:30000}")
    private int timeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final InferenceHistoryService inferenceHistoryService;

    public AIInferenceService(InferenceHistoryService inferenceHistoryService) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.inferenceHistoryService = inferenceHistoryService;
    }

    /**
     * 检查推理服务器健康状态
     */
    public boolean checkHealth() {
        try {
            String url = inferenceServerUrl + "/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("推理服务器健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取健康状态详细信息
     */
    public Map<String, Object> getHealthStatus() {
        try {
            String url = inferenceServerUrl + "/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                Map<String, Object> errorStatus = new HashMap<>();
                errorStatus.put("status", "unhealthy");
                errorStatus.put("error", "服务器响应异常");
                return errorStatus;
            }
        } catch (Exception e) {
            logger.error("推理服务器健康检查失败: {}", e.getMessage());
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "unhealthy");
            errorStatus.put("error", e.getMessage());
            return errorStatus;
        }
    }

    /**
     * 单张图片推理
     */
    public AIInferenceDto.InferenceResponse inference(AIInferenceDto.InferenceRequest request) {
        String taskId = "task_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        long startTime = System.currentTimeMillis();
        
        try {
            String url = inferenceServerUrl + "/inference";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<AIInferenceDto.InferenceRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                AIInferenceDto.InferenceResponse inferenceResponse = 
                    objectMapper.readValue(response.getBody(), AIInferenceDto.InferenceResponse.class);
                
                // 记录推理历史
                recordInferenceHistory(taskId, "single", request, inferenceResponse, 
                    null, null, startTime, "SUCCESS", null);
                
                return inferenceResponse;
            } else {
                logger.error("推理请求失败，状态码: {}", response.getStatusCode());
                AIInferenceDto.InferenceResponse errorResponse = 
                    createErrorResponse("推理请求失败", request.getTask(), request.getModelName());
                
                // 记录失败的推理历史
                recordInferenceHistory(taskId, "single", request, errorResponse, 
                    null, null, startTime, "FAILED", "推理请求失败");
                
                return errorResponse;
            }
        } catch (ResourceAccessException e) {
            logger.error("连接推理服务器失败: {}", e.getMessage());
            AIInferenceDto.InferenceResponse errorResponse = 
                createErrorResponse("连接推理服务器失败", request.getTask(), request.getModelName());
            
            // 记录失败的推理历史
            recordInferenceHistory(taskId, "single", request, errorResponse, 
                null, null, startTime, "FAILED", "连接推理服务器失败");
            
            return errorResponse;
        } catch (Exception e) {
            logger.error("推理过程中发生错误: {}", e.getMessage(), e);
            AIInferenceDto.InferenceResponse errorResponse = 
                createErrorResponse("推理过程中发生错误: " + e.getMessage(), request.getTask(), request.getModelName());
            
            // 记录失败的推理历史
            recordInferenceHistory(taskId, "single", request, errorResponse, 
                null, null, startTime, "FAILED", "推理过程中发生错误: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 单张图片推理（带文件上传）
     */
    public AIInferenceDto.InferenceResponse inferSingle(MultipartFile file, AIInferenceDto.InferenceRequest request) {
        String taskId = "task_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        long startTime = System.currentTimeMillis();
        
        try {
            // 这里可以实现文件上传到推理服务器的逻辑
            // 目前使用模拟推理
            logger.info("执行单张图片推理，文件: {}, 任务: {}, 模型: {}", 
                file.getOriginalFilename(), request.getTask(), request.getModelName());
            
            AIInferenceDto.InferenceResponse response = new AIInferenceDto.InferenceResponse();
            response.setSuccess(true);
            response.setTask(request.getTask());
            response.setModelName(request.getModelName() != null ? request.getModelName() : "default_model");
            response.setTimestamp(LocalDateTime.now());
            response.setInferenceTime(150.0);
            
            // 模拟检测结果
            if ("detect".equals(request.getTask())) {
                response.setDetections(List.of(
                    createMockDetectionResult("person", 0.95, 100, 50, 200, 300),
                    createMockDetectionResult("car", 0.87, 300, 200, 150, 100)
                ));
            }
            
            // 记录推理历史
            recordInferenceHistory(taskId, "single_upload", request, response, 
                file.getOriginalFilename(), file.getSize(), startTime, "SUCCESS", null);
            
            return response;
        } catch (Exception e) {
            logger.error("单张图片推理过程中发生错误: {}", e.getMessage(), e);
            AIInferenceDto.InferenceResponse errorResponse = 
                createErrorResponse("推理过程中发生错误: " + e.getMessage(), request.getTask(), request.getModelName());
            
            // 记录失败的推理历史
            recordInferenceHistory(taskId, "single_upload", request, errorResponse, 
                file.getOriginalFilename(), file.getSize(), startTime, "FAILED", 
                "推理过程中发生错误: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 批量图片推理（带文件上传）
     */
    public Map<String, Object> inferBatch(List<MultipartFile> files, AIInferenceDto.BatchInferenceRequest request) {
        String taskId = "task_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("执行批量图片推理，文件数量: {}, 任务: {}, 模型: {}", 
                files.size(), request.getTask(), request.getModelName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total_files", files.size());
            response.put("processed_files", files.size());
            response.put("failed_files", 0);
            response.put("processing_time", 500.0);
            
            // 模拟每个文件的推理结果
            List<Map<String, Object>> results = new ArrayList<>();
            for (MultipartFile file : files) {
                Map<String, Object> fileResult = new HashMap<>();
                fileResult.put("filename", file.getOriginalFilename());
                fileResult.put("success", true);
                fileResult.put("processing_time", 150.0);
                
                if ("detect".equals(request.getTask())) {
                    fileResult.put("detections", List.of(
                        createMockDetectionResult("person", 0.92, 120, 60, 180, 280),
                        createMockDetectionResult("vehicle", 0.85, 320, 180, 160, 120)
                    ));
                }
                
                results.add(fileResult);
                
                // 为每个文件记录推理历史
                recordBatchFileInferenceHistory(taskId, request, file, fileResult, startTime);
            }
            
            response.put("results", results);
            
            // 记录批量推理总体历史
            recordBatchInferenceHistory(taskId, request, response, files, startTime, "SUCCESS", null);
            
            return response;
        } catch (Exception e) {
            logger.error("批量推理过程中发生错误: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            // 记录失败的批量推理历史
            recordBatchInferenceHistory(taskId, request, errorResponse, files, startTime, "FAILED", 
                "批量推理过程中发生错误: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 批量图片推理
     */
    public List<AIInferenceDto.InferenceResponse> batchInference(AIInferenceDto.BatchInferenceRequest request) {
        try {
            String url = inferenceServerUrl + "/batch_inference";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<AIInferenceDto.BatchInferenceRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                TypeReference<List<AIInferenceDto.InferenceResponse>> typeRef = 
                    new TypeReference<List<AIInferenceDto.InferenceResponse>>() {};
                return objectMapper.readValue(response.getBody(), typeRef);
            } else {
                logger.error("批量推理请求失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("批量推理请求失败");
            }
        } catch (Exception e) {
            logger.error("批量推理过程中发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("批量推理过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取可用模型列表
     */
    public List<AIInferenceDto.ModelInfo> getModels() {
        try {
            String url = inferenceServerUrl + "/models";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                TypeReference<List<AIInferenceDto.ModelInfo>> typeRef = 
                    new TypeReference<List<AIInferenceDto.ModelInfo>>() {};
                return objectMapper.readValue(response.getBody(), typeRef);
            } else {
                logger.error("获取模型列表失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("获取模型列表失败");
            }
        } catch (Exception e) {
            logger.error("获取模型列表时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("获取模型列表时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取模型信息
     */
    public AIInferenceDto.ModelInfo getModelInfo(String modelName) {
        try {
            String url = inferenceServerUrl + "/models/" + modelName;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return objectMapper.readValue(response.getBody(), AIInferenceDto.ModelInfo.class);
            } else {
                logger.error("获取模型信息失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("获取模型信息失败");
            }
        } catch (Exception e) {
            logger.error("获取模型信息时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("获取模型信息时发生错误: " + e.getMessage());
        }
    }

    /**
     * 加载模型
     */
    public boolean loadModel(String modelName) {
        try {
            String url = inferenceServerUrl + "/models/load";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            AIInferenceDto.ModelLoadRequest request = new AIInferenceDto.ModelLoadRequest();
            request.setModelName(modelName);
            
            HttpEntity<AIInferenceDto.ModelLoadRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("加载模型时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 卸载模型
     */
    public boolean unloadModel(String modelName) {
        try {
            String url = inferenceServerUrl + "/models/unload";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            AIInferenceDto.ModelUnloadRequest request = new AIInferenceDto.ModelUnloadRequest();
            request.setModelName(modelName);
            
            HttpEntity<AIInferenceDto.ModelUnloadRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("卸载模型时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取已加载的模型
     */
    public List<AIInferenceDto.ModelInfo> getLoadedModels() {
        try {
            String url = inferenceServerUrl + "/models/loaded";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                TypeReference<List<AIInferenceDto.ModelInfo>> typeRef = 
                    new TypeReference<List<AIInferenceDto.ModelInfo>>() {};
                return objectMapper.readValue(response.getBody(), typeRef);
            } else {
                logger.error("获取已加载模型失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("获取已加载模型失败");
            }
        } catch (Exception e) {
            logger.error("获取已加载模型时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("获取已加载模型时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取推理统计信息
     */
    public AIInferenceDto.InferenceStats getStats() {
        try {
            String url = inferenceServerUrl + "/stats";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return objectMapper.readValue(response.getBody(), AIInferenceDto.InferenceStats.class);
            } else {
                logger.error("获取统计信息失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("获取统计信息失败");
            }
        } catch (Exception e) {
            logger.error("获取统计信息时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("获取统计信息时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取配置信息
     */
    public Map<String, Object> getConfig() {
        try {
            String url = inferenceServerUrl + "/config";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                TypeReference<Map<String, Object>> typeRef = 
                    new TypeReference<Map<String, Object>>() {};
                return objectMapper.readValue(response.getBody(), typeRef);
            } else {
                logger.error("获取配置信息失败，状态码: {}", response.getStatusCode());
                throw new RuntimeException("获取配置信息失败");
            }
        } catch (Exception e) {
            logger.error("获取配置信息时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("获取配置信息时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新配置
     */
    public boolean updateConfig(AIInferenceDto.ConfigUpdateRequest request) {
        try {
            String url = inferenceServerUrl + "/config";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<AIInferenceDto.ConfigUpdateRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("更新配置时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取推理统计信息
     */
    public AIInferenceDto.InferenceStats getInferenceStats() {
        return getStats();
    }

    /**
     * 重置推理统计信息
     */
    public boolean resetInferenceStats() {
        try {
            String url = inferenceServerUrl + "/stats/reset";
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("重置统计信息时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 创建错误响应
     */
    private AIInferenceDto.InferenceResponse createErrorResponse(String errorMessage, String task, String modelName) {
        AIInferenceDto.InferenceResponse response = new AIInferenceDto.InferenceResponse();
        response.setSuccess(false);
        response.setTask(task);
        response.setModelName(modelName);
        response.setErrorMessage(errorMessage);
        response.setTimestamp(java.time.LocalDateTime.now());
        return response;
    }

    /**
     * 创建模拟检测结果
     */
    private AIInferenceDto.DetectionResult createMockDetectionResult(String className, double confidence, 
                                                                    int x, int y, int width, int height) {
        AIInferenceDto.DetectionResult detection = new AIInferenceDto.DetectionResult();
        detection.setClassName(className);
        detection.setConfidence(confidence);
        detection.setBbox(List.of((double)x, (double)y, (double)(x + width), (double)(y + height)));
        return detection;
    }

    /**
     * 记录推理历史
     */
    private void recordInferenceHistory(String taskId, String inferenceType, 
                                      AIInferenceDto.InferenceRequest request,
                                      AIInferenceDto.InferenceResponse response,
                                      String originalFilename, Long fileSize,
                                      long startTime, String status, String errorMessage) {
        try {
            long processingTime = System.currentTimeMillis() - startTime;
            
            InferenceHistoryDto.CreateInferenceHistoryRequest historyRequest = 
                InferenceHistoryDto.CreateInferenceHistoryRequest.builder()
                    .taskId(taskId)
                    .inferenceType(inferenceType)
                    .modelName(request.getModelName() != null ? request.getModelName() : "default_model")
                    .confidenceThreshold(request.getConfidenceThreshold())
                    .originalFilename(originalFilename)
                    .fileSize(fileSize)
                    .imagePath(null) // 单张推理请求中没有imagePath字段
                    .inferenceResult(response)
                    .detectedObjectsCount(response.getDetections() != null ? response.getDetections().size() : 0)
                    .processingTime(processingTime)
                    .status(status)
                    .errorMessage(errorMessage)
                    .userId(getCurrentUserId())
                    .username(getCurrentUsername())
                    .deviceInfo(getDeviceInfo())
                    .inferenceServer(inferenceServerUrl)
                    .build();
            
            inferenceHistoryService.createInferenceHistory(historyRequest);
            logger.debug("推理历史记录已保存: taskId={}", taskId);
        } catch (Exception e) {
            logger.error("保存推理历史记录失败: taskId={}, error={}", taskId, e.getMessage(), e);
        }
    }

    /**
     * 记录批量推理历史
     */
    private void recordBatchInferenceHistory(String taskId, AIInferenceDto.BatchInferenceRequest request,
                                           Map<String, Object> response, List<MultipartFile> files,
                                           long startTime, String status, String errorMessage) {
        try {
            long processingTime = System.currentTimeMillis() - startTime;
            
            // 计算总的检测对象数量
            int totalDetectedObjects = 0;
            if (response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                for (Map<String, Object> result : results) {
                    if (result.containsKey("detections")) {
                        List<?> detections = (List<?>) result.get("detections");
                        totalDetectedObjects += detections.size();
                    }
                }
            }
            
            InferenceHistoryDto.CreateInferenceHistoryRequest historyRequest = 
                InferenceHistoryDto.CreateInferenceHistoryRequest.builder()
                    .taskId(taskId)
                    .inferenceType("batch")
                    .modelName(request.getModelName() != null ? request.getModelName() : "default_model")
                    .confidenceThreshold(request.getConfidenceThreshold())
                    .originalFilename("batch_" + files.size() + "_files")
                    .fileSize(files.stream().mapToLong(MultipartFile::getSize).sum())
                    .imagePath(null)
                    .inferenceResult(response)
                    .detectedObjectsCount(totalDetectedObjects)
                    .processingTime(processingTime)
                    .status(status)
                    .errorMessage(errorMessage)
                    .userId(getCurrentUserId())
                    .username(getCurrentUsername())
                    .deviceInfo(getDeviceInfo())
                    .inferenceServer(inferenceServerUrl)
                    .tags("batch_inference")
                    .build();
            
            inferenceHistoryService.createInferenceHistory(historyRequest);
            logger.debug("批量推理历史记录已保存: taskId={}", taskId);
        } catch (Exception e) {
            logger.error("保存批量推理历史记录失败: taskId={}, error={}", taskId, e.getMessage(), e);
        }
    }

    /**
     * 记录批量推理中单个文件的历史
     */
    private void recordBatchFileInferenceHistory(String batchTaskId, AIInferenceDto.BatchInferenceRequest request,
                                               MultipartFile file, Map<String, Object> fileResult,
                                               long startTime) {
        try {
            String fileTaskId = batchTaskId + "_" + file.getOriginalFilename();
            long processingTime = System.currentTimeMillis() - startTime;
            
            // 计算检测对象数量
            int detectedObjects = 0;
            if (fileResult.containsKey("detections")) {
                List<?> detections = (List<?>) fileResult.get("detections");
                detectedObjects = detections.size();
            }
            
            String status = (Boolean) fileResult.getOrDefault("success", false) ? "SUCCESS" : "FAILED";
            
            InferenceHistoryDto.CreateInferenceHistoryRequest historyRequest = 
                InferenceHistoryDto.CreateInferenceHistoryRequest.builder()
                    .taskId(fileTaskId)
                    .inferenceType("batch_file")
                    .modelName(request.getModelName() != null ? request.getModelName() : "default_model")
                    .confidenceThreshold(request.getConfidenceThreshold())
                    .originalFilename(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .imagePath(null)
                    .inferenceResult(fileResult)
                    .detectedObjectsCount(detectedObjects)
                    .processingTime(processingTime)
                    .status(status)
                    .errorMessage(status.equals("FAILED") ? (String) fileResult.get("error") : null)
                    .userId(getCurrentUserId())
                    .username(getCurrentUsername())
                    .deviceInfo(getDeviceInfo())
                    .inferenceServer(inferenceServerUrl)
                    .tags("batch_file,parent_task:" + batchTaskId)
                    .build();
            
            inferenceHistoryService.createInferenceHistory(historyRequest);
            logger.debug("批量文件推理历史记录已保存: fileTaskId={}", fileTaskId);
        } catch (Exception e) {
            logger.error("保存批量文件推理历史记录失败: file={}, error={}", 
                file.getOriginalFilename(), e.getMessage(), e);
        }
    }

    /**
     * 获取设备信息
     */
    private String getDeviceInfo() {
        try {
            return "Server: " + System.getProperty("os.name") + " " + System.getProperty("os.version") +
                   ", Java: " + System.getProperty("java.version");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 获取当前认证用户的ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                // 这里可以通过用户名查询用户ID，暂时使用一个默认ID
                // 在实际应用中，应该通过UserService根据用户名获取用户ID
                return 1L; // 临时使用测试用户ID
            }
        } catch (Exception e) {
            logger.warn("获取当前用户ID失败: {}", e.getMessage());
        }
        // 如果无法获取认证用户，返回默认ID
        return 1L;
    }

    /**
     * 获取当前认证用户的用户名
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            logger.warn("获取当前用户名失败: {}", e.getMessage());
        }
        return "system";
    }
}