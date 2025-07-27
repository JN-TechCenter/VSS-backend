package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    // 创建设备
    public Device createDevice(Device device) {
        // 检查设备ID是否已存在
        if (deviceRepository.existsByDeviceId(device.getDeviceId())) {
            throw new RuntimeException("设备ID已存在: " + device.getDeviceId());
        }
        
        // 检查IP地址是否已存在
        if (device.getIpAddress() != null && deviceRepository.existsByIpAddress(device.getIpAddress())) {
            throw new RuntimeException("IP地址已存在: " + device.getIpAddress());
        }
        
        // 检查MAC地址是否已存在
        if (device.getMacAddress() != null && deviceRepository.existsByMacAddress(device.getMacAddress())) {
            throw new RuntimeException("MAC地址已存在: " + device.getMacAddress());
        }
        
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }
    
    // 更新设备
    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + id));
        
        // 检查设备ID是否与其他设备冲突
        if (!device.getDeviceId().equals(deviceDetails.getDeviceId()) && 
            deviceRepository.existsByDeviceId(deviceDetails.getDeviceId())) {
            throw new RuntimeException("设备ID已存在: " + deviceDetails.getDeviceId());
        }
        
        // 检查IP地址是否与其他设备冲突
        if (deviceDetails.getIpAddress() != null && 
            !deviceDetails.getIpAddress().equals(device.getIpAddress()) &&
            deviceRepository.existsByIpAddress(deviceDetails.getIpAddress())) {
            throw new RuntimeException("IP地址已存在: " + deviceDetails.getIpAddress());
        }
        
        // 检查MAC地址是否与其他设备冲突
        if (deviceDetails.getMacAddress() != null && 
            !deviceDetails.getMacAddress().equals(device.getMacAddress()) &&
            deviceRepository.existsByMacAddress(deviceDetails.getMacAddress())) {
            throw new RuntimeException("MAC地址已存在: " + deviceDetails.getMacAddress());
        }
        
        // 更新设备信息
        device.setDeviceId(deviceDetails.getDeviceId());
        device.setName(deviceDetails.getName());
        device.setType(deviceDetails.getType());
        device.setStatus(deviceDetails.getStatus());
        device.setLocation(deviceDetails.getLocation());
        device.setIpAddress(deviceDetails.getIpAddress());
        device.setPort(deviceDetails.getPort());
        device.setMacAddress(deviceDetails.getMacAddress());
        device.setManufacturer(deviceDetails.getManufacturer());
        device.setModel(deviceDetails.getModel());
        device.setFirmwareVersion(deviceDetails.getFirmwareVersion());
        device.setDescription(deviceDetails.getDescription());
        device.setConfiguration(deviceDetails.getConfiguration());
        device.setUpdatedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }
    
    // 删除设备
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + id));
        deviceRepository.delete(device);
    }
    
    // 根据ID获取设备
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }
    
    // 根据设备ID获取设备
    public Optional<Device> getDeviceByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId);
    }
    
    // 获取所有设备
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    
    // 分页获取设备
    public Page<Device> getDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }
    
    // 根据类型获取设备
    public List<Device> getDevicesByType(Device.DeviceType type) {
        return deviceRepository.findByType(type);
    }
    
    // 根据状态获取设备
    public List<Device> getDevicesByStatus(Device.DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }
    
    // 搜索设备
    public List<Device> searchDevices(String keyword) {
        return deviceRepository.searchDevices(keyword);
    }
    
    // 分页搜索设备
    public Page<Device> searchDevices(String keyword, Pageable pageable) {
        return deviceRepository.searchDevices(keyword, pageable);
    }
    
    // 获取在线设备
    public List<Device> getOnlineDevices() {
        return deviceRepository.findOnlineDevices();
    }
    
    // 获取离线设备
    public List<Device> getOfflineDevices() {
        return deviceRepository.findOfflineDevices();
    }
    
    // 更新设备状态
    public Device updateDeviceStatus(Long id, Device.DeviceStatus status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + id));
        
        device.setStatus(status);
        device.setUpdatedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }
    
    // 更新设备心跳
    public Device updateDeviceHeartbeat(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，设备ID: " + deviceId));
        
        device.setLastHeartbeat(LocalDateTime.now());
        device.setStatus(Device.DeviceStatus.ONLINE);
        device.setUpdatedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }
    
    // 获取需要维护的设备
    public List<Device> getDevicesNeedingMaintenance() {
        return deviceRepository.findDevicesNeedingMaintenance(LocalDateTime.now());
    }
    
    // 获取长时间未心跳的设备
    public List<Device> getDevicesWithOldHeartbeat(int minutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutes);
        return deviceRepository.findDevicesWithOldHeartbeat(threshold);
    }
    
    // 获取设备统计信息
    public Map<String, Object> getDeviceStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总设备数
        long totalDevices = deviceRepository.count();
        statistics.put("totalDevices", totalDevices);
        
        // 按状态统计
        Map<String, Long> statusCounts = new HashMap<>();
        for (Device.DeviceStatus status : Device.DeviceStatus.values()) {
            statusCounts.put(status.name(), deviceRepository.countByStatus(status));
        }
        statistics.put("statusCounts", statusCounts);
        
        // 按类型统计
        Map<String, Long> typeCounts = new HashMap<>();
        for (Device.DeviceType type : Device.DeviceType.values()) {
            typeCounts.put(type.name(), deviceRepository.countByType(type));
        }
        statistics.put("typeCounts", typeCounts);
        
        // 在线设备数
        long onlineDevices = deviceRepository.countByStatus(Device.DeviceStatus.ONLINE);
        statistics.put("onlineDevices", onlineDevices);
        
        // 离线设备数
        long offlineDevices = deviceRepository.countByStatus(Device.DeviceStatus.OFFLINE);
        statistics.put("offlineDevices", offlineDevices);
        
        // 需要维护的设备数
        long maintenanceDevices = getDevicesNeedingMaintenance().size();
        statistics.put("maintenanceDevices", maintenanceDevices);
        
        return statistics;
    }
    
    // 批量更新设备状态
    public void batchUpdateDeviceStatus(List<Long> deviceIds, Device.DeviceStatus status) {
        for (Long deviceId : deviceIds) {
            updateDeviceStatus(deviceId, status);
        }
    }
    
    // 检查设备连接状态
    public boolean checkDeviceConnection(String deviceId) {
        Optional<Device> deviceOpt = deviceRepository.findByDeviceId(deviceId);
        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            // 这里可以实现实际的设备连接检查逻辑
            // 例如ping设备IP地址或发送心跳请求
            return device.getStatus() == Device.DeviceStatus.ONLINE;
        }
        return false;
    }
    
    // 重启设备
    public Device restartDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + id));
        
        // 这里可以实现实际的设备重启逻辑
        device.setStatus(Device.DeviceStatus.RESTARTING);
        device.setUpdatedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }
}