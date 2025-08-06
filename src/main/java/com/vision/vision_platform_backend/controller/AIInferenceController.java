package com.vision.vision_platform_backend.controller;

import com.vision.vision_platform_backend.dto.AIInferenceDto;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import com.vision.vision_platform_backend.service.AIInferenceService;
import com.vision.vision_platform_backend.service.InferenceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AI推理控制器
 * 集成MindSpore推理服务
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIInferenceController {

    @Autowired
    private AIInferenceService aiInferenceService;

    @Autowired
    private InferenceHistoryService inferenceHistoryService;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> health = aiInferenceService.getHealthStatus();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 单张图片推理
     */
    @PostMapping("/infer")
    public ResponseEntity<AIInferenceDto.InferenceResponse> inferSingle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "task", defaultValue = "detect") String task,
            @RequestParam(value = "model_name", required = false) String modelName,
            @RequestParam(value = "confidence_threshold", defaultValue = "0.5") Double confidenceThreshold,
            @RequestParam(value = "nms_threshold", defaultValue = "0.4") Double nmsThreshold) {
        
        try {
            AIInferenceDto.InferenceRequest request = new AIInferenceDto.InferenceRequest();
            request.setTask(task);
            request.setModelName(modelName);
            request.setConfidenceThreshold(confidenceThreshold);
            request.setNmsThreshold(nmsThreshold);
            
            AIInferenceDto.InferenceResponse response = aiInferenceService.inferSingle(file, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AIInferenceDto.InferenceResponse errorResponse = new AIInferenceDto.InferenceResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 批量图片推理
     */
    @PostMapping("/infer/batch")
    public ResponseEntity<Map<String, Object>> inferBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "task", defaultValue = "detect") String task,
            @RequestParam(value = "model_name", required = false) String modelName,
            @RequestParam(value = "confidence_threshold", defaultValue = "0.5") Double confidenceThreshold,
            @RequestParam(value = "nms_threshold", defaultValue = "0.4") Double nmsThreshold) {
        
        try {
            AIInferenceDto.BatchInferenceRequest request = new AIInferenceDto.BatchInferenceRequest();
            request.setTask(task);
            request.setModelName(modelName);
            request.setConfidenceThreshold(confidenceThreshold);
            request.setNmsThreshold(nmsThreshold);
            
            Map<String, Object> response = aiInferenceService.inferBatch(files, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 获取可用模型列表
     */
    @GetMapping("/models")
    public ResponseEntity<List<AIInferenceDto.ModelInfo>> getModels() {
        try {
            List<AIInferenceDto.ModelInfo> models = aiInferenceService.getModels();
            return ResponseEntity.ok(models);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 获取特定模型信息
     */
    @GetMapping("/models/{modelName}")
    public ResponseEntity<AIInferenceDto.ModelInfo> getModelInfo(@PathVariable String modelName) {
        try {
            AIInferenceDto.ModelInfo modelInfo = aiInferenceService.getModelInfo(modelName);
            if (modelInfo != null) {
                return ResponseEntity.ok(modelInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 获取已加载的模型列表
     */
    @GetMapping("/models/loaded")
    public ResponseEntity<List<AIInferenceDto.ModelInfo>> getLoadedModels() {
        try {
            List<AIInferenceDto.ModelInfo> models = aiInferenceService.getLoadedModels();
            return ResponseEntity.ok(models);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 加载模型
     */
    @PostMapping("/models/load")
    public ResponseEntity<Map<String, String>> loadModel(@RequestBody AIInferenceDto.ModelLoadRequest request) {
        try {
            boolean success = aiInferenceService.loadModel(request.getModelName());
            if (success) {
                return ResponseEntity.ok(Map.of("message", "模型 " + request.getModelName() + " 加载成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "模型加载失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 卸载模型
     */
    @PostMapping("/models/unload")
    public ResponseEntity<Map<String, String>> unloadModel(@RequestBody AIInferenceDto.ModelUnloadRequest request) {
        try {
            boolean success = aiInferenceService.unloadModel(request.getModelName());
            if (success) {
                return ResponseEntity.ok(Map.of("message", "模型 " + request.getModelName() + " 卸载成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "模型卸载失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取推理统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<AIInferenceDto.InferenceStats> getStats() {
        try {
            AIInferenceDto.InferenceStats stats = aiInferenceService.getInferenceStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 重置推理统计信息
     */
    @PostMapping("/stats/reset")
    public ResponseEntity<Map<String, String>> resetStats() {
        try {
            aiInferenceService.resetInferenceStats();
            return ResponseEntity.ok(Map.of("message", "统计信息已重置"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取推理服务配置
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> config = aiInferenceService.getConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新推理服务配置
     */
    @PostMapping("/config/update")
    public ResponseEntity<Map<String, String>> updateConfig(@RequestBody AIInferenceDto.ConfigUpdateRequest request) {
        try {
            boolean success = aiInferenceService.updateConfig(request);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "配置更新成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "配置更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== 推理历史记录相关API ====================

    /**
     * 搜索推理历史记录
     */
    @PostMapping("/history/search")
    public ResponseEntity<InferenceHistoryDto.InferenceHistoryPageResponse> searchInferenceHistory(
            @RequestBody InferenceHistoryDto.SearchInferenceHistoryRequest request) {
        try {
            InferenceHistoryDto.InferenceHistoryPageResponse history = 
                inferenceHistoryService.searchInferenceHistory(request);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 获取推理历史记录详情
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<InferenceHistoryDto.InferenceHistoryResponse> getInferenceHistory(@PathVariable Long id) {
        try {
            Optional<InferenceHistoryDto.InferenceHistoryResponse> historyOpt = 
                inferenceHistoryService.getInferenceHistoryById(id);
            if (historyOpt.isPresent()) {
                return ResponseEntity.ok(historyOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 根据任务ID获取推理历史记录
     */
    @GetMapping("/history/task/{taskId}")
    public ResponseEntity<InferenceHistoryDto.InferenceHistoryResponse> getInferenceHistoryByTaskId(
            @PathVariable String taskId) {
        try {
            Optional<InferenceHistoryDto.InferenceHistoryResponse> historyOpt = 
                inferenceHistoryService.getInferenceHistoryByTaskId(taskId);
            if (historyOpt.isPresent()) {
                return ResponseEntity.ok(historyOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 删除推理历史记录
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Map<String, String>> deleteInferenceHistory(@PathVariable Long id) {
        try {
            inferenceHistoryService.deleteInferenceHistory(id);
            return ResponseEntity.ok(Map.of("message", "推理历史记录删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 批量删除推理历史记录
     */
    @DeleteMapping("/history/batch")
    public ResponseEntity<Map<String, String>> batchDeleteInferenceHistory(
            @RequestBody InferenceHistoryDto.BatchOperationRequest request) {
        try {
            inferenceHistoryService.batchDeleteInferenceHistory(request.getIds());
            return ResponseEntity.ok(Map.of(
                "message", "成功删除 " + request.getIds().size() + " 条推理历史记录",
                "deletedCount", String.valueOf(request.getIds().size())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取推理历史统计信息
     */
    @GetMapping("/history/stats")
    public ResponseEntity<InferenceHistoryDto.InferenceHistoryStats> getInferenceHistoryStats(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        try {
            // 这里简化处理，如果需要更复杂的统计，可以调用其他服务方法
            InferenceHistoryDto.InferenceHistoryStats stats = 
                InferenceHistoryDto.InferenceHistoryStats.builder()
                    .totalInferences(0L)
                    .successfulInferences(0L)
                    .failedInferences(0L)
                    .successRate(0.0)
                    .averageProcessingTime(0.0)
                    .averageDetectedObjects(0.0)
                    .build();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 清理推理历史记录
     */
    @PostMapping("/history/cleanup")
    public ResponseEntity<Map<String, String>> cleanupInferenceHistory(
            @RequestBody InferenceHistoryDto.CleanupInferenceHistoryRequest request) {
        try {
            inferenceHistoryService.cleanupInferenceHistory(request);
            return ResponseEntity.ok(Map.of(
                "message", "推理历史记录清理完成",
                "cleanedCount", "0"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 标记/取消收藏推理历史记录
     */
    @PostMapping("/history/{id}/favorite")
    public ResponseEntity<Map<String, String>> toggleFavoriteInferenceHistory(
            @PathVariable Long id,
            @RequestParam(value = "favorite", defaultValue = "true") Boolean favorite) {
        try {
            InferenceHistoryDto.UpdateInferenceHistoryRequest request = 
                InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                    .isFavorite(favorite)
                    .build();
            
            InferenceHistoryDto.InferenceHistoryResponse response = 
                inferenceHistoryService.updateInferenceHistory(id, request);
            if (response != null) {
                String action = favorite ? "收藏" : "取消收藏";
                return ResponseEntity.ok(Map.of("message", "推理历史记录" + action + "成功"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 为推理历史记录评分
     */
    @PostMapping("/history/{id}/rating")
    public ResponseEntity<Map<String, String>> rateInferenceHistory(
            @PathVariable Long id,
            @RequestParam("rating") Integer rating) {
        try {
            if (rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "评分必须在1-5之间"));
            }
            
            InferenceHistoryDto.UpdateInferenceHistoryRequest request = 
                InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                    .resultRating(rating)
                    .build();
            
            InferenceHistoryDto.InferenceHistoryResponse response = 
                inferenceHistoryService.updateInferenceHistory(id, request);
            if (response != null) {
                return ResponseEntity.ok(Map.of("message", "推理历史记录评分成功"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 为推理历史记录添加备注
     */
    @PostMapping("/history/{id}/notes")
    public ResponseEntity<Map<String, String>> addNotesToInferenceHistory(
            @PathVariable Long id,
            @RequestParam("notes") String notes) {
        try {
            InferenceHistoryDto.UpdateInferenceHistoryRequest request = 
                InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                    .notes(notes)
                    .build();
            
            InferenceHistoryDto.InferenceHistoryResponse response = 
                inferenceHistoryService.updateInferenceHistory(id, request);
            if (response != null) {
                return ResponseEntity.ok(Map.of("message", "推理历史记录备注添加成功"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}