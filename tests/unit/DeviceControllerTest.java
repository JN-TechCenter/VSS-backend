package com.vision.vision_platform_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.service.DeviceService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Device testDevice;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
        objectMapper = new ObjectMapper();
        
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setDeviceId("DEVICE001");
        testDevice.setName("Test Device");
        testDevice.setType(Device.DeviceType.CAMERA);
        testDevice.setStatus(Device.DeviceStatus.ONLINE);
        testDevice.setIpAddress("192.168.1.100");
        testDevice.setPort(8080);
        testDevice.setLocation("Test Location");
        testDevice.setDescription("Test Description");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        testDevice.setLastHeartbeat(LocalDateTime.now());
    }

    @Test
    void createDevice_Success() throws Exception {
        when(deviceService.createDevice(any(Device.class))).thenReturn(testDevice);

        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDevice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.deviceId").value("DEVICE001"))
                .andExpect(jsonPath("$.name").value("Test Device"));

        verify(deviceService).createDevice(any(Device.class));
    }

    @Test
    void createDevice_DeviceIdExists() throws Exception {
        when(deviceService.createDevice(any(Device.class)))
                .thenThrow(new RuntimeException("设备ID已存在"));

        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDevice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("设备ID已存在"));

        verify(deviceService).createDevice(any(Device.class));
    }

    @Test
    void getAllDevices_Success() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceService.getAllDevices()).thenReturn(devices);

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].deviceId").value("DEVICE001"));

        verify(deviceService).getAllDevices();
    }

    @Test
    void getDevices_WithPagination() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        Page<Device> devicePage = new PageImpl<>(devices, PageRequest.of(0, 10), 1);
        when(deviceService.getDevices(any(Pageable.class))).thenReturn(devicePage);

        mockMvc.perform(get("/api/devices/page")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(deviceService).getDevices(any(Pageable.class));
    }

    @Test
    void getDeviceById_Success() throws Exception {
        when(deviceService.getDeviceById(1L)).thenReturn(Optional.of(testDevice));

        mockMvc.perform(get("/api/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.deviceId").value("DEVICE001"));

        verify(deviceService).getDeviceById(1L);
    }

    @Test
    void getDeviceById_NotFound() throws Exception {
        when(deviceService.getDeviceById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/devices/1"))
                .andExpect(status().isNotFound());

        verify(deviceService).getDeviceById(1L);
    }

    @Test
    void getDeviceByDeviceId_Success() throws Exception {
        when(deviceService.getDeviceByDeviceId("DEVICE001")).thenReturn(Optional.of(testDevice));

        mockMvc.perform(get("/api/devices/device-id/DEVICE001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.deviceId").value("DEVICE001"));

        verify(deviceService).getDeviceByDeviceId("DEVICE001");
    }

    @Test
    void getDeviceByDeviceId_NotFound() throws Exception {
        when(deviceService.getDeviceByDeviceId("DEVICE001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/devices/device-id/DEVICE001"))
                .andExpect(status().isNotFound());

        verify(deviceService).getDeviceByDeviceId("DEVICE001");
    }

    @Test
    void updateDevice_Success() throws Exception {
        Device updatedDevice = new Device();
        updatedDevice.setId(1L);
        updatedDevice.setName("Updated Device");
        
        when(deviceService.updateDevice(eq(1L), any(Device.class))).thenReturn(updatedDevice);

        mockMvc.perform(put("/api/devices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDevice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(deviceService).updateDevice(eq(1L), any(Device.class));
    }

    @Test
    void updateDevice_NotFound() throws Exception {
        when(deviceService.updateDevice(eq(1L), any(Device.class)))
                .thenThrow(new RuntimeException("设备不存在"));

        mockMvc.perform(put("/api/devices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDevice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("设备不存在"));

        verify(deviceService).updateDevice(eq(1L), any(Device.class));
    }

    @Test
    void deleteDevice_Success() throws Exception {
        doNothing().when(deviceService).deleteDevice(1L);

        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("设备删除成功"));

        verify(deviceService).deleteDevice(1L);
    }

    @Test
    void deleteDevice_NotFound() throws Exception {
        doThrow(new RuntimeException("设备不存在")).when(deviceService).deleteDevice(1L);

        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("设备不存在"));

        verify(deviceService).deleteDevice(1L);
    }

    @Test
    void getDevicesByType_Success() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceService.getDevicesByType(Device.DeviceType.CAMERA)).thenReturn(devices);

        mockMvc.perform(get("/api/devices/type/CAMERA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("CAMERA"));

        verify(deviceService).getDevicesByType(Device.DeviceType.CAMERA);
    }

    @Test
    void getDevicesByStatus_Success() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceService.getDevicesByStatus(Device.DeviceStatus.ONLINE)).thenReturn(devices);

        mockMvc.perform(get("/api/devices/status/ONLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ONLINE"));

        verify(deviceService).getDevicesByStatus(Device.DeviceStatus.ONLINE);
    }

    @Test
    void searchDevices_Success() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceService.searchDevices("test")).thenReturn(devices);

        mockMvc.perform(get("/api/devices/search")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Device"));

        verify(deviceService).searchDevices("test");
    }

    @Test
    void searchDevicesPage_Success() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        Page<Device> devicePage = new PageImpl<>(devices, PageRequest.of(0, 10), 1);
        when(deviceService.searchDevices(eq("test"), any(Pageable.class))).thenReturn(devicePage);

        mockMvc.perform(get("/api/devices/search/page")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Device"));

        verify(deviceService).searchDevices(eq("test"), any(Pageable.class));
    }

    @Test
    void getOnlineDevices_Success() throws Exception {
        List<Device> devices = Arrays.asList(testDevice);
        when(deviceService.getOnlineDevices()).thenReturn(devices);

        mockMvc.perform(get("/api/devices/online"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ONLINE"));

        verify(deviceService).getOnlineDevices();
    }

    @Test
    void getOfflineDevices_Success() throws Exception {
        Device offlineDevice = new Device();
        offlineDevice.setStatus(Device.DeviceStatus.OFFLINE);
        List<Device> devices = Arrays.asList(offlineDevice);
        when(deviceService.getOfflineDevices()).thenReturn(devices);

        mockMvc.perform(get("/api/devices/offline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(deviceService).getOfflineDevices();
    }

    @Test
    void updateDeviceStatus_Success() throws Exception {
        Device updatedDevice = new Device();
        updatedDevice.setId(1L);
        updatedDevice.setStatus(Device.DeviceStatus.MAINTENANCE);
        
        when(deviceService.updateDeviceStatus(1L, Device.DeviceStatus.MAINTENANCE))
                .thenReturn(updatedDevice);

        Map<String, String> statusUpdate = Map.of("status", "MAINTENANCE");

        mockMvc.perform(patch("/api/devices/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(deviceService).updateDeviceStatus(1L, Device.DeviceStatus.MAINTENANCE);
    }

    @Test
    void updateDeviceStatus_InvalidStatus() throws Exception {
        Map<String, String> statusUpdate = Map.of("status", "INVALID_STATUS");

        mockMvc.perform(patch("/api/devices/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("无效的设备状态"));

        verify(deviceService, never()).updateDeviceStatus(anyLong(), any());
    }

    @Test
    void deviceHeartbeat_Success() throws Exception {
        when(deviceService.updateDeviceHeartbeat("DEVICE001")).thenReturn(testDevice);

        mockMvc.perform(post("/api/devices/DEVICE001/heartbeat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("心跳更新成功"))
                .andExpect(jsonPath("$.device.deviceId").value("DEVICE001"));

        verify(deviceService).updateDeviceHeartbeat("DEVICE001");
    }

    @Test
    void deviceHeartbeat_DeviceNotFound() throws Exception {
        when(deviceService.updateDeviceHeartbeat("DEVICE001"))
                .thenThrow(new RuntimeException("设备不存在"));

        mockMvc.perform(post("/api/devices/DEVICE001/heartbeat"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("设备不存在"));

        verify(deviceService).updateDeviceHeartbeat("DEVICE001");
    }

    @Test
    void getDevicesNeedingMaintenance_Success() throws Exception {
        Device maintenanceDevice = new Device();
        maintenanceDevice.setStatus(Device.DeviceStatus.MAINTENANCE);
        List<Device> devices = Arrays.asList(maintenanceDevice);
        
        when(deviceService.getDevicesNeedingMaintenance()).thenReturn(devices);

        mockMvc.perform(get("/api/devices/maintenance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(deviceService).getDevicesNeedingMaintenance();
    }

    @Test
    void getDeviceStatistics_Success() throws Exception {
        Map<String, Object> statistics = Map.of(
                "totalDevices", 10,
                "onlineDevices", 8,
                "offlineDevices", 2
        );
        
        when(deviceService.getDeviceStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/devices/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDevices").value(10))
                .andExpect(jsonPath("$.onlineDevices").value(8))
                .andExpect(jsonPath("$.offlineDevices").value(2));

        verify(deviceService).getDeviceStatistics();
    }

    @Test
    void checkDeviceConnection_Success() throws Exception {
        when(deviceService.checkDeviceConnection("DEVICE001")).thenReturn(true);

        mockMvc.perform(get("/api/devices/DEVICE001/connection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value("DEVICE001"))
                .andExpect(jsonPath("$.connected").value(true));

        verify(deviceService).checkDeviceConnection("DEVICE001");
    }

    @Test
    void restartDevice_Success() throws Exception {
        when(deviceService.restartDevice(1L)).thenReturn(testDevice);

        mockMvc.perform(post("/api/devices/1/restart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("设备重启命令已发送"))
                .andExpect(jsonPath("$.device.id").value(1L));

        verify(deviceService).restartDevice(1L);
    }

    @Test
    void restartDevice_DeviceNotFound() throws Exception {
        when(deviceService.restartDevice(1L))
                .thenThrow(new RuntimeException("设备不存在"));

        mockMvc.perform(post("/api/devices/1/restart"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("设备不存在"));

        verify(deviceService).restartDevice(1L);
    }

    @Test
    void batchUpdateDeviceStatus_Success() throws Exception {
        Map<String, Object> request = Map.of(
                "deviceIds", Arrays.asList(1L, 2L),
                "status", "MAINTENANCE"
        );
        
        doNothing().when(deviceService).batchUpdateDeviceStatus(anyList(), any());

        mockMvc.perform(patch("/api/devices/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("批量更新设备状态成功"));

        verify(deviceService).batchUpdateDeviceStatus(anyList(), eq(Device.DeviceStatus.MAINTENANCE));
    }

    @Test
    void batchUpdateDeviceStatus_InvalidStatus() throws Exception {
        Map<String, Object> request = Map.of(
                "deviceIds", Arrays.asList(1L, 2L),
                "status", "INVALID_STATUS"
        );

        mockMvc.perform(patch("/api/devices/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("无效的设备状态"));

        verify(deviceService, never()).batchUpdateDeviceStatus(anyList(), any());
    }
}