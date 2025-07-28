package com.vision.vision_platform_backend.test.config;

import com.vision.vision_platform_backend.model.*;
import com.vision.vision_platform_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 测试数据初始化工具类
 * 用于在测试中创建和管理测试数据
 */
@TestComponent
public class TestDataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private VideoStreamRepository videoStreamRepository;

    @Autowired
    private InferenceHistoryRepository inferenceHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 初始化测试用户数据
     */
    @Transactional
    public List<User> initTestUsers() {
        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        // 创建普通用户
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("user123"));
        user1.setEmail("user1@test.com");
        user1.setRole("USER");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("user123"));
        user2.setEmail("user2@test.com");
        user2.setRole("USER");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        return userRepository.saveAll(Arrays.asList(admin, user1, user2));
    }

    /**
     * 初始化测试设备数据
     */
    @Transactional
    public List<Device> initTestDevices() {
        Device device1 = new Device();
        device1.setName("Test Camera 1");
        device1.setType("CAMERA");
        device1.setIpAddress("192.168.1.100");
        device1.setPort(8080);
        device1.setStatus("ONLINE");
        device1.setLocation("Test Location 1");
        device1.setDescription("Test camera device 1");
        device1.setCreatedAt(LocalDateTime.now());
        device1.setUpdatedAt(LocalDateTime.now());
        device1.setLastHeartbeat(LocalDateTime.now());

        Device device2 = new Device();
        device2.setName("Test Camera 2");
        device2.setType("CAMERA");
        device2.setIpAddress("192.168.1.101");
        device2.setPort(8080);
        device2.setStatus("OFFLINE");
        device2.setLocation("Test Location 2");
        device2.setDescription("Test camera device 2");
        device2.setCreatedAt(LocalDateTime.now());
        device2.setUpdatedAt(LocalDateTime.now());
        device2.setLastHeartbeat(LocalDateTime.now().minusMinutes(10));

        Device device3 = new Device();
        device3.setName("Test Sensor 1");
        device3.setType("SENSOR");
        device3.setIpAddress("192.168.1.102");
        device3.setPort(9090);
        device3.setStatus("ONLINE");
        device3.setLocation("Test Location 3");
        device3.setDescription("Test sensor device");
        device3.setCreatedAt(LocalDateTime.now());
        device3.setUpdatedAt(LocalDateTime.now());
        device3.setLastHeartbeat(LocalDateTime.now());

        return deviceRepository.saveAll(Arrays.asList(device1, device2, device3));
    }

    /**
     * 初始化测试视频流数据
     */
    @Transactional
    public List<VideoStream> initTestVideoStreams() {
        VideoStream stream1 = new VideoStream();
        stream1.setName("Test Stream 1");
        stream1.setUrl("rtmp://test.com/stream1");
        stream1.setType("RTMP");
        stream1.setStatus("ACTIVE");
        stream1.setResolution("1920x1080");
        stream1.setFrameRate(30);
        stream1.setBitrate(2000);
        stream1.setDescription("Test video stream 1");
        stream1.setCreatedAt(LocalDateTime.now());
        stream1.setUpdatedAt(LocalDateTime.now());

        VideoStream stream2 = new VideoStream();
        stream2.setName("Test Stream 2");
        stream2.setUrl("http://test.com/stream2.m3u8");
        stream2.setType("HLS");
        stream2.setStatus("INACTIVE");
        stream2.setResolution("1280x720");
        stream2.setFrameRate(25);
        stream2.setBitrate(1500);
        stream2.setDescription("Test video stream 2");
        stream2.setCreatedAt(LocalDateTime.now());
        stream2.setUpdatedAt(LocalDateTime.now());

        return videoStreamRepository.saveAll(Arrays.asList(stream1, stream2));
    }

    /**
     * 初始化测试推理历史数据
     */
    @Transactional
    public List<InferenceHistory> initTestInferenceHistory() {
        InferenceHistory history1 = new InferenceHistory();
        history1.setTaskId("task-001");
        history1.setModelName("yolo-v5");
        history1.setInputData("test-image-1.jpg");
        history1.setOutputData("{\"detections\": [{\"class\": \"person\", \"confidence\": 0.95}]}");
        history1.setStatus("COMPLETED");
        history1.setProcessingTime(1500L);
        history1.setCreatedAt(LocalDateTime.now().minusHours(1));
        history1.setCompletedAt(LocalDateTime.now().minusHours(1).plusSeconds(2));

        InferenceHistory history2 = new InferenceHistory();
        history2.setTaskId("task-002");
        history2.setModelName("yolo-v5");
        history2.setInputData("test-image-2.jpg");
        history2.setOutputData("{\"detections\": [{\"class\": \"car\", \"confidence\": 0.88}]}");
        history2.setStatus("COMPLETED");
        history2.setProcessingTime(1200L);
        history2.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        history2.setCompletedAt(LocalDateTime.now().minusMinutes(30).plusSeconds(1));

        InferenceHistory history3 = new InferenceHistory();
        history3.setTaskId("task-003");
        history3.setModelName("yolo-v5");
        history3.setInputData("test-image-3.jpg");
        history3.setStatus("PROCESSING");
        history3.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        return inferenceHistoryRepository.saveAll(Arrays.asList(history1, history2, history3));
    }

    /**
     * 清理所有测试数据
     */
    @Transactional
    public void cleanupTestData() {
        inferenceHistoryRepository.deleteAll();
        videoStreamRepository.deleteAll();
        deviceRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * 创建测试用户（不保存到数据库）
     */
    public User createTestUser(String username, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("test123"));
        user.setEmail(email);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * 创建测试设备（不保存到数据库）
     */
    public Device createTestDevice(String name, String type, String ipAddress) {
        Device device = new Device();
        device.setName(name);
        device.setType(type);
        device.setIpAddress(ipAddress);
        device.setPort(8080);
        device.setStatus("ONLINE");
        device.setLocation("Test Location");
        device.setDescription("Test device");
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        device.setLastHeartbeat(LocalDateTime.now());
        return device;
    }

    /**
     * 创建测试视频流（不保存到数据库）
     */
    public VideoStream createTestVideoStream(String name, String url, String type) {
        VideoStream stream = new VideoStream();
        stream.setName(name);
        stream.setUrl(url);
        stream.setType(type);
        stream.setStatus("ACTIVE");
        stream.setResolution("1920x1080");
        stream.setFrameRate(30);
        stream.setBitrate(2000);
        stream.setDescription("Test video stream");
        stream.setCreatedAt(LocalDateTime.now());
        stream.setUpdatedAt(LocalDateTime.now());
        return stream;
    }

    /**
     * 创建测试推理历史（不保存到数据库）
     */
    public InferenceHistory createTestInferenceHistory(String taskId, String modelName, String status) {
        InferenceHistory history = new InferenceHistory();
        history.setTaskId(taskId);
        history.setModelName(modelName);
        history.setInputData("test-input.jpg");
        history.setStatus(status);
        history.setCreatedAt(LocalDateTime.now());
        
        if ("COMPLETED".equals(status)) {
            history.setOutputData("{\"detections\": []}");
            history.setProcessingTime(1000L);
            history.setCompletedAt(LocalDateTime.now().plusSeconds(1));
        }
        
        return history;
    }
}