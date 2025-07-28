package com.vision.vision_platform_backend.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_streams")
@EntityListeners(AuditingEntityListener.class)
public class VideoStream {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String streamId;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StreamType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StreamStatus status;
    
    @Column(nullable = false)
    private String sourceUrl;
    
    private String outputUrl;
    
    @Enumerated(EnumType.STRING)
    private StreamProtocol protocol;
    
    @Enumerated(EnumType.STRING)
    private StreamQuality quality;
    
    private Integer width;
    private Integer height;
    private Integer frameRate;
    private Integer bitrate;
    
    // 关联设备
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    // 录制配置
    private Boolean recordingEnabled;
    private String recordingPath;
    private Integer recordingDuration; // 分钟
    
    // 转码配置
    private Boolean transcodeEnabled;
    private String transcodeFormat;
    private String transcodeQuality;
    
    // 监控信息
    private LocalDateTime lastActiveTime;
    private Long viewerCount;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double networkBandwidth;
    
    // 错误信息
    private String lastError;
    private LocalDateTime lastErrorTime;
    private Integer errorCount;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String updatedBy;
    
    // 枚举定义
    public enum StreamType {
        RTSP, RTMP, HTTP, HLS, WEBRTC, FILE
    }
    
    public enum StreamStatus {
        INACTIVE, STARTING, ACTIVE, STOPPING, ERROR, MAINTENANCE
    }
    
    public enum StreamProtocol {
        TCP, UDP, HTTP, HTTPS, WEBSOCKET
    }
    
    public enum StreamQuality {
        LOW, MEDIUM, HIGH, ULTRA, AUTO
    }
    
    // 便捷方法
    @Transient
    public boolean isActive() {
        return status == StreamStatus.ACTIVE;
    }

    @Transient
    public boolean isInactive() {
        return status == StreamStatus.INACTIVE;
    }

    @Transient
    public boolean hasError() {
        return status == StreamStatus.ERROR;
    }
    
    public void updateLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }
    
    public void incrementViewerCount() {
        this.viewerCount = (this.viewerCount == null ? 0 : this.viewerCount) + 1;
    }
    
    public void decrementViewerCount() {
        this.viewerCount = Math.max(0, (this.viewerCount == null ? 0 : this.viewerCount) - 1);
    }
    
    public void recordError(String error) {
        this.lastError = error;
        this.lastErrorTime = LocalDateTime.now();
        this.errorCount = (this.errorCount == null ? 0 : this.errorCount) + 1;
        this.status = StreamStatus.ERROR;
    }
    
    public void clearError() {
        this.lastError = null;
        this.lastErrorTime = null;
        this.errorCount = 0;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStreamId() {
        return streamId;
    }
    
    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public StreamType getType() {
        return type;
    }
    
    public void setType(StreamType type) {
        this.type = type;
    }
    
    public StreamStatus getStatus() {
        return status;
    }
    
    public void setStatus(StreamStatus status) {
        this.status = status;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public String getOutputUrl() {
        return outputUrl;
    }
    
    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }
    
    public StreamProtocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(StreamProtocol protocol) {
        this.protocol = protocol;
    }
    
    public StreamQuality getQuality() {
        return quality;
    }
    
    public void setQuality(StreamQuality quality) {
        this.quality = quality;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Integer getFrameRate() {
        return frameRate;
    }
    
    public void setFrameRate(Integer frameRate) {
        this.frameRate = frameRate;
    }
    
    public Integer getBitrate() {
        return bitrate;
    }
    
    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
    
    public Boolean getRecordingEnabled() {
        return recordingEnabled;
    }
    
    public void setRecordingEnabled(Boolean recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }
    
    public String getRecordingPath() {
        return recordingPath;
    }
    
    public void setRecordingPath(String recordingPath) {
        this.recordingPath = recordingPath;
    }
    
    public Integer getRecordingDuration() {
        return recordingDuration;
    }
    
    public void setRecordingDuration(Integer recordingDuration) {
        this.recordingDuration = recordingDuration;
    }
    
    public Boolean getTranscodeEnabled() {
        return transcodeEnabled;
    }
    
    public void setTranscodeEnabled(Boolean transcodeEnabled) {
        this.transcodeEnabled = transcodeEnabled;
    }
    
    public String getTranscodeFormat() {
        return transcodeFormat;
    }
    
    public void setTranscodeFormat(String transcodeFormat) {
        this.transcodeFormat = transcodeFormat;
    }
    
    public String getTranscodeQuality() {
        return transcodeQuality;
    }
    
    public void setTranscodeQuality(String transcodeQuality) {
        this.transcodeQuality = transcodeQuality;
    }
    
    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }
    
    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
    
    public Long getViewerCount() {
        return viewerCount;
    }
    
    public void setViewerCount(Long viewerCount) {
        this.viewerCount = viewerCount;
    }
    
    public Double getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public Double getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public Double getNetworkBandwidth() {
        return networkBandwidth;
    }
    
    public void setNetworkBandwidth(Double networkBandwidth) {
        this.networkBandwidth = networkBandwidth;
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
    
    public LocalDateTime getLastErrorTime() {
        return lastErrorTime;
    }
    
    public void setLastErrorTime(LocalDateTime lastErrorTime) {
        this.lastErrorTime = lastErrorTime;
    }
    
    public Integer getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}