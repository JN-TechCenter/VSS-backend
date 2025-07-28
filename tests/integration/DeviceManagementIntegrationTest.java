package com.vision.vision_platform_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.repository.DeviceRepository;
import com.vision.vision_platform_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备管理集成测试
 * 测试设备的完整生命周期管理
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("设备管理集成测试")
public class DeviceManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestUsers();
    }

    private void setupTestUsers() throws Exception {
        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.5fO1aVvS"); // admin123
        admin.setEmail("admin@example.com");
        admin.setFullName("系统管理员");
        admin.setRole(User.UserRole.ADMIN);
        admin.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(admin);

        // 创建普通用户
        User user = new User();
        user.setUsername("user");
        user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.5fO1aVvS"); // admin123
        user.setEmail("user@example.com");
        user.setFullName("普通用户");
        user.setRole(User.UserRole.OPERATOR);
        user.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user);

        // 获取管理员token
        Map<String, String> adminLogin = Map.of("username", "admin", "password", "admin123");
        MvcResult adminResult = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        
        Map<String, Object> adminResponse = objectMapper.readValue(
                adminResult.getResponse().getContentAsString(), Map.class);
        adminToken = (String) adminResponse.get("token");

        // 获取普通用户token
        Map<String, String> userLogin = Map.of("username", "user", "password", "admin123");
        MvcResult userResult = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andReturn();
        
        Map<String, Object> userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), Map.class);
        userToken = (String) userResponse.get("token");
    }

    @Test
    @Order(1)
    @DisplayName("1. 创建设备测试")
    void testCreateDevice() throws Exception {
        // 准备设备数据
        Map<String, Object> deviceData = Map.of(
                "deviceId", "CAM-001",
                "name", "前门摄像头",
                "type", "CAMERA",
                "location", "前门入口",
                "ipAddress", "192.168.1.100",
                "macAddress", "00:11:22:33:44:55",
                "description", "前门监控摄像头"
        );

        // 管理员创建设备
        MvcResult result = mockMvc.perform(post("/api/devices")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("CAM-001"))
                .andExpect(jsonPath("$.name").value("前门摄像头"))
                .andExpect(jsonPath("$.type").value("CAMERA"))
                .andExpect(jsonPath("$.status").value("OFFLINE"))
                .andReturn();

        // 验证设备已保存到数据库
        assertTrue(deviceRepository.existsByDeviceId("CAM-001"));

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("设备创建响应: " + responseContent);
    }

    @Test
    @Order(2)
    @DisplayName("2. 普通用户创建设备权限测试")
    void testUserCreateDevicePermission() throws Exception {
        Map<String, Object> deviceData = Map.of(
                "deviceId", "CAM-002",
                "name", "后门摄像头",
                "type", "CAMERA",
                "location", "后门入口",
                "ipAddress", "192.168.1.101"
        );

        // 普通用户尝试创建设备应该被拒绝
        mockMvc.perform(post("/api/devices")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("3. 获取设备列表测试")
    void testGetDeviceList() throws Exception {
        // 创建测试设备
        Device device1 = createTestDevice("CAM-003", "测试摄像头1", "192.168.1.102");
        Device device2 = createTestDevice("CAM-004", "测试摄像头2", "192.168.1.103");
        deviceRepository.save(device1);
        deviceRepository.save(device2);

        // 管理员获取设备列表
        mockMvc.perform(get("/api/devices")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));

        // 普通用户也可以获取设备列表
        mockMvc.perform(get("/api/devices")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("4. 根据ID获取设备测试")
    void testGetDeviceById() throws Exception {
        // 创建测试设备
        Device device = createTestDevice("CAM-005", "测试摄像头", "192.168.1.104");
        device = deviceRepository.save(device);

        // 获取设备详情
        mockMvc.perform(get("/api/devices/" + device.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value("CAM-005"))
                .andExpect(jsonPath("$.name").value("测试摄像头"))
                .andExpect(jsonPath("$.ipAddress").value("192.168.1.104"));

        // 测试不存在的设备
        mockMvc.perform(get("/api/devices/99999")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("5. 更新设备信息测试")
    void testUpdateDevice() throws Exception {
        // 创建测试设备
        Device device = createTestDevice("CAM-006", "原始名称", "192.168.1.105");
        device = deviceRepository.save(device);

        // 准备更新数据
        Map<String, Object> updateData = Map.of(
                "name", "更新后的名称",
                "location", "更新后的位置",
                "description", "更新后的描述"
        );

        // 管理员更新设备
        mockMvc.perform(put("/api/devices/" + device.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新后的名称"))
                .andExpect(jsonPath("$.location").value("更新后的位置"))
                .andExpect(jsonPath("$.description").value("更新后的描述"));

        // 普通用户尝试更新设备应该被拒绝
        mockMvc.perform(put("/api/devices/" + device.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @DisplayName("6. 设备状态管理测试")
    void testDeviceStatusManagement() throws Exception {
        // 创建测试设备
        Device device = createTestDevice("CAM-007", "状态测试设备", "192.168.1.106");
        device = deviceRepository.save(device);

        // 更新设备状态为在线
        mockMvc.perform(patch("/api/devices/" + device.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "ONLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONLINE"));

        // 更新设备状态为维护中
        mockMvc.perform(patch("/api/devices/" + device.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));

        // 测试无效状态
        mockMvc.perform(patch("/api/devices/" + device.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    @DisplayName("7. 设备心跳更新测试")
    void testDeviceHeartbeat() throws Exception {
        // 创建测试设备
        Device device = createTestDevice("CAM-008", "心跳测试设备", "192.168.1.107");
        device.setStatus(Device.DeviceStatus.ONLINE);
        device = deviceRepository.save(device);

        LocalDateTime beforeHeartbeat = LocalDateTime.now();

        // 更新设备心跳
        mockMvc.perform(post("/api/devices/" + device.getId() + "/heartbeat")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 验证心跳时间已更新
        Device updatedDevice = deviceRepository.findById(device.getId()).orElse(null);
        assertNotNull(updatedDevice);
        assertTrue(updatedDevice.getLastHeartbeat().isAfter(beforeHeartbeat));
    }

    @Test
    @Order(8)
    @DisplayName("8. 设备搜索测试")
    void testDeviceSearch() throws Exception {
        // 创建多个测试设备
        Device camera1 = createTestDevice("CAM-009", "前门摄像头", "192.168.1.108");
        Device camera2 = createTestDevice("CAM-010", "后门摄像头", "192.168.1.109");
        Device sensor1 = createTestDevice("SEN-001", "温度传感器", "192.168.1.110");
        sensor1.setType(Device.DeviceType.SENSOR);
        
        deviceRepository.save(camera1);
        deviceRepository.save(camera2);
        deviceRepository.save(sensor1);

        // 按关键词搜索
        mockMvc.perform(get("/api/devices/search")
                .header("Authorization", "Bearer " + userToken)
                .param("keyword", "摄像头"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));

        // 按设备ID搜索
        mockMvc.perform(get("/api/devices/search")
                .header("Authorization", "Bearer " + userToken)
                .param("keyword", "CAM-009"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].deviceId").value("CAM-009"));
    }

    @Test
    @Order(9)
    @DisplayName("9. 按状态过滤设备测试")
    void testFilterDevicesByStatus() throws Exception {
        // 创建不同状态的设备
        Device onlineDevice = createTestDevice("CAM-011", "在线设备", "192.168.1.111");
        onlineDevice.setStatus(Device.DeviceStatus.ONLINE);
        
        Device offlineDevice = createTestDevice("CAM-012", "离线设备", "192.168.1.112");
        offlineDevice.setStatus(Device.DeviceStatus.OFFLINE);
        
        deviceRepository.save(onlineDevice);
        deviceRepository.save(offlineDevice);

        // 获取在线设备
        mockMvc.perform(get("/api/devices/online")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 获取离线设备
        mockMvc.perform(get("/api/devices/offline")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(10)
    @DisplayName("10. 删除设备测试")
    void testDeleteDevice() throws Exception {
        // 创建测试设备
        Device device = createTestDevice("CAM-013", "待删除设备", "192.168.1.113");
        device = deviceRepository.save(device);
        Long deviceId = device.getId();

        // 管理员删除设备
        mockMvc.perform(delete("/api/devices/" + deviceId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // 验证设备已被删除
        assertFalse(deviceRepository.existsById(deviceId));

        // 普通用户尝试删除设备应该被拒绝
        Device anotherDevice = createTestDevice("CAM-014", "另一个设备", "192.168.1.114");
        anotherDevice = deviceRepository.save(anotherDevice);

        mockMvc.perform(delete("/api/devices/" + anotherDevice.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    @DisplayName("11. 设备统计信息测试")
    void testDeviceStatistics() throws Exception {
        // 创建多个不同状态的设备
        for (int i = 0; i < 3; i++) {
            Device device = createTestDevice("STAT-" + i, "统计设备" + i, "192.168.2." + (100 + i));
            device.setStatus(i % 2 == 0 ? Device.DeviceStatus.ONLINE : Device.DeviceStatus.OFFLINE);
            deviceRepository.save(device);
        }

        // 获取设备统计信息
        mockMvc.perform(get("/api/devices/statistics")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDevices").exists())
                .andExpect(jsonPath("$.onlineDevices").exists())
                .andExpect(jsonPath("$.offlineDevices").exists());
    }

    @Test
    @Order(12)
    @DisplayName("12. 完整设备生命周期测试")
    void testCompleteDeviceLifecycle() throws Exception {
        String deviceId = "LIFECYCLE-001";
        
        // 1. 创建设备
        Map<String, Object> deviceData = Map.of(
                "deviceId", deviceId,
                "name", "生命周期测试设备",
                "type", "CAMERA",
                "location", "测试位置",
                "ipAddress", "192.168.3.100"
        );

        MvcResult createResult = mockMvc.perform(post("/api/devices")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deviceData)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        Map<String, Object> createdDevice = objectMapper.readValue(createResponse, Map.class);
        Integer id = (Integer) createdDevice.get("id");

        // 2. 获取设备信息
        mockMvc.perform(get("/api/devices/" + id)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(deviceId));

        // 3. 更新设备状态
        mockMvc.perform(patch("/api/devices/" + id + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "ONLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONLINE"));

        // 4. 更新心跳
        mockMvc.perform(post("/api/devices/" + id + "/heartbeat")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // 5. 更新设备信息
        Map<String, Object> updateData = Map.of(
                "name", "更新的生命周期设备",
                "location", "更新的位置"
        );

        mockMvc.perform(put("/api/devices/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新的生命周期设备"));

        // 6. 验证最终状态
        mockMvc.perform(get("/api/devices/" + id)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新的生命周期设备"))
                .andExpect(jsonPath("$.location").value("更新的位置"))
                .andExpect(jsonPath("$.status").value("ONLINE"));

        System.out.println("完整设备生命周期测试完成");
    }

    private Device createTestDevice(String deviceId, String name, String ipAddress) {
        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setName(name);
        device.setType(Device.DeviceType.CAMERA);
        device.setStatus(Device.DeviceStatus.OFFLINE);
        device.setLocation("测试位置");
        device.setIpAddress(ipAddress);
        device.setMacAddress("00:11:22:33:44:66");
        device.setDescription("测试设备");
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        return device;
    }
}