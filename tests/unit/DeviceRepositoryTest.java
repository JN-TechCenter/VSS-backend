package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.model.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备仓库单元测试
 */
@DataJpaTest
@ActiveProfiles("test")
class DeviceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeviceRepository deviceRepository;

    private Device camera1;
    private Device camera2;
    private Device sensor1;

    @BeforeEach
    void setUp() {
        // 创建测试摄像头1
        camera1 = new Device();
        camera1.setDeviceId("CAM001");
        camera1.setName("Front Door Camera");
        camera1.setType(Device.DeviceType.CAMERA);
        camera1.setStatus(Device.DeviceStatus.ONLINE);
        camera1.setLocation("Front Entrance");
        camera1.setIpAddress("192.168.1.100");
        camera1.setPort(8080);
        camera1.setMacAddress("00:11:22:33:44:55");
        camera1.setManufacturer("Hikvision");
        camera1.setModel("DS-2CD2T47G1-L");
        camera1.setSerialNumber("SN001");
        camera1.setFirmwareVersion("V5.6.3");
        camera1.setDescription("Main entrance security camera");
        camera1.setLastHeartbeat(LocalDateTime.now().minusMinutes(1));
        camera1.setCreatedBy("admin");

        // 创建测试摄像头2
        camera2 = new Device();
        camera2.setDeviceId("CAM002");
        camera2.setName("Back Door Camera");
        camera2.setType(Device.DeviceType.CAMERA);
        camera2.setStatus(Device.DeviceStatus.OFFLINE);
        camera2.setLocation("Back Entrance");
        camera2.setIpAddress("192.168.1.101");
        camera2.setPort(8080);
        camera2.setMacAddress("00:11:22:33:44:56");
        camera2.setManufacturer("Hikvision");
        camera2.setModel("DS-2CD2T47G1-L");
        camera2.setSerialNumber("SN002");
        camera2.setFirmwareVersion("V5.6.3");
        camera2.setDescription("Back entrance security camera");
        camera2.setLastHeartbeat(LocalDateTime.now().minusHours(2));
        camera2.setNextMaintenanceAt(LocalDateTime.now().minusDays(1)); // 需要维护
        camera2.setCreatedBy("admin");

        // 创建测试传感器
        sensor1 = new Device();
        sensor1.setDeviceId("SENSOR001");
        sensor1.setName("Temperature Sensor");
        sensor1.setType(Device.DeviceType.SENSOR);
        sensor1.setStatus(Device.DeviceStatus.ONLINE);
        sensor1.setLocation("Server Room");
        sensor1.setIpAddress("192.168.1.200");
        sensor1.setPort(9090);
        sensor1.setMacAddress("00:11:22:33:44:57");
        sensor1.setManufacturer("Bosch");
        sensor1.setModel("BME280");
        sensor1.setSerialNumber("SN003");
        sensor1.setDescription("Environmental monitoring sensor");
        sensor1.setLastHeartbeat(LocalDateTime.now().minusMinutes(5));
        sensor1.setCreatedBy("operator");

        // 保存测试数据
        entityManager.persistAndFlush(camera1);
        entityManager.persistAndFlush(camera2);
        entityManager.persistAndFlush(sensor1);
    }

    @Test
    void testFindByDeviceId_Success() {
        // When
        Optional<Device> result = deviceRepository.findByDeviceId("CAM001");

        // Then
        assertTrue(result.isPresent());
        assertEquals("CAM001", result.get().getDeviceId());
        assertEquals("Front Door Camera", result.get().getName());
    }

    @Test
    void testFindByDeviceId_NotFound() {
        // When
        Optional<Device> result = deviceRepository.findByDeviceId("NONEXISTENT");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByName_Success() {
        // When
        Optional<Device> result = deviceRepository.findByName("Temperature Sensor");

        // Then
        assertTrue(result.isPresent());
        assertEquals("SENSOR001", result.get().getDeviceId());
    }

    @Test
    void testFindByType() {
        // When
        List<Device> cameras = deviceRepository.findByType(Device.DeviceType.CAMERA);
        List<Device> sensors = deviceRepository.findByType(Device.DeviceType.SENSOR);

        // Then
        assertEquals(2, cameras.size());
        assertEquals(1, sensors.size());
        assertEquals("SENSOR001", sensors.get(0).getDeviceId());
    }

    @Test
    void testFindByStatus() {
        // When
        List<Device> onlineDevices = deviceRepository.findByStatus(Device.DeviceStatus.ONLINE);
        List<Device> offlineDevices = deviceRepository.findByStatus(Device.DeviceStatus.OFFLINE);

        // Then
        assertEquals(2, onlineDevices.size());
        assertEquals(1, offlineDevices.size());
        assertEquals("CAM002", offlineDevices.get(0).getDeviceId());
    }

    @Test
    void testFindByLocationContaining() {
        // When
        List<Device> entranceDevices = deviceRepository.findByLocationContaining("Entrance");
        List<Device> roomDevices = deviceRepository.findByLocationContaining("Room");

        // Then
        assertEquals(2, entranceDevices.size());
        assertEquals(1, roomDevices.size());
        assertEquals("SENSOR001", roomDevices.get(0).getDeviceId());
    }

    @Test
    void testFindByIpAddress() {
        // When
        Optional<Device> result = deviceRepository.findByIpAddress("192.168.1.100");

        // Then
        assertTrue(result.isPresent());
        assertEquals("CAM001", result.get().getDeviceId());
    }

    @Test
    void testFindByMacAddress() {
        // When
        Optional<Device> result = deviceRepository.findByMacAddress("00:11:22:33:44:55");

        // Then
        assertTrue(result.isPresent());
        assertEquals("CAM001", result.get().getDeviceId());
    }

    @Test
    void testFindByManufacturer() {
        // When
        List<Device> hikvisionDevices = deviceRepository.findByManufacturer("Hikvision");
        List<Device> boschDevices = deviceRepository.findByManufacturer("Bosch");

        // Then
        assertEquals(2, hikvisionDevices.size());
        assertEquals(1, boschDevices.size());
    }

    @Test
    void testFindByTypeAndStatus_WithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Device> result = deviceRepository.findByTypeAndStatus(
                Device.DeviceType.CAMERA, Device.DeviceStatus.ONLINE, pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("CAM001", result.getContent().get(0).getDeviceId());
    }

    @Test
    void testSearchDevices() {
        // When
        List<Device> results = deviceRepository.searchDevices("Front");

        // Then
        assertEquals(1, results.size());
        assertEquals("CAM001", results.get(0).getDeviceId());
    }

    @Test
    void testSearchDevices_WithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Device> results = deviceRepository.searchDevices("192.168", pageable);

        // Then
        assertEquals(3, results.getTotalElements());
        assertEquals(2, results.getContent().size());
    }

    @Test
    void testFindOnlineDevices() {
        // When
        List<Device> onlineDevices = deviceRepository.findOnlineDevices();

        // Then
        assertEquals(2, onlineDevices.size());
        assertTrue(onlineDevices.stream().allMatch(d -> d.getStatus() == Device.DeviceStatus.ONLINE));
    }

    @Test
    void testFindOfflineDevices() {
        // When
        List<Device> offlineDevices = deviceRepository.findOfflineDevices();

        // Then
        assertEquals(1, offlineDevices.size());
        assertEquals("CAM002", offlineDevices.get(0).getDeviceId());
    }

    @Test
    void testFindDevicesNeedingMaintenance() {
        // When
        List<Device> devicesNeedingMaintenance = deviceRepository.findDevicesNeedingMaintenance(LocalDateTime.now());

        // Then
        assertEquals(1, devicesNeedingMaintenance.size());
        assertEquals("CAM002", devicesNeedingMaintenance.get(0).getDeviceId());
    }

    @Test
    void testFindDevicesWithOldHeartbeat() {
        // Given
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

        // When
        List<Device> devicesWithOldHeartbeat = deviceRepository.findDevicesWithOldHeartbeat(threshold);

        // Then
        assertEquals(1, devicesWithOldHeartbeat.size());
        assertEquals("CAM002", devicesWithOldHeartbeat.get(0).getDeviceId());
    }

    @Test
    void testCountDevicesByStatus() {
        // When
        List<Object[]> statusCounts = deviceRepository.countDevicesByStatus();

        // Then
        assertEquals(2, statusCounts.size());
        
        for (Object[] statusCount : statusCounts) {
            Device.DeviceStatus status = (Device.DeviceStatus) statusCount[0];
            Long count = (Long) statusCount[1];
            
            if (status == Device.DeviceStatus.ONLINE) {
                assertEquals(2L, count);
            } else if (status == Device.DeviceStatus.OFFLINE) {
                assertEquals(1L, count);
            }
        }
    }

    @Test
    void testCountDevicesByType() {
        // When
        List<Object[]> typeCounts = deviceRepository.countDevicesByType();

        // Then
        assertEquals(2, typeCounts.size());
        
        for (Object[] typeCount : typeCounts) {
            Device.DeviceType type = (Device.DeviceType) typeCount[0];
            Long count = (Long) typeCount[1];
            
            if (type == Device.DeviceType.CAMERA) {
                assertEquals(2L, count);
            } else if (type == Device.DeviceType.SENSOR) {
                assertEquals(1L, count);
            }
        }
    }

    @Test
    void testCountByStatus() {
        // When
        long onlineCount = deviceRepository.countByStatus(Device.DeviceStatus.ONLINE);
        long offlineCount = deviceRepository.countByStatus(Device.DeviceStatus.OFFLINE);
        long maintenanceCount = deviceRepository.countByStatus(Device.DeviceStatus.MAINTENANCE);

        // Then
        assertEquals(2, onlineCount);
        assertEquals(1, offlineCount);
        assertEquals(0, maintenanceCount);
    }

    @Test
    void testCountByType() {
        // When
        long cameraCount = deviceRepository.countByType(Device.DeviceType.CAMERA);
        long sensorCount = deviceRepository.countByType(Device.DeviceType.SENSOR);
        long controllerCount = deviceRepository.countByType(Device.DeviceType.CONTROLLER);

        // Then
        assertEquals(2, cameraCount);
        assertEquals(1, sensorCount);
        assertEquals(0, controllerCount);
    }

    @Test
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // When
        List<Device> devices = deviceRepository.findByCreatedAtBetween(start, end);

        // Then
        assertEquals(3, devices.size());
    }

    @Test
    void testFindByCreatedBy() {
        // When
        List<Device> adminDevices = deviceRepository.findByCreatedBy("admin");
        List<Device> operatorDevices = deviceRepository.findByCreatedBy("operator");

        // Then
        assertEquals(2, adminDevices.size());
        assertEquals(1, operatorDevices.size());
    }

    @Test
    void testExistsByDeviceId() {
        // When
        boolean exists = deviceRepository.existsByDeviceId("CAM001");
        boolean notExists = deviceRepository.existsByDeviceId("NONEXISTENT");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testExistsByIpAddress() {
        // When
        boolean exists = deviceRepository.existsByIpAddress("192.168.1.100");
        boolean notExists = deviceRepository.existsByIpAddress("192.168.1.999");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testExistsByMacAddress() {
        // When
        boolean exists = deviceRepository.existsByMacAddress("00:11:22:33:44:55");
        boolean notExists = deviceRepository.existsByMacAddress("00:00:00:00:00:00");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testSaveAndUpdate() {
        // Given
        Device newDevice = new Device();
        newDevice.setDeviceId("TEST001");
        newDevice.setName("Test Device");
        newDevice.setType(Device.DeviceType.CONTROLLER);
        newDevice.setStatus(Device.DeviceStatus.OFFLINE);
        newDevice.setLocation("Test Location");
        newDevice.setCreatedBy("test");

        // When
        Device savedDevice = deviceRepository.save(newDevice);
        
        // Update the device
        savedDevice.setStatus(Device.DeviceStatus.ONLINE);
        savedDevice.setUpdatedBy("admin");
        Device updatedDevice = deviceRepository.save(savedDevice);

        // Then
        assertNotNull(savedDevice.getId());
        assertEquals(Device.DeviceStatus.ONLINE, updatedDevice.getStatus());
        assertEquals("admin", updatedDevice.getUpdatedBy());
    }

    @Test
    void testDeleteDevice() {
        // Given
        Long deviceId = camera1.getId();

        // When
        deviceRepository.deleteById(deviceId);
        Optional<Device> deletedDevice = deviceRepository.findById(deviceId);

        // Then
        assertFalse(deletedDevice.isPresent());
    }
}