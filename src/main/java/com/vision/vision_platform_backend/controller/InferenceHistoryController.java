package com.vision.vision_platform_backend.controller;

import com.vision.vision_platform_backend.service.InferenceHistoryService;
import com.vision.vision_platform_backend.dto.InferenceHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 推理历史记录控制器
 */
@RestController
@RequestMapping("/api/inference-history")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InferenceHistoryController {

    private final InferenceHistoryService inferenceHistoryService;

    /**
     * 创建推理历史记录
     */
    @PostMapping
    public ResponseEntity<?> createInferenceHistory(
            @RequestBody InferenceHistoryDto.CreateInferenceHistoryRequest request) {
        try {
            InferenceHistoryDto.InferenceHistoryResponse response = 
                    inferenceHistoryService.createInferenceHistory(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "推理历史记录创建成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("创建推理历史记录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "创建推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据ID获取推理历史记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInferenceHistoryById(@PathVariable UUID id) {
        try {
            return inferenceHistoryService.getInferenceHistoryById(id)
                    .map(history -> ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "获取推理历史记录成功",
                            "data", history
                    )))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("获取推理历史记录失败: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "获取推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据任务ID获取推理历史记录
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getInferenceHistoryByTaskId(@PathVariable String taskId) {
        try {
            return inferenceHistoryService.getInferenceHistoryByTaskId(taskId)
                    .map(history -> ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "获取推理历史记录成功",
                            "data", history
                    )))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("获取推理历史记录失败: taskId={}, error={}", taskId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "获取推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 搜索推理历史记录
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchInferenceHistory(
            @RequestBody InferenceHistoryDto.SearchInferenceHistoryRequest request) {
        try {
            InferenceHistoryDto.InferenceHistoryPageResponse response = 
                    inferenceHistoryService.searchInferenceHistory(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "搜索推理历史记录成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("搜索推理历史记录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "搜索推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取推理历史记录列表（简化版搜索）
     */
    @GetMapping
    public ResponseEntity<?> getInferenceHistoryList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String inferenceType,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(required = false) Integer minRating) {
        try {
            InferenceHistoryDto.SearchInferenceHistoryRequest request = 
                    InferenceHistoryDto.SearchInferenceHistoryRequest.builder()
                            .page(page)
                            .size(size)
                            .sortBy(sortBy)
                            .sortDirection(sortDirection)
                            .keyword(keyword)
                            .inferenceType(inferenceType)
                            .modelName(modelName)
                            .status(status)
                            .userId(userId)
                            .username(username)
                            .startTime(startTime)
                            .endTime(endTime)
                            .isFavorite(isFavorite)
                            .minRating(minRating)
                            .build();

            InferenceHistoryDto.InferenceHistoryPageResponse response = 
                    inferenceHistoryService.searchInferenceHistory(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取推理历史记录列表成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("获取推理历史记录列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "获取推理历史记录列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新推理历史记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInferenceHistory(
            @PathVariable UUID id,
            @RequestBody InferenceHistoryDto.UpdateInferenceHistoryRequest request) {
        try {
            InferenceHistoryDto.InferenceHistoryResponse response = 
                    inferenceHistoryService.updateInferenceHistory(id, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "更新推理历史记录成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("更新推理历史记录失败: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "更新推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 删除推理历史记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInferenceHistory(@PathVariable UUID id) {
        try {
            inferenceHistoryService.deleteInferenceHistory(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "删除推理历史记录成功"
            ));
        } catch (Exception e) {
            log.error("删除推理历史记录失败: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "删除推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 批量删除推理历史记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteInferenceHistory(@RequestBody List<UUID> ids) {
        try {
            inferenceHistoryService.batchDeleteInferenceHistory(ids);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "批量删除推理历史记录成功"
            ));
        } catch (Exception e) {
            log.error("批量删除推理历史记录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "批量删除推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取推理历史统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getInferenceHistoryStats(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            InferenceHistoryDto.InferenceHistoryStats stats = 
                    inferenceHistoryService.getInferenceHistoryStats(userId, startTime, endTime);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取推理历史统计成功",
                    "data", stats
            ));
        } catch (Exception e) {
            log.error("获取推理历史统计失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "获取推理历史统计失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 清理推理历史记录
     */
    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupInferenceHistory(
            @RequestBody InferenceHistoryDto.CleanupInferenceHistoryRequest request) {
        try {
            inferenceHistoryService.cleanupInferenceHistory(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "清理推理历史记录成功"
            ));
        } catch (Exception e) {
            log.error("清理推理历史记录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "清理推理历史记录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 标记为收藏/取消收藏
     */
    @PutMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable UUID id, @RequestParam Boolean favorite) {
        try {
            InferenceHistoryDto.UpdateInferenceHistoryRequest request = 
                    InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                            .isFavorite(favorite)
                            .build();
            
            InferenceHistoryDto.InferenceHistoryResponse response = 
                    inferenceHistoryService.updateInferenceHistory(id, request);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", favorite ? "添加收藏成功" : "取消收藏成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("切换收藏状态失败: id={}, favorite={}, error={}", id, favorite, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "切换收藏状态失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 评分推理结果
     */
    @PutMapping("/{id}/rating")
    public ResponseEntity<?> rateInferenceResult(@PathVariable UUID id, @RequestParam Integer rating) {
        try {
            if (rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "评分必须在1-5之间"
                ));
            }

            InferenceHistoryDto.UpdateInferenceHistoryRequest request = 
                    InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                            .resultRating(rating)
                            .build();
            
            InferenceHistoryDto.InferenceHistoryResponse response = 
                    inferenceHistoryService.updateInferenceHistory(id, request);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "评分成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("评分失败: id={}, rating={}, error={}", id, rating, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "评分失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 添加备注
     */
    @PutMapping("/{id}/notes")
    public ResponseEntity<?> addNotes(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        try {
            String notes = request.get("notes");
            
            InferenceHistoryDto.UpdateInferenceHistoryRequest updateRequest = 
                    InferenceHistoryDto.UpdateInferenceHistoryRequest.builder()
                            .notes(notes)
                            .build();
            
            InferenceHistoryDto.InferenceHistoryResponse response = 
                    inferenceHistoryService.updateInferenceHistory(id, updateRequest);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "添加备注成功",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("添加备注失败: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "添加备注失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取最近的推理记录
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentInferenceHistory(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) UUID userId) {
        try {
            InferenceHistoryDto.SearchInferenceHistoryRequest request = 
                    InferenceHistoryDto.SearchInferenceHistoryRequest.builder()
                            .page(0)
                            .size(limit)
                            .sortBy("createdAt")
                            .sortDirection("desc")
                            .userId(userId)
                            .build();

            InferenceHistoryDto.InferenceHistoryPageResponse response = 
                    inferenceHistoryService.searchInferenceHistory(request);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取最近推理记录成功",
                    "data", response.getContent()
            ));
        } catch (Exception e) {
            log.error("获取最近推理记录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "获取最近推理记录失败: " + e.getMessage()
            ));
        }
    }
}