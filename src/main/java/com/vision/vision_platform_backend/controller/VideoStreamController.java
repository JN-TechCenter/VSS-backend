package com.vision.vision_platform_backend.controller;

import com.vision.vision_platform_backend.dto.VideoStreamDto;
import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.service.VideoStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video-streams")
@CrossOrigin(origins = "*")
public class VideoStreamController {
    
    @Autowired
    private VideoStreamService videoStreamService;
    
    // 创建视频流 - 需要管理员权限
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createVideoStream(@Valid @RequestBody VideoStreamDto dto) {
        try {
            VideoStreamDto created = videoStreamService.createVideoStream(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "视频流创建成功");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("创建视频流失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 更新视频流 - 需要管理员权限
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVideoStream(@PathVariable Long id, @Valid @RequestBody VideoStreamDto dto) {
        try {
            VideoStreamDto updated = videoStreamService.updateVideoStream(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "视频流更新成功");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("更新视频流失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 删除视频流 - 需要管理员权限
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVideoStream(@PathVariable Long id) {
        try {
            videoStreamService.deleteVideoStream(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "视频流删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("删除视频流失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 获取视频流详情
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getVideoStream(@PathVariable Long id) {
        try {
            VideoStreamDto stream = videoStreamService.getVideoStream(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取视频流成功");
            response.put("data", stream);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取视频流失败: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // 根据流ID获取视频流
    @GetMapping("/stream/{streamId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getVideoStreamByStreamId(@PathVariable String streamId) {
        try {
            VideoStreamDto stream = videoStreamService.getVideoStreamByStreamId(streamId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取视频流成功");
            response.put("data", stream);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取视频流失败: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // 获取视频流列表（分页）
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getVideoStreams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<VideoStreamDto> streams = videoStreamService.getVideoStreams(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取视频流列表成功");
            response.put("data", streams.getContent());
            response.put("totalElements", streams.getTotalElements());
            response.put("totalPages", streams.getTotalPages());
            response.put("currentPage", streams.getNumber());
            response.put("pageSize", streams.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取视频流列表失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 搜索视频流
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> searchVideoStreams(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
            Page<VideoStreamDto> streams = videoStreamService.searchVideoStreams(keyword, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "搜索视频流成功");
            response.put("data", streams.getContent());
            response.put("totalElements", streams.getTotalElements());
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("搜索视频流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 根据状态获取视频流
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getVideoStreamsByStatus(
            @PathVariable VideoStream.StreamStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
            Page<VideoStreamDto> streams = videoStreamService.getVideoStreamsByStatus(status, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取" + status + "状态视频流成功");
            response.put("data", streams.getContent());
            response.put("totalElements", streams.getTotalElements());
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取视频流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 根据类型获取视频流
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getVideoStreamsByType(
            @PathVariable VideoStream.StreamType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
            Page<VideoStreamDto> streams = videoStreamService.getVideoStreamsByType(type, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取" + type + "类型视频流成功");
            response.put("data", streams.getContent());
            response.put("totalElements", streams.getTotalElements());
            response.put("type", type);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取视频流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 根据设备获取视频流
    @GetMapping("/device/{deviceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getVideoStreamsByDevice(@PathVariable Long deviceId) {
        try {
            List<VideoStreamDto> streams = videoStreamService.getVideoStreamsByDevice(deviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取设备视频流成功");
            response.put("data", streams);
            response.put("count", streams.size());
            response.put("deviceId", deviceId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取设备视频流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 获取活跃的视频流
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<?> getActiveStreams() {
        try {
            List<VideoStreamDto> streams = videoStreamService.getActiveStreams();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取活跃视频流成功");
            response.put("data", streams);
            response.put("count", streams.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取活跃视频流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 获取有错误的视频流
    @GetMapping("/errors")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<?> getErrorStreams() {
        try {
            List<VideoStreamDto> streams = videoStreamService.getErrorStreams();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取错误视频流成功");
            response.put("data", streams);
            response.put("count", streams.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取错误视频流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 启动视频流 - 需要操作员权限
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<?> startStream(@PathVariable Long id) {
        try {
            VideoStreamDto stream = videoStreamService.startStream(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "视频流启动成功");
            response.put("data", stream);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("启动视频流失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 停止视频流 - 需要操作员权限
    @PostMapping("/{id}/stop")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<?> stopStream(@PathVariable Long id) {
        try {
            VideoStreamDto stream = videoStreamService.stopStream(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "视频流停止成功");
            response.put("data", stream);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("停止视频流失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 重启视频流 - 需要操作员权限
    @PostMapping("/{id}/restart")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<?> restartStream(@PathVariable Long id) {
        try {
            VideoStreamDto stream = videoStreamService.restartStream(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "视频流重启成功");
            response.put("data", stream);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("重启视频流失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 更新流状态 - 内部API，无需认证
    @PutMapping("/stream/{streamId}/status")
    public ResponseEntity<?> updateStreamStatus(
            @PathVariable String streamId, 
            @RequestParam VideoStream.StreamStatus status) {
        try {
            VideoStreamDto stream = videoStreamService.updateStreamStatus(streamId, status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "流状态更新成功");
            response.put("data", stream);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("更新流状态失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 更新流监控信息 - 内部API，无需认证
    @PutMapping("/stream/{streamId}/metrics")
    public ResponseEntity<?> updateStreamMetrics(
            @PathVariable String streamId,
            @RequestParam(required = false) Double cpuUsage,
            @RequestParam(required = false) Double memoryUsage,
            @RequestParam(required = false) Double networkBandwidth) {
        try {
            videoStreamService.updateStreamMetrics(streamId, cpuUsage, memoryUsage, networkBandwidth);
            Map<String, String> response = new HashMap<>();
            response.put("message", "流监控信息更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("更新流监控信息失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 增加观看人数 - 内部API，无需认证
    @PostMapping("/stream/{streamId}/viewer/join")
    public ResponseEntity<?> joinViewer(@PathVariable String streamId) {
        try {
            videoStreamService.incrementViewerCount(streamId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "观看人数增加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("增加观看人数失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 减少观看人数 - 内部API，无需认证
    @PostMapping("/stream/{streamId}/viewer/leave")
    public ResponseEntity<?> leaveViewer(@PathVariable String streamId) {
        try {
            videoStreamService.decrementViewerCount(streamId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "观看人数减少成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("减少观看人数失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 记录流错误 - 内部API，无需认证
    @PostMapping("/stream/{streamId}/error")
    public ResponseEntity<?> recordStreamError(
            @PathVariable String streamId, 
            @RequestParam String error) {
        try {
            videoStreamService.recordStreamError(streamId, error);
            Map<String, String> response = new HashMap<>();
            response.put("message", "流错误记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("记录流错误失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 获取流统计信息
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<?> getStreamStatistics() {
        try {
            Map<String, Object> stats = videoStreamService.getStreamStatistics();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取流统计信息成功");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("获取流统计信息失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 清理长时间未活跃的流 - 需要管理员权限
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cleanupInactiveStreams(@RequestParam(defaultValue = "30") int minutes) {
        try {
            List<VideoStreamDto> cleaned = videoStreamService.cleanupInactiveStreams(minutes);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "清理未活跃流成功");
            response.put("data", cleaned);
            response.put("count", cleaned.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("清理未活跃流失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 批量更新状态 - 需要管理员权限
    @PutMapping("/batch/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> batchUpdateStatus(
            @RequestParam List<Long> ids,
            @RequestParam VideoStream.StreamStatus status) {
        try {
            videoStreamService.batchUpdateStatus(ids, status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量更新状态成功");
            response.put("count", ids.size());
            response.put("status", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("批量更新状态失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 批量删除 - 需要管理员权限
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> batchDelete(@RequestParam List<Long> ids) {
        try {
            videoStreamService.batchDelete(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量删除成功");
            response.put("count", ids.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("批量删除失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // 辅助方法：创建错误响应
    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, status);
    }
}