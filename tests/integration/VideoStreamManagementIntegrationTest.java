package com.vision.vision_platform_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.repository.DeviceRepository;
import com.vision.vision_platform_backend.repository.UserRepository;
import com.vision.vision_platform_backend.repository.VideoStreamRepository;
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
 * 视频流管理集成测试
 * 测试视频流的完整生命周期管理
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("视频流管理集成测试")
public class VideoStreamManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VideoStreamRepository videoStreamRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;
    private Device testDevice;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestUsers();
        setupTestDevice();
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

    private void setupTestDevice() {
        testDevice = new Device();
        testDevice.setDeviceId("CAM-STREAM-001");
        testDevice.setName("流测试摄像头");
        testDevice.setType(Device.DeviceType.CAMERA);
        testDevice.setStatus(Device.DeviceStatus.ONLINE);
        testDevice.setLocation("测试位置");
        testDevice.setIpAddress("192.168.1.200");
        testDevice.setMacAddress("00:11:22:33:44:77");
        testDevice.setDescription("用于流测试的设备");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        testDevice = deviceRepository.save(testDevice);
    }

    @Test
    @Order(1)
    @DisplayName("1. 创建视频流测试")
    void testCreateVideoStream() throws Exception {
        // 准备视频流数据
        Map<String, Object> streamData = Map.of(
                "streamId", "STREAM-001",
                "name", "前门监控流",
                "deviceId", testDevice.getId(),
                "streamUrl", "rtmp://192.168.1.200:1935/live/stream001",
                "type", "LIVE",
                "resolution", "1920x1080",
                "frameRate", 30,
                "bitrate", 2000,
                "description", "前门实时监控视频流"
        );

        // 管理员创建视频流
        MvcResult result = mockMvc.perform(post("/api/streams")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(streamData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.streamId").value("STREAM-001"))
                .andExpect(jsonPath("$.name").value("前门监控流"))
                .andExpect(jsonPath("$.type").value("LIVE"))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andReturn();

        // 验证视频流已保存到数据库
        assertTrue(videoStreamRepository.existsByStreamId("STREAM-001"));

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("视频流创建响应: " + responseContent);
    }

    @Test
    @Order(2)
    @DisplayName("2. 普通用户创建视频流权限测试")
    void testUserCreateStreamPermission() throws Exception {
        Map<String, Object> streamData = Map.of(
                "streamId", "STREAM-002",
                "name", "后门监控流",
                "deviceId", testDevice.getId(),
                "streamUrl", "rtmp://192.168.1.200:1935/live/stream002",
                "type", "LIVE"
        );

        // 普通用户尝试创建视频流应该被拒绝
        mockMvc.perform(post("/api/streams")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(streamData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("3. 获取视频流列表测试")
    void testGetStreamList() throws Exception {
        // 创建测试视频流
        VideoStream stream1 = createTestStream("STREAM-003", "测试流1", "rtmp://test1");
        VideoStream stream2 = createTestStream("STREAM-004", "测试流2", "rtmp://test2");
        videoStreamRepository.save(stream1);
        videoStreamRepository.save(stream2);

        // 管理员获取视频流列表
        mockMvc.perform(get("/api/streams")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));

        // 普通用户也可以获取视频流列表
        mockMvc.perform(get("/api/streams")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("4. 根据ID获取视频流测试")
    void testGetStreamById() throws Exception {
        // 创建测试视频流
        VideoStream stream = createTestStream("STREAM-005", "测试流", "rtmp://test");
        stream = videoStreamRepository.save(stream);

        // 获取视频流详情
        mockMvc.perform(get("/api/streams/" + stream.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streamId").value("STREAM-005"))
                .andExpect(jsonPath("$.name").value("测试流"))
                .andExpect(jsonPath("$.streamUrl").value("rtmp://test"));

        // 测试不存在的视频流
        mockMvc.perform(get("/api/streams/99999")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @DisplayName("5. 更新视频流信息测试")
    void testUpdateStream() throws Exception {
        // 创建测试视频流
        VideoStream stream = createTestStream("STREAM-006", "原始名称", "rtmp://original");
        stream = videoStreamRepository.save(stream);

        // 准备更新数据
        Map<String, Object> updateData = Map.of(
                "name", "更新后的名称",
                "resolution", "1280x720",
                "frameRate", 25,
                "description", "更新后的描述"
        );

        // 管理员更新视频流
        mockMvc.perform(put("/api/streams/" + stream.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新后的名称"))
                .andExpect(jsonPath("$.resolution").value("1280x720"))
                .andExpect(jsonPath("$.frameRate").value(25))
                .andExpect(jsonPath("$.description").value("更新后的描述"));

        // 普通用户尝试更新视频流应该被拒绝
        mockMvc.perform(put("/api/streams/" + stream.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @DisplayName("6. 视频流状态管理测试")
    void testStreamStatusManagement() throws Exception {
        // 创建测试视频流
        VideoStream stream = createTestStream("STREAM-007", "状态测试流", "rtmp://status");
        stream = videoStreamRepository.save(stream);

        // 启动视频流
        mockMvc.perform(patch("/api/streams/" + stream.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // 停止视频流
        mockMvc.perform(patch("/api/streams/" + stream.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        // 设置错误状态
        mockMvc.perform(patch("/api/streams/" + stream.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "ERROR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));

        // 测试无效状态
        mockMvc.perform(patch("/api/streams/" + stream.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    @DisplayName("7. 视频流统计信息更新测试")
    void testStreamStatisticsUpdate() throws Exception {
        // 创建测试视频流
        VideoStream stream = createTestStream("STREAM-008", "统计测试流", "rtmp://stats");
        stream.setStatus(VideoStream.StreamStatus.ACTIVE);
        stream = videoStreamRepository.save(stream);

        // 更新观看人数
        mockMvc.perform(patch("/api/streams/" + stream.getId() + "/viewers")
                .header("Authorization", "Bearer " + userToken)
                .param("count", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewerCount").value(10));

        // 更新性能指标
        Map<String, Object> performanceData = Map.of(
                "cpuUsage", 45.5,
                "memoryUsage", 60.2,
                "networkBandwidth", 1500.0
        );

        mockMvc.perform(patch("/api/streams/" + stream.getId() + "/performance")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(performanceData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpuUsage").value(45.5))
                .andExpect(jsonPath("$.memoryUsage").value(60.2))
                .andExpect(jsonPath("$.networkBandwidth").value(1500.0));
    }

    @Test
    @Order(8)
    @DisplayName("8. 视频流搜索测试")
    void testStreamSearch() throws Exception {
        // 创建多个测试视频流
        VideoStream liveStream = createTestStream("LIVE-001", "直播流", "rtmp://live");
        liveStream.setType(VideoStream.StreamType.LIVE);
        
        VideoStream recordStream = createTestStream("REC-001", "录制流", "rtmp://record");
        recordStream.setType(VideoStream.StreamType.RECORDED);
        
        VideoStream playbackStream = createTestStream("PLAY-001", "回放流", "rtmp://playback");
        playbackStream.setType(VideoStream.StreamType.PLAYBACK);
        
        videoStreamRepository.save(liveStream);
        videoStreamRepository.save(recordStream);
        videoStreamRepository.save(playbackStream);

        // 按关键词搜索
        mockMvc.perform(get("/api/streams/search")
                .header("Authorization", "Bearer " + userToken)
                .param("keyword", "直播"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)));

        // 按流ID搜索
        mockMvc.perform(get("/api/streams/search")
                .header("Authorization", "Bearer " + userToken)
                .param("keyword", "LIVE-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].streamId").value("LIVE-001"));
    }

    @Test
    @Order(9)
    @DisplayName("9. 按状态和类型过滤视频流测试")
    void testFilterStreamsByStatusAndType() throws Exception {
        // 创建不同状态和类型的视频流
        VideoStream activeStream = createTestStream("ACTIVE-001", "活跃流", "rtmp://active");
        activeStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        activeStream.setType(VideoStream.StreamType.LIVE);
        
        VideoStream inactiveStream = createTestStream("INACTIVE-001", "非活跃流", "rtmp://inactive");
        inactiveStream.setStatus(VideoStream.StreamStatus.INACTIVE);
        inactiveStream.setType(VideoStream.StreamType.RECORDED);
        
        videoStreamRepository.save(activeStream);
        videoStreamRepository.save(inactiveStream);

        // 获取活跃流
        mockMvc.perform(get("/api/streams/active")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 获取直播流
        mockMvc.perform(get("/api/streams/live")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 获取录制流
        mockMvc.perform(get("/api/streams/recorded")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(10)
    @DisplayName("10. 视频流错误处理测试")
    void testStreamErrorHandling() throws Exception {
        // 创建测试视频流
        VideoStream stream = createTestStream("ERROR-001", "错误测试流", "rtmp://error");
        stream = videoStreamRepository.save(stream);

        // 报告错误
        Map<String, Object> errorData = Map.of(
                "errorMessage", "连接超时",
                "errorCode", "TIMEOUT_ERROR"
        );

        mockMvc.perform(post("/api/streams/" + stream.getId() + "/error")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(errorData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ERROR"));

        // 清除错误
        mockMvc.perform(delete("/api/streams/" + stream.getId() + "/error")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @Order(11)
    @DisplayName("11. 删除视频流测试")
    void testDeleteStream() throws Exception {
        // 创建测试视频流
        VideoStream stream = createTestStream("DELETE-001", "待删除流", "rtmp://delete");
        stream = videoStreamRepository.save(stream);
        Long streamId = stream.getId();

        // 管理员删除视频流
        mockMvc.perform(delete("/api/streams/" + streamId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // 验证视频流已被删除
        assertFalse(videoStreamRepository.existsById(streamId));

        // 普通用户尝试删除视频流应该被拒绝
        VideoStream anotherStream = createTestStream("DELETE-002", "另一个流", "rtmp://another");
        anotherStream = videoStreamRepository.save(anotherStream);

        mockMvc.perform(delete("/api/streams/" + anotherStream.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(12)
    @DisplayName("12. 视频流统计信息测试")
    void testStreamStatistics() throws Exception {
        // 创建多个不同状态的视频流
        for (int i = 0; i < 5; i++) {
            VideoStream stream = createTestStream("STAT-" + i, "统计流" + i, "rtmp://stat" + i);
            stream.setStatus(i % 2 == 0 ? VideoStream.StreamStatus.ACTIVE : VideoStream.StreamStatus.INACTIVE);
            stream.setViewerCount(i * 10);
            videoStreamRepository.save(stream);
        }

        // 获取视频流统计信息
        mockMvc.perform(get("/api/streams/statistics")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStreams").exists())
                .andExpect(jsonPath("$.activeStreams").exists())
                .andExpect(jsonPath("$.totalViewers").exists())
                .andExpect(jsonPath("$.averageCpuUsage").exists())
                .andExpect(jsonPath("$.averageMemoryUsage").exists());
    }

    @Test
    @Order(13)
    @DisplayName("13. 完整视频流生命周期测试")
    void testCompleteStreamLifecycle() throws Exception {
        String streamId = "LIFECYCLE-STREAM-001";
        
        // 1. 创建视频流
        Map<String, Object> streamData = Map.of(
                "streamId", streamId,
                "name", "生命周期测试流",
                "deviceId", testDevice.getId(),
                "streamUrl", "rtmp://192.168.1.200:1935/live/lifecycle",
                "type", "LIVE",
                "resolution", "1920x1080",
                "frameRate", 30
        );

        MvcResult createResult = mockMvc.perform(post("/api/streams")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(streamData)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        Map<String, Object> createdStream = objectMapper.readValue(createResponse, Map.class);
        Integer id = (Integer) createdStream.get("id");

        // 2. 获取视频流信息
        mockMvc.perform(get("/api/streams/" + id)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streamId").value(streamId));

        // 3. 启动视频流
        mockMvc.perform(patch("/api/streams/" + id + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // 4. 更新观看人数
        mockMvc.perform(patch("/api/streams/" + id + "/viewers")
                .header("Authorization", "Bearer " + userToken)
                .param("count", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewerCount").value(50));

        // 5. 更新性能指标
        Map<String, Object> performanceData = Map.of(
                "cpuUsage", 35.0,
                "memoryUsage", 55.0,
                "networkBandwidth", 2000.0
        );

        mockMvc.perform(patch("/api/streams/" + id + "/performance")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(performanceData)))
                .andExpect(status().isOk());

        // 6. 更新视频流信息
        Map<String, Object> updateData = Map.of(
                "name", "更新的生命周期流",
                "description", "完整测试的视频流"
        );

        mockMvc.perform(put("/api/streams/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新的生命周期流"));

        // 7. 停止视频流
        mockMvc.perform(patch("/api/streams/" + id + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        // 8. 验证最终状态
        mockMvc.perform(get("/api/streams/" + id)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新的生命周期流"))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.viewerCount").value(50));

        System.out.println("完整视频流生命周期测试完成");
    }

    private VideoStream createTestStream(String streamId, String name, String streamUrl) {
        VideoStream stream = new VideoStream();
        stream.setStreamId(streamId);
        stream.setName(name);
        stream.setDevice(testDevice);
        stream.setStreamUrl(streamUrl);
        stream.setType(VideoStream.StreamType.LIVE);
        stream.setStatus(VideoStream.StreamStatus.INACTIVE);
        stream.setResolution("1920x1080");
        stream.setFrameRate(30);
        stream.setBitrate(2000);
        stream.setViewerCount(0);
        stream.setCpuUsage(0.0);
        stream.setMemoryUsage(0.0);
        stream.setNetworkBandwidth(0.0);
        stream.setErrorCount(0);
        stream.setDescription("测试视频流");
        stream.setCreatedAt(LocalDateTime.now());
        stream.setUpdatedAt(LocalDateTime.now());
        return stream;
    }
}