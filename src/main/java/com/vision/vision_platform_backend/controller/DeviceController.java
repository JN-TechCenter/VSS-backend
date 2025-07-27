package com.vision.vision_platform_backend.controller;

import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.service.DeviceService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    @Autowired
    private DeviceService deviceService;
    
    // 创建设备 - 需要管理员权限
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDevice(@Valid @RequestBody Device device) {
        try {
            Device createdDevice = deviceService.createDevice(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 获取所有设备 - 需要用户权限
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }
    
    // 分页获取设备 - 需要用户权限
    @GetMapping("/page")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Device>> getDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Device> devices = deviceService.getDevices(pageable);
        return ResponseEntity.ok(devices);
    }
    
    // 根据ID获取设备 - 需要用户权限
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDeviceById(@PathVariable Long id) {
        Optional<Device> device = deviceService.getDeviceById(id);
        if (device.isPresent()) {
            return ResponseEntity.ok(device.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 根据设备ID获取设备 - 需要用户权限
    @GetMapping("/device-id/{deviceId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getDeviceByDeviceId(@PathVariable String deviceId) {
        Optional<Device> device = deviceService.getDeviceByDeviceId(deviceId);
        if (device.isPresent()) {
            return ResponseEntity.ok(device.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 更新设备 - 需要管理员权限
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @Valid @RequestBody Device deviceDetails) {
        try {
            Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 删除设备 - 需要管理员权限
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.ok(Map.of("message", "设备删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 根据类型获取设备 - 需要用户权限
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getDevicesByType(@PathVariable Device.DeviceType type) {
        List<Device> devices = deviceService.getDevicesByType(type);
        return ResponseEntity.ok(devices);
    }
    
    // 根据状态获取设备 - 需要用户权限
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getDevicesByStatus(@PathVariable Device.DeviceStatus status) {
        List<Device> devices = deviceService.getDevicesByStatus(status);
        return ResponseEntity.ok(devices);
    }
    
    // 搜索设备 - 需要用户权限
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Device>> searchDevices(@RequestParam String keyword) {
        List<Device> devices = deviceService.searchDevices(keyword);
        return ResponseEntity.ok(devices);
    }
    
    // 分页搜索设备 - 需要用户权限
    @GetMapping("/search/page")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Device>> searchDevicesPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Device> devices = deviceService.searchDevices(keyword, pageable);
        return ResponseEntity.ok(devices);
    }
    
    // 获取在线设备 - 需要用户权限
    @GetMapping("/online")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getOnlineDevices() {
        List<Device> devices = deviceService.getOnlineDevices();
        return ResponseEntity.ok(devices);
    }
    
    // 获取离线设备 - 需要用户权限
    @GetMapping("/offline")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getOfflineDevices() {
        List<Device> devices = deviceService.getOfflineDevices();
        return ResponseEntity.ok(devices);
    }
    
    // 更新设备状态 - 需要管理员权限
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDeviceStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            Device.DeviceStatus status = Device.DeviceStatus.valueOf(statusUpdate.get("status"));
            Device updatedDevice = deviceService.updateDeviceStatus(id, status);
            return ResponseEntity.ok(updatedDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "无效的设备状态"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 设备心跳 - 设备自身调用，无需权限验证
    @PostMapping("/{deviceId}/heartbeat")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> deviceHeartbeat(@PathVariable String deviceId) {
        try {
            Device updatedDevice = deviceService.updateDeviceHeartbeat(deviceId);
            return ResponseEntity.ok(Map.of("message", "心跳更新成功", "device", updatedDevice));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 获取需要维护的设备 - 需要管理员权限
    @GetMapping("/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getDevicesNeedingMaintenance() {
        List<Device> devices = deviceService.getDevicesNeedingMaintenance();
        return ResponseEntity.ok(devices);
    }
    
    // 获取设备统计信息 - 需要用户权限
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDeviceStatistics() {
        Map<String, Object> statistics = deviceService.getDeviceStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    // 检查设备连接状态 - 需要用户权限
    @GetMapping("/{deviceId}/connection")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkDeviceConnection(@PathVariable String deviceId) {
        boolean isConnected = deviceService.checkDeviceConnection(deviceId);
        return ResponseEntity.ok(Map.of("deviceId", deviceId, "connected", isConnected));
    }
    
    // 重启设备 - 需要管理员权限
    @PostMapping("/{id}/restart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> restartDevice(@PathVariable Long id) {
        try {
            Device device = deviceService.restartDevice(id);
            return ResponseEntity.ok(Map.of("message", "设备重启命令已发送", "device", device));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 批量更新设备状态 - 需要管理员权限
    @PatchMapping("/batch/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> batchUpdateDeviceStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> deviceIds = (List<Long>) request.get("deviceIds");
            String statusStr = (String) request.get("status");
            Device.DeviceStatus status = Device.DeviceStatus.valueOf(statusStr);
            
            deviceService.batchUpdateDeviceStatus(deviceIds, status);
            return ResponseEntity.ok(Map.of("message", "批量更新设备状态成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "无效的设备状态"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}