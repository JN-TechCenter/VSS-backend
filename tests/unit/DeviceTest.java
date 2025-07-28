package com.vision.vision_platform_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Device模型类单元测试
 */
@ExtendWith(MockitoExtension.class)
class DeviceTest {

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device();
    }

    @Test
    void testDefaultConstructor() {
        // When
        Device newDevice = new Device();

        // Then
        assertNotNull(newDevice);
        assertNull(newDevice.getId());
        assertNull(newDevice.getDeviceId());
        assertNull(newDevice.getName());
        assertNull(newDevice.getType());
        assertEquals(Device.DeviceStatus.OFFLINE, newDevice.getStatus()); // 默认状态为OFFLINE
        assertNotNull(newDevice.getCreatedAt());
        assertNotNull(newDevice.getUpdatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String deviceId = "DEV-001";
        String name = "Test Camera";
        Device.DeviceType type = Device.DeviceType.CAMERA;
        String location = "Building A - Floor 1";

        // When
        Device newDevice = new Device(deviceId, name, type, location);

        // Then
        assertEquals(deviceId, newDevice.getDeviceId());
        assertEquals(name, newDevice.getName());
        assertEquals(type, newDevice.getType());
        assertEquals(location, newDevice.getLocation());
        assertEquals(Device.DeviceStatus.OFFLINE, newDevice.getStatus());
        assertNotNull(newDevice.getCreatedAt());
        assertNotNull(newDevice.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Long id = 1L;
        String deviceId = "DEV-002";
        String name = "Security Camera";
        Device.DeviceType type = Device.DeviceType.CAMERA;
        Device.DeviceStatus status = Device.DeviceStatus.ONLINE;
        String location = "Entrance";
        String ipAddress = "192.168.1.100";
        Integer port = 8080;
        String macAddress = "00:11:22:33:44:55";
        String manufacturer = "Hikvision";
        String model = "DS-2CD2T47G1-L";
        String serialNumber = "SN123456789";
        String firmwareVersion = "V5.6.3";
        String description = "Main entrance security camera";
        String configuration = "{\"resolution\": \"1080p\", \"fps\": 30}";
        LocalDateTime lastHeartbeat = LocalDateTime.now();
        LocalDateTime installedAt = LocalDateTime.now().minusDays(30);
        LocalDateTime lastMaintenanceAt = LocalDateTime.now().minusDays(7);
        LocalDateTime nextMaintenanceAt = LocalDateTime.now().plusDays(30);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(30);
        LocalDateTime updatedAt = LocalDateTime.now();
        String createdBy = "admin";
        String updatedBy = "technician";

        // When
        device.setId(id);
        device.setDeviceId(deviceId);
        device.setName(name);
        device.setType(type);
        device.setStatus(status);
        device.setLocation(location);
        device.setIpAddress(ipAddress);
        device.setPort(port);
        device.setMacAddress(macAddress);
        device.setManufacturer(manufacturer);
        device.setModel(model);
        device.setSerialNumber(serialNumber);
        device.setFirmwareVersion(firmwareVersion);
        device.setDescription(description);
        device.setConfiguration(configuration);
        device.setLastHeartbeat(lastHeartbeat);
        device.setInstalledAt(installedAt);
        device.setLastMaintenanceAt(lastMaintenanceAt);
        device.setNextMaintenanceAt(nextMaintenanceAt);
        device.setCreatedAt(createdAt);
        device.setUpdatedAt(updatedAt);
        device.setCreatedBy(createdBy);
        device.setUpdatedBy(updatedBy);

        // Then
        assertEquals(id, device.getId());
        assertEquals(deviceId, device.getDeviceId());
        assertEquals(name, device.getName());
        assertEquals(type, device.getType());
        assertEquals(status, device.getStatus());
        assertEquals(location, device.getLocation());
        assertEquals(ipAddress, device.getIpAddress());
        assertEquals(port, device.getPort());
        assertEquals(macAddress, device.getMacAddress());
        assertEquals(manufacturer, device.getManufacturer());
        assertEquals(model, device.getModel());
        assertEquals(serialNumber, device.getSerialNumber());
        assertEquals(firmwareVersion, device.getFirmwareVersion());
        assertEquals(description, device.getDescription());
        assertEquals(configuration, device.getConfiguration());
        assertEquals(lastHeartbeat, device.getLastHeartbeat());
        assertEquals(installedAt, device.getInstalledAt());
        assertEquals(lastMaintenanceAt, device.getLastMaintenanceAt());
        assertEquals(nextMaintenanceAt, device.getNextMaintenanceAt());
        assertEquals(createdAt, device.getCreatedAt());
        assertEquals(updatedAt, device.getUpdatedAt());
        assertEquals(createdBy, device.getCreatedBy());
        assertEquals(updatedBy, device.getUpdatedBy());
    }

    @Test
    void testUpdateHeartbeat() {
        // Given
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);
        device.setStatus(Device.DeviceStatus.OFFLINE);

        // When
        device.updateHeartbeat();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertEquals(Device.DeviceStatus.ONLINE, device.getStatus());
        assertNotNull(device.getLastHeartbeat());
        assertTrue(device.getLastHeartbeat().isAfter(beforeUpdate));
        assertTrue(device.getLastHeartbeat().isBefore(afterUpdate));
        assertNotNull(device.getUpdatedAt());
        assertTrue(device.getUpdatedAt().isAfter(beforeUpdate));
        assertTrue(device.getUpdatedAt().isBefore(afterUpdate));
    }

    @Test
    void testSetOffline() {
        // Given
        device.setStatus(Device.DeviceStatus.ONLINE);
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        device.setOffline();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertEquals(Device.DeviceStatus.OFFLINE, device.getStatus());
        assertNotNull(device.getUpdatedAt());
        assertTrue(device.getUpdatedAt().isAfter(beforeUpdate));
        assertTrue(device.getUpdatedAt().isBefore(afterUpdate));
    }

    @Test
    void testSetMaintenance() {
        // Given
        device.setStatus(Device.DeviceStatus.ONLINE);
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        device.setMaintenance();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertEquals(Device.DeviceStatus.MAINTENANCE, device.getStatus());
        assertNotNull(device.getUpdatedAt());
        assertTrue(device.getUpdatedAt().isAfter(beforeUpdate));
        assertTrue(device.getUpdatedAt().isBefore(afterUpdate));
    }

    @Test
    void testSetError() {
        // Given
        device.setStatus(Device.DeviceStatus.ONLINE);
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        device.setError();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertEquals(Device.DeviceStatus.ERROR, device.getStatus());
        assertNotNull(device.getUpdatedAt());
        assertTrue(device.getUpdatedAt().isAfter(beforeUpdate));
        assertTrue(device.getUpdatedAt().isBefore(afterUpdate));
    }

    @Test
    void testIsOnline() {
        // Test when device is online
        device.setStatus(Device.DeviceStatus.ONLINE);
        assertTrue(device.isOnline());

        // Test when device is offline
        device.setStatus(Device.DeviceStatus.OFFLINE);
        assertFalse(device.isOnline());

        // Test when device is in maintenance
        device.setStatus(Device.DeviceStatus.MAINTENANCE);
        assertFalse(device.isOnline());
    }

    @Test
    void testIsOffline() {
        // Test when device is offline
        device.setStatus(Device.DeviceStatus.OFFLINE);
        assertTrue(device.isOffline());

        // Test when device is online
        device.setStatus(Device.DeviceStatus.ONLINE);
        assertFalse(device.isOffline());

        // Test when device is in error state
        device.setStatus(Device.DeviceStatus.ERROR);
        assertFalse(device.isOffline());
    }

    @Test
    void testNeedsMaintenance_True() {
        // Given - next maintenance is in the past
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        device.setNextMaintenanceAt(pastDate);

        // When & Then
        assertTrue(device.needsMaintenance());
    }

    @Test
    void testNeedsMaintenance_False() {
        // Given - next maintenance is in the future
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        device.setNextMaintenanceAt(futureDate);

        // When & Then
        assertFalse(device.needsMaintenance());
    }

    @Test
    void testNeedsMaintenance_Null() {
        // Given - no maintenance scheduled
        device.setNextMaintenanceAt(null);

        // When & Then
        assertFalse(device.needsMaintenance());
    }

    @Test
    void testPreUpdate() {
        // Given
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusHours(1);
        device.setUpdatedAt(originalUpdatedAt);
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        device.preUpdate();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertNotNull(device.getUpdatedAt());
        assertTrue(device.getUpdatedAt().isAfter(beforeUpdate));
        assertTrue(device.getUpdatedAt().isBefore(afterUpdate));
        assertNotEquals(originalUpdatedAt, device.getUpdatedAt());
    }

    @Test
    void testDeviceTypeEnum() {
        // Test all device types
        assertEquals("摄像头", Device.DeviceType.CAMERA.getDisplayName());
        assertEquals("传感器", Device.DeviceType.SENSOR.getDisplayName());
        assertEquals("控制器", Device.DeviceType.CONTROLLER.getDisplayName());
        assertEquals("网关", Device.DeviceType.GATEWAY.getDisplayName());
        assertEquals("显示设备", Device.DeviceType.DISPLAY.getDisplayName());
        assertEquals("存储设备", Device.DeviceType.STORAGE.getDisplayName());
        assertEquals("网络设备", Device.DeviceType.NETWORK.getDisplayName());
        assertEquals("其他", Device.DeviceType.OTHER.getDisplayName());
    }

    @Test
    void testDeviceStatusEnum() {
        // Test all device statuses
        assertEquals("在线", Device.DeviceStatus.ONLINE.getDisplayName());
        assertEquals("离线", Device.DeviceStatus.OFFLINE.getDisplayName());
        assertEquals("维护中", Device.DeviceStatus.MAINTENANCE.getDisplayName());
        assertEquals("故障", Device.DeviceStatus.ERROR.getDisplayName());
        assertEquals("已禁用", Device.DeviceStatus.DISABLED.getDisplayName());
        assertEquals("重启中", Device.DeviceStatus.RESTARTING.getDisplayName());
        assertEquals("未知", Device.DeviceStatus.UNKNOWN.getDisplayName());
    }

    @Test
    void testEnumValues() {
        // Test that all enum values are accessible
        Device.DeviceType[] types = Device.DeviceType.values();
        assertEquals(8, types.length);
        
        Device.DeviceStatus[] statuses = Device.DeviceStatus.values();
        assertEquals(7, statuses.length);
    }

    @Test
    void testCompleteDeviceLifecycle() {
        // Given - Create a new device
        Device newDevice = new Device("CAM-001", "Main Camera", Device.DeviceType.CAMERA, "Lobby");
        
        // Initially offline
        assertTrue(newDevice.isOffline());
        assertFalse(newDevice.isOnline());
        
        // Device comes online
        newDevice.updateHeartbeat();
        assertTrue(newDevice.isOnline());
        assertFalse(newDevice.isOffline());
        assertNotNull(newDevice.getLastHeartbeat());
        
        // Device needs maintenance
        newDevice.setNextMaintenanceAt(LocalDateTime.now().minusDays(1));
        assertTrue(newDevice.needsMaintenance());
        
        // Put device in maintenance
        newDevice.setMaintenance();
        assertEquals(Device.DeviceStatus.MAINTENANCE, newDevice.getStatus());
        assertFalse(newDevice.isOnline());
        
        // Device encounters error
        newDevice.setError();
        assertEquals(Device.DeviceStatus.ERROR, newDevice.getStatus());
        
        // Device goes offline
        newDevice.setOffline();
        assertTrue(newDevice.isOffline());
        assertEquals(Device.DeviceStatus.OFFLINE, newDevice.getStatus());
    }
}