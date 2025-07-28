package com.vision.vision_platform_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String deviceId;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;
    
    private String location;
    private String ipAddress;
    private Integer port;
    private String macAddress;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String firmwareVersion;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String configuration; // JSON格式的配置信息
    
    private LocalDateTime lastHeartbeat;
    private LocalDateTime installedAt;
    private LocalDateTime lastMaintenanceAt;
    private LocalDateTime nextMaintenanceAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String updatedBy;
    
    // 设备类型枚举
    public enum DeviceType {
        CAMERA("摄像头"),
        SENSOR("传感器"),
        CONTROLLER("控制器"),
        GATEWAY("网关"),
        DISPLAY("显示设备"),
        STORAGE("存储设备"),
        NETWORK("网络设备"),
        OTHER("其他");
        
        private final String displayName;
        
        DeviceType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 设备状态枚举
    public enum DeviceStatus {
        ONLINE("在线"),
        OFFLINE("离线"),
        MAINTENANCE("维护中"),
        ERROR("故障"),
        DISABLED("已禁用"),
        RESTARTING("重启中"),
        UNKNOWN("未知");
        
        private final String displayName;
        
        DeviceStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 构造函数
    public Device() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = DeviceStatus.OFFLINE;
    }
    
    public Device(String deviceId, String name, DeviceType type, String location) {
        this();
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.location = location;
    }
    
    // 业务方法
    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
        this.status = DeviceStatus.ONLINE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setOffline() {
        this.status = DeviceStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setMaintenance() {
        this.status = DeviceStatus.MAINTENANCE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setError() {
        this.status = DeviceStatus.ERROR;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Transient
    public boolean isOnline() {
        return this.status == DeviceStatus.ONLINE;
    }
    
    @Transient
    public boolean isOffline() {
        return this.status == DeviceStatus.OFFLINE;
    }
    
    public boolean needsMaintenance() {
        return this.nextMaintenanceAt != null && 
               this.nextMaintenanceAt.isBefore(LocalDateTime.now());
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public DeviceType getType() {
        return type;
    }
    
    public void setType(DeviceType type) {
        this.type = type;
    }
    
    public DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
    
    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    public LocalDateTime getInstalledAt() {
        return installedAt;
    }
    
    public void setInstalledAt(LocalDateTime installedAt) {
        this.installedAt = installedAt;
    }
    
    public LocalDateTime getLastMaintenanceAt() {
        return lastMaintenanceAt;
    }
    
    public void setLastMaintenanceAt(LocalDateTime lastMaintenanceAt) {
        this.lastMaintenanceAt = lastMaintenanceAt;
    }
    
    public LocalDateTime getNextMaintenanceAt() {
        return nextMaintenanceAt;
    }
    
    public void setNextMaintenanceAt(LocalDateTime nextMaintenanceAt) {
        this.nextMaintenanceAt = nextMaintenanceAt;
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