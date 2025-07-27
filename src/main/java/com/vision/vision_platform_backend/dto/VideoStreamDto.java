package com.vision.vision_platform_backend.dto;

import com.vision.vision_platform_backend.model.VideoStream;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class VideoStreamDto {
    
    private Long id;
    
    @NotBlank(message = "流ID不能为空")
    @Size(max = 50, message = "流ID长度不能超过50个字符")
    private String streamId;
    
    @NotBlank(message = "流名称不能为空")
    @Size(max = 100, message = "流名称长度不能超过100个字符")
    private String name;
    
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
    
    @NotNull(message = "流类型不能为空")
    private VideoStream.StreamType type;
    
    private VideoStream.StreamStatus status;
    
    @NotBlank(message = "源URL不能为空")
    @Size(max = 500, message = "源URL长度不能超过500个字符")
    private String sourceUrl;
    
    @Size(max = 500, message = "输出URL长度不能超过500个字符")
    private String outputUrl;
    
    private VideoStream.StreamProtocol protocol;
    private VideoStream.StreamQuality quality;
    
    @Min(value = 1, message = "宽度必须大于0")
    @Max(value = 7680, message = "宽度不能超过7680")
    private Integer width;
    
    @Min(value = 1, message = "高度必须大于0")
    @Max(value = 4320, message = "高度不能超过4320")
    private Integer height;
    
    @Min(value = 1, message = "帧率必须大于0")
    @Max(value = 120, message = "帧率不能超过120")
    private Integer frameRate;
    
    @Min(value = 1, message = "比特率必须大于0")
    private Integer bitrate;
    
    // 关联设备ID
    private Long deviceId;
    private String deviceName;
    
    // 录制配置
    private Boolean recordingEnabled;
    
    @Size(max = 500, message = "录制路径长度不能超过500个字符")
    private String recordingPath;
    
    @Min(value = 1, message = "录制时长必须大于0分钟")
    @Max(value = 1440, message = "录制时长不能超过1440分钟")
    private Integer recordingDuration;
    
    // 转码配置
    private Boolean transcodeEnabled;
    
    @Size(max = 50, message = "转码格式长度不能超过50个字符")
    private String transcodeFormat;
    
    @Size(max = 50, message = "转码质量长度不能超过50个字符")
    private String transcodeQuality;
    
    // 监控信息（只读）
    private LocalDateTime lastActiveTime;
    private Long viewerCount;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double networkBandwidth;
    
    // 错误信息（只读）
    private String lastError;
    private LocalDateTime lastErrorTime;
    private Integer errorCount;
    
    // 时间戳（只读）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // 构造函数
    public VideoStreamDto() {}
    
    public VideoStreamDto(VideoStream videoStream) {
        this.id = videoStream.getId();
        this.streamId = videoStream.getStreamId();
        this.name = videoStream.getName();
        this.description = videoStream.getDescription();
        this.type = videoStream.getType();
        this.status = videoStream.getStatus();
        this.sourceUrl = videoStream.getSourceUrl();
        this.outputUrl = videoStream.getOutputUrl();
        this.protocol = videoStream.getProtocol();
        this.quality = videoStream.getQuality();
        this.width = videoStream.getWidth();
        this.height = videoStream.getHeight();
        this.frameRate = videoStream.getFrameRate();
        this.bitrate = videoStream.getBitrate();
        
        if (videoStream.getDevice() != null) {
            this.deviceId = videoStream.getDevice().getId();
            this.deviceName = videoStream.getDevice().getName();
        }
        
        this.recordingEnabled = videoStream.getRecordingEnabled();
        this.recordingPath = videoStream.getRecordingPath();
        this.recordingDuration = videoStream.getRecordingDuration();
        this.transcodeEnabled = videoStream.getTranscodeEnabled();
        this.transcodeFormat = videoStream.getTranscodeFormat();
        this.transcodeQuality = videoStream.getTranscodeQuality();
        
        this.lastActiveTime = videoStream.getLastActiveTime();
        this.viewerCount = videoStream.getViewerCount();
        this.cpuUsage = videoStream.getCpuUsage();
        this.memoryUsage = videoStream.getMemoryUsage();
        this.networkBandwidth = videoStream.getNetworkBandwidth();
        
        this.lastError = videoStream.getLastError();
        this.lastErrorTime = videoStream.getLastErrorTime();
        this.errorCount = videoStream.getErrorCount();
        
        this.createdAt = videoStream.getCreatedAt();
        this.updatedAt = videoStream.getUpdatedAt();
        this.createdBy = videoStream.getCreatedBy();
        this.updatedBy = videoStream.getUpdatedBy();
    }
    
    // 转换为实体对象
    public VideoStream toEntity() {
        VideoStream videoStream = new VideoStream();
        videoStream.setId(this.id);
        videoStream.setStreamId(this.streamId);
        videoStream.setName(this.name);
        videoStream.setDescription(this.description);
        videoStream.setType(this.type);
        videoStream.setStatus(this.status != null ? this.status : VideoStream.StreamStatus.INACTIVE);
        videoStream.setSourceUrl(this.sourceUrl);
        videoStream.setOutputUrl(this.outputUrl);
        videoStream.setProtocol(this.protocol);
        videoStream.setQuality(this.quality);
        videoStream.setWidth(this.width);
        videoStream.setHeight(this.height);
        videoStream.setFrameRate(this.frameRate);
        videoStream.setBitrate(this.bitrate);
        
        videoStream.setRecordingEnabled(this.recordingEnabled != null ? this.recordingEnabled : false);
        videoStream.setRecordingPath(this.recordingPath);
        videoStream.setRecordingDuration(this.recordingDuration);
        videoStream.setTranscodeEnabled(this.transcodeEnabled != null ? this.transcodeEnabled : false);
        videoStream.setTranscodeFormat(this.transcodeFormat);
        videoStream.setTranscodeQuality(this.transcodeQuality);
        
        videoStream.setViewerCount(this.viewerCount != null ? this.viewerCount : 0L);
        videoStream.setErrorCount(this.errorCount != null ? this.errorCount : 0);
        
        return videoStream;
    }
    
    // 便捷方法
    public boolean isActive() {
        return status == VideoStream.StreamStatus.ACTIVE;
    }
    
    public boolean hasError() {
        return status == VideoStream.StreamStatus.ERROR;
    }
    
    public String getResolution() {
        if (width != null && height != null) {
            return width + "x" + height;
        }
        return null;
    }
    
    public String getStreamInfo() {
        StringBuilder info = new StringBuilder();
        info.append(name);
        if (getResolution() != null) {
            info.append(" (").append(getResolution()).append(")");
        }
        if (frameRate != null) {
            info.append(" @").append(frameRate).append("fps");
        }
        return info.toString();
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
    
    public VideoStream.StreamType getType() {
        return type;
    }
    
    public void setType(VideoStream.StreamType type) {
        this.type = type;
    }
    
    public VideoStream.StreamStatus getStatus() {
        return status;
    }
    
    public void setStatus(VideoStream.StreamStatus status) {
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
    
    public VideoStream.StreamProtocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(VideoStream.StreamProtocol protocol) {
        this.protocol = protocol;
    }
    
    public VideoStream.StreamQuality getQuality() {
        return quality;
    }
    
    public void setQuality(VideoStream.StreamQuality quality) {
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
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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