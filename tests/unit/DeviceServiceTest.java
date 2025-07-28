package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setDeviceId("DEV001");
        testDevice.setName("测试设备");
        testDevice.setType(Device.DeviceType.CAMERA);
        testDevice.setStatus(Device.DeviceStatus.ONLINE);
        testDevice.setLocation("测试位置");
        testDevice.setIpAddress("192.168.1.100");
        testDevice.setPort(8080);
        testDevice.setMacAddress("00:11:22:33:44:55");
        testDevice.setManufacturer("测试厂商");
        testDevice.setModel("测试型号");
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setDescription("测试描述");
        testDevice.setConfiguration("{}");
        testDevice.setLastHeartbeat(LocalDateTime.now());
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateDevice_Success() {
        // Given
        when(deviceRepository.existsByDeviceId(testDevice.getDeviceId())).thenReturn(false);
        when(deviceRepository.existsByIpAddress(testDevice.getIpAddress())).thenReturn(false);
        when(deviceRepository.existsByMacAddress(testDevice.getMacAddress())).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceService.createDevice(testDevice);

        // Then
        assertNotNull(result);
        assertEquals(testDevice.getDeviceId(), result.getDeviceId());
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void testCreateDevice_DeviceIdExists() {
        // Given
        when(deviceRepository.existsByDeviceId(testDevice.getDeviceId())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.createDevice(testDevice));
        assertEquals("设备ID已存在: " + testDevice.getDeviceId(), exception.getMessage());
    }

    @Test
    void testCreateDevice_IpAddressExists() {
        // Given
        when(deviceRepository.existsByDeviceId(testDevice.getDeviceId())).thenReturn(false);
        when(deviceRepository.existsByIpAddress(testDevice.getIpAddress())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.createDevice(testDevice));
        assertEquals("IP地址已存在: " + testDevice.getIpAddress(), exception.getMessage());
    }

    @Test
    void testCreateDevice_MacAddressExists() {
        // Given
        when(deviceRepository.existsByDeviceId(testDevice.getDeviceId())).thenReturn(false);
        when(deviceRepository.existsByIpAddress(testDevice.getIpAddress())).thenReturn(false);
        when(deviceRepository.existsByMacAddress(testDevice.getMacAddress())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.createDevice(testDevice));
        assertEquals("MAC地址已存在: " + testDevice.getMacAddress(), exception.getMessage());
    }

    @Test
    void testUpdateDevice_Success() {
        // Given
        Device updatedDevice = new Device();
        updatedDevice.setDeviceId("DEV001");
        updatedDevice.setName("更新后的设备");
        updatedDevice.setIpAddress("192.168.1.101");
        updatedDevice.setMacAddress("00:11:22:33:44:56");

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.existsByDeviceId(updatedDevice.getDeviceId())).thenReturn(false);
        when(deviceRepository.existsByIpAddress(updatedDevice.getIpAddress())).thenReturn(false);
        when(deviceRepository.existsByMacAddress(updatedDevice.getMacAddress())).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceService.updateDevice(1L, updatedDevice);

        // Then
        assertNotNull(result);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void testUpdateDevice_DeviceNotFound() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.updateDevice(1L, testDevice));
        assertEquals("设备不存在，ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteDevice_Success() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // When
        deviceService.deleteDevice(1L);

        // Then
        verify(deviceRepository).delete(testDevice);
    }

    @Test
    void testDeleteDevice_DeviceNotFound() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.deleteDevice(1L));
        assertEquals("设备不存在，ID: 1", exception.getMessage());
    }

    @Test
    void testGetDeviceById() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // When
        Optional<Device> result = deviceService.getDeviceById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDevice.getId(), result.get().getId());
    }

    @Test
    void testGetDeviceByDeviceId() {
        // Given
        when(deviceRepository.findByDeviceId("DEV001")).thenReturn(Optional.of(testDevice));

        // When
        Optional<Device> result = deviceService.getDeviceByDeviceId("DEV001");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDevice.getDeviceId(), result.get().getDeviceId());
    }

    @Test
    void testGetAllDevices() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceService.getAllDevices();

        // Then
        assertEquals(1, result.size());
        assertEquals(testDevice.getId(), result.get(0).getId());
    }

    @Test
    void testGetDevices_Pageable() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> devicePage = new PageImpl<>(Arrays.asList(testDevice));
        when(deviceRepository.findAll(pageable)).thenReturn(devicePage);

        // When
        Page<Device> result = deviceService.getDevices(pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(testDevice.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testGetDevicesByType() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findByType(Device.DeviceType.CAMERA)).thenReturn(devices);

        // When
        List<Device> result = deviceService.getDevicesByType(Device.DeviceType.CAMERA);

        // Then
        assertEquals(1, result.size());
        assertEquals(testDevice.getType(), result.get(0).getType());
    }

    @Test
    void testGetDevicesByStatus() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findByStatus(Device.DeviceStatus.ONLINE)).thenReturn(devices);

        // When
        List<Device> result = deviceService.getDevicesByStatus(Device.DeviceStatus.ONLINE);

        // Then
        assertEquals(1, result.size());
        assertEquals(testDevice.getStatus(), result.get(0).getStatus());
    }

    @Test
    void testSearchDevices() {
        // Given
        String keyword = "测试";
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.searchDevices(keyword)).thenReturn(devices);

        // When
        List<Device> result = deviceService.searchDevices(keyword);

        // Then
        assertEquals(1, result.size());
        assertEquals(testDevice.getName(), result.get(0).getName());
    }

    @Test
    void testSearchDevices_Pageable() {
        // Given
        String keyword = "测试";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> devicePage = new PageImpl<>(Arrays.asList(testDevice));
        when(deviceRepository.searchDevices(keyword, pageable)).thenReturn(devicePage);

        // When
        Page<Device> result = deviceService.searchDevices(keyword, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(testDevice.getName(), result.getContent().get(0).getName());
    }

    @Test
    void testGetOnlineDevices() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findOnlineDevices()).thenReturn(devices);

        // When
        List<Device> result = deviceService.getOnlineDevices();

        // Then
        assertEquals(1, result.size());
        assertEquals(Device.DeviceStatus.ONLINE, result.get(0).getStatus());
    }

    @Test
    void testGetOfflineDevices() {
        // Given
        Device offlineDevice = new Device();
        offlineDevice.setStatus(Device.DeviceStatus.OFFLINE);
        List<Device> devices = Arrays.asList(offlineDevice);
        when(deviceRepository.findOfflineDevices()).thenReturn(devices);

        // When
        List<Device> result = deviceService.getOfflineDevices();

        // Then
        assertEquals(1, result.size());
        assertEquals(Device.DeviceStatus.OFFLINE, result.get(0).getStatus());
    }

    @Test
    void testUpdateDeviceStatus_Success() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceService.updateDeviceStatus(1L, Device.DeviceStatus.MAINTENANCE);

        // Then
        assertNotNull(result);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void testUpdateDeviceStatus_DeviceNotFound() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.updateDeviceStatus(1L, Device.DeviceStatus.MAINTENANCE));
        assertEquals("设备不存在，ID: 1", exception.getMessage());
    }

    @Test
    void testUpdateDeviceHeartbeat_Success() {
        // Given
        when(deviceRepository.findByDeviceId("DEV001")).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceService.updateDeviceHeartbeat("DEV001");

        // Then
        assertNotNull(result);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void testUpdateDeviceHeartbeat_DeviceNotFound() {
        // Given
        when(deviceRepository.findByDeviceId("DEV001")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.updateDeviceHeartbeat("DEV001"));
        assertEquals("设备不存在，设备ID: DEV001", exception.getMessage());
    }

    @Test
    void testGetDevicesNeedingMaintenance() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findDevicesNeedingMaintenance(any(LocalDateTime.class))).thenReturn(devices);

        // When
        List<Device> result = deviceService.getDevicesNeedingMaintenance();

        // Then
        assertEquals(1, result.size());
        verify(deviceRepository).findDevicesNeedingMaintenance(any(LocalDateTime.class));
    }

    @Test
    void testGetDevicesWithOldHeartbeat() {
        // Given
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceRepository.findDevicesWithOldHeartbeat(any(LocalDateTime.class))).thenReturn(devices);

        // When
        List<Device> result = deviceService.getDevicesWithOldHeartbeat(30);

        // Then
        assertEquals(1, result.size());
        verify(deviceRepository).findDevicesWithOldHeartbeat(any(LocalDateTime.class));
    }

    @Test
    void testGetDeviceStatistics() {
        // Given
        when(deviceRepository.count()).thenReturn(10L);
        when(deviceRepository.countByStatus(Device.DeviceStatus.ONLINE)).thenReturn(7L);
        when(deviceRepository.countByStatus(Device.DeviceStatus.OFFLINE)).thenReturn(2L);
        when(deviceRepository.countByStatus(Device.DeviceStatus.MAINTENANCE)).thenReturn(1L);
        when(deviceRepository.countByType(Device.DeviceType.CAMERA)).thenReturn(5L);
        when(deviceRepository.countByType(Device.DeviceType.SENSOR)).thenReturn(3L);
        when(deviceRepository.countByType(Device.DeviceType.CONTROLLER)).thenReturn(2L);
        when(deviceRepository.findDevicesNeedingMaintenance(any(LocalDateTime.class))).thenReturn(Arrays.asList(testDevice));

        // When
        Map<String, Object> result = deviceService.getDeviceStatistics();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.get("totalDevices"));
        assertEquals(7L, result.get("onlineDevices"));
        assertEquals(2L, result.get("offlineDevices"));
        assertEquals(1L, result.get("maintenanceDevices"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> statusCounts = (Map<String, Long>) result.get("statusCounts");
        assertNotNull(statusCounts);
        assertEquals(7L, statusCounts.get("ONLINE"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> typeCounts = (Map<String, Long>) result.get("typeCounts");
        assertNotNull(typeCounts);
        assertEquals(5L, typeCounts.get("CAMERA"));
    }

    @Test
    void testBatchUpdateDeviceStatus() {
        // Given
        List<Long> deviceIds = Arrays.asList(1L, 2L, 3L);
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        deviceService.batchUpdateDeviceStatus(deviceIds, Device.DeviceStatus.MAINTENANCE);

        // Then
        verify(deviceRepository, times(3)).findById(anyLong());
        verify(deviceRepository, times(3)).save(any(Device.class));
    }

    @Test
    void testCheckDeviceConnection_DeviceExists() {
        // Given
        when(deviceRepository.findByDeviceId("DEV001")).thenReturn(Optional.of(testDevice));

        // When
        boolean result = deviceService.checkDeviceConnection("DEV001");

        // Then
        assertTrue(result);
    }

    @Test
    void testCheckDeviceConnection_DeviceNotExists() {
        // Given
        when(deviceRepository.findByDeviceId("DEV001")).thenReturn(Optional.empty());

        // When
        boolean result = deviceService.checkDeviceConnection("DEV001");

        // Then
        assertFalse(result);
    }

    @Test
    void testCheckDeviceConnection_DeviceOffline() {
        // Given
        testDevice.setStatus(Device.DeviceStatus.OFFLINE);
        when(deviceRepository.findByDeviceId("DEV001")).thenReturn(Optional.of(testDevice));

        // When
        boolean result = deviceService.checkDeviceConnection("DEV001");

        // Then
        assertFalse(result);
    }

    @Test
    void testRestartDevice_Success() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // When
        Device result = deviceService.restartDevice(1L);

        // Then
        assertNotNull(result);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void testRestartDevice_DeviceNotFound() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deviceService.restartDevice(1L));
        assertEquals("设备不存在，ID: 1", exception.getMessage());
    }
}