package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.dto.VideoStreamDto;
import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.repository.VideoStreamRepository;
import com.vision.vision_platform_backend.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoStreamService {
    
    @Autowired
    private VideoStreamRepository videoStreamRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    // 创建视频流
    public VideoStreamDto createVideoStream(VideoStreamDto dto) {
        // 检查流ID是否已存在
        if (videoStreamRepository.existsByStreamId(dto.getStreamId())) {
            throw new RuntimeException("流ID已存在: " + dto.getStreamId());
        }
        
        VideoStream videoStream = dto.toEntity();
        
        // 设置关联设备
        if (dto.getDeviceId() != null) {
            Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new RuntimeException("设备不存在: " + dto.getDeviceId()));
            videoStream.setDevice(device);
        }
        
        // 设置默认值
        videoStream.setStatus(VideoStream.StreamStatus.INACTIVE);
        videoStream.setViewerCount(0L);
        videoStream.setErrorCount(0);
        videoStream.setCreatedAt(LocalDateTime.now());
        videoStream.setUpdatedAt(LocalDateTime.now());
        
        VideoStream saved = videoStreamRepository.save(videoStream);
        return new VideoStreamDto(saved);
    }
    
    // 更新视频流
    public VideoStreamDto updateVideoStream(Long id, VideoStreamDto dto) {
        VideoStream existing = videoStreamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + id));
        
        // 检查流ID是否被其他流使用
        if (!existing.getStreamId().equals(dto.getStreamId()) && 
            videoStreamRepository.existsByStreamId(dto.getStreamId())) {
            throw new RuntimeException("流ID已存在: " + dto.getStreamId());
        }
        
        // 更新字段
        existing.setStreamId(dto.getStreamId());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setType(dto.getType());
        existing.setSourceUrl(dto.getSourceUrl());
        existing.setOutputUrl(dto.getOutputUrl());
        existing.setProtocol(dto.getProtocol());
        existing.setQuality(dto.getQuality());
        existing.setWidth(dto.getWidth());
        existing.setHeight(dto.getHeight());
        existing.setFrameRate(dto.getFrameRate());
        existing.setBitrate(dto.getBitrate());
        
        // 更新设备关联
        if (dto.getDeviceId() != null) {
            Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new RuntimeException("设备不存在: " + dto.getDeviceId()));
            existing.setDevice(device);
        } else {
            existing.setDevice(null);
        }
        
        // 更新配置
        existing.setRecordingEnabled(dto.getRecordingEnabled());
        existing.setRecordingPath(dto.getRecordingPath());
        existing.setRecordingDuration(dto.getRecordingDuration());
        existing.setTranscodeEnabled(dto.getTranscodeEnabled());
        existing.setTranscodeFormat(dto.getTranscodeFormat());
        existing.setTranscodeQuality(dto.getTranscodeQuality());
        
        existing.setUpdatedAt(LocalDateTime.now());
        
        VideoStream saved = videoStreamRepository.save(existing);
        return new VideoStreamDto(saved);
    }
    
    // 删除视频流
    public void deleteVideoStream(Long id) {
        VideoStream videoStream = videoStreamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + id));
        
        // 如果流正在运行，先停止
        if (videoStream.isActive()) {
            stopStream(id);
        }
        
        videoStreamRepository.delete(videoStream);
    }
    
    // 获取视频流详情
    public VideoStreamDto getVideoStream(Long id) {
        VideoStream videoStream = videoStreamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + id));
        return new VideoStreamDto(videoStream);
    }
    
    // 根据流ID获取视频流
    public VideoStreamDto getVideoStreamByStreamId(String streamId) {
        VideoStream videoStream = videoStreamRepository.findByStreamId(streamId)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + streamId));
        return new VideoStreamDto(videoStream);
    }
    
    // 获取视频流列表（分页）
    public Page<VideoStreamDto> getVideoStreams(Pageable pageable) {
        Page<VideoStream> streams = videoStreamRepository.findAll(pageable);
        return streams.map(VideoStreamDto::new);
    }
    
    // 搜索视频流
    public Page<VideoStreamDto> searchVideoStreams(String keyword, Pageable pageable) {
        Page<VideoStream> streams = videoStreamRepository.searchByKeyword(keyword, pageable);
        return streams.map(VideoStreamDto::new);
    }
    
    // 根据状态获取视频流
    public Page<VideoStreamDto> getVideoStreamsByStatus(VideoStream.StreamStatus status, Pageable pageable) {
        Page<VideoStream> streams = videoStreamRepository.findByStatus(status, pageable);
        return streams.map(VideoStreamDto::new);
    }
    
    // 根据类型获取视频流
    public Page<VideoStreamDto> getVideoStreamsByType(VideoStream.StreamType type, Pageable pageable) {
        Page<VideoStream> streams = videoStreamRepository.findByType(type, pageable);
        return streams.map(VideoStreamDto::new);
    }
    
    // 根据设备获取视频流
    public List<VideoStreamDto> getVideoStreamsByDevice(Long deviceId) {
        List<VideoStream> streams = videoStreamRepository.findByDeviceId(deviceId);
        return streams.stream().map(VideoStreamDto::new).collect(Collectors.toList());
    }
    
    // 获取活跃的视频流
    public List<VideoStreamDto> getActiveStreams() {
        List<VideoStream> streams = videoStreamRepository.findActiveStreams();
        return streams.stream().map(VideoStreamDto::new).collect(Collectors.toList());
    }
    
    // 获取有错误的视频流
    public List<VideoStreamDto> getErrorStreams() {
        List<VideoStream> streams = videoStreamRepository.findErrorStreams();
        return streams.stream().map(VideoStreamDto::new).collect(Collectors.toList());
    }
    
    // 启动视频流
    public VideoStreamDto startStream(Long id) {
        VideoStream videoStream = videoStreamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + id));
        
        if (videoStream.isActive()) {
            throw new RuntimeException("视频流已经在运行中");
        }
        
        try {
            // 这里应该调用实际的流媒体服务启动流
            // 目前只是模拟状态变更
            videoStream.setStatus(VideoStream.StreamStatus.STARTING);
            videoStream.setUpdatedAt(LocalDateTime.now());
            videoStreamRepository.save(videoStream);
            
            // 模拟启动过程
            Thread.sleep(1000);
            
            videoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
            videoStream.setLastActiveTime(LocalDateTime.now());
            videoStream.clearError();
            videoStream.setUpdatedAt(LocalDateTime.now());
            
            VideoStream saved = videoStreamRepository.save(videoStream);
            return new VideoStreamDto(saved);
            
        } catch (Exception e) {
            videoStream.recordError("启动失败: " + e.getMessage());
            videoStreamRepository.save(videoStream);
            throw new RuntimeException("启动视频流失败: " + e.getMessage());
        }
    }
    
    // 停止视频流
    public VideoStreamDto stopStream(Long id) {
        VideoStream videoStream = videoStreamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + id));
        
        if (videoStream.isInactive()) {
            throw new RuntimeException("视频流已经停止");
        }
        
        try {
            // 这里应该调用实际的流媒体服务停止流
            // 目前只是模拟状态变更
            videoStream.setStatus(VideoStream.StreamStatus.STOPPING);
            videoStream.setUpdatedAt(LocalDateTime.now());
            videoStreamRepository.save(videoStream);
            
            // 模拟停止过程
            Thread.sleep(500);
            
            videoStream.setStatus(VideoStream.StreamStatus.INACTIVE);
            videoStream.setViewerCount(0L);
            videoStream.setUpdatedAt(LocalDateTime.now());
            
            VideoStream saved = videoStreamRepository.save(videoStream);
            return new VideoStreamDto(saved);
            
        } catch (Exception e) {
            videoStream.recordError("停止失败: " + e.getMessage());
            videoStreamRepository.save(videoStream);
            throw new RuntimeException("停止视频流失败: " + e.getMessage());
        }
    }
    
    // 重启视频流
    public VideoStreamDto restartStream(Long id) {
        try {
            stopStream(id);
            Thread.sleep(1000);
            return startStream(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("重启视频流被中断: " + e.getMessage());
        }
    }
    
    // 更新流状态
    public VideoStreamDto updateStreamStatus(String streamId, VideoStream.StreamStatus status) {
        VideoStream videoStream = videoStreamRepository.findByStreamId(streamId)
            .orElseThrow(() -> new RuntimeException("视频流不存在: " + streamId));
        
        videoStream.setStatus(status);
        videoStream.setUpdatedAt(LocalDateTime.now());
        
        if (status == VideoStream.StreamStatus.ACTIVE) {
            videoStream.updateLastActiveTime();
        }
        
        VideoStream saved = videoStreamRepository.save(videoStream);
        return new VideoStreamDto(saved);
    }
    
    // 更新流监控信息
    public void updateStreamMetrics(String streamId, Double cpuUsage, Double memoryUsage, Double networkBandwidth) {
        VideoStream videoStream = videoStreamRepository.findByStreamId(streamId).orElse(null);
        if (videoStream != null) {
            videoStream.setCpuUsage(cpuUsage);
            videoStream.setMemoryUsage(memoryUsage);
            videoStream.setNetworkBandwidth(networkBandwidth);
            videoStream.updateLastActiveTime();
            videoStream.setUpdatedAt(LocalDateTime.now());
            videoStreamRepository.save(videoStream);
        }
    }
    
    // 增加观看人数
    public void incrementViewerCount(String streamId) {
        VideoStream videoStream = videoStreamRepository.findByStreamId(streamId).orElse(null);
        if (videoStream != null) {
            videoStream.incrementViewerCount();
            videoStream.updateLastActiveTime();
            videoStreamRepository.save(videoStream);
        }
    }
    
    // 减少观看人数
    public void decrementViewerCount(String streamId) {
        VideoStream videoStream = videoStreamRepository.findByStreamId(streamId).orElse(null);
        if (videoStream != null) {
            videoStream.decrementViewerCount();
            videoStreamRepository.save(videoStream);
        }
    }
    
    // 记录流错误
    public void recordStreamError(String streamId, String error) {
        VideoStream videoStream = videoStreamRepository.findByStreamId(streamId).orElse(null);
        if (videoStream != null) {
            videoStream.recordError(error);
            videoStreamRepository.save(videoStream);
        }
    }
    
    // 获取流统计信息
    public Map<String, Object> getStreamStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 状态统计
        List<Object[]> statusCounts = videoStreamRepository.countByStatus();
        Map<String, Long> statusStats = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusStats.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("statusCounts", statusStats);
        
        // 类型统计
        List<Object[]> typeCounts = videoStreamRepository.countByType();
        Map<String, Long> typeStats = new HashMap<>();
        for (Object[] row : typeCounts) {
            typeStats.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("typeCounts", typeStats);
        
        // 总体统计
        stats.put("totalStreams", videoStreamRepository.count());
        stats.put("activeStreams", videoStreamRepository.findActiveStreams().size());
        stats.put("errorStreams", videoStreamRepository.findErrorStreams().size());
        stats.put("totalViewers", videoStreamRepository.getTotalViewerCount());
        stats.put("averageCpuUsage", videoStreamRepository.getAverageCpuUsage());
        stats.put("averageMemoryUsage", videoStreamRepository.getAverageMemoryUsage());
        stats.put("totalNetworkBandwidth", videoStreamRepository.getTotalNetworkBandwidth());
        
        return stats;
    }
    
    // 清理长时间未活跃的流
    public List<VideoStreamDto> cleanupInactiveStreams(int minutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutes);
        List<VideoStream> inactiveStreams = videoStreamRepository.findInactiveStreams(threshold);
        
        List<VideoStreamDto> cleaned = new ArrayList<>();
        for (VideoStream stream : inactiveStreams) {
            try {
                stream.setStatus(VideoStream.StreamStatus.INACTIVE);
                stream.setViewerCount(0L);
                stream.recordError("长时间未活跃，自动停止");
                videoStreamRepository.save(stream);
                cleaned.add(new VideoStreamDto(stream));
            } catch (Exception e) {
                // 记录清理失败的流
            }
        }
        
        return cleaned;
    }
    
    // 批量操作
    public void batchUpdateStatus(List<Long> ids, VideoStream.StreamStatus status) {
        List<VideoStream> streams = videoStreamRepository.findAllById(ids);
        for (VideoStream stream : streams) {
            stream.setStatus(status);
            stream.setUpdatedAt(LocalDateTime.now());
        }
        videoStreamRepository.saveAll(streams);
    }
    
    public void batchDelete(List<Long> ids) {
        List<VideoStream> streams = videoStreamRepository.findAllById(ids);
        for (VideoStream stream : streams) {
            if (stream.isActive()) {
                stream.setStatus(VideoStream.StreamStatus.STOPPING);
            }
        }
        videoStreamRepository.deleteAllById(ids);
    }
}