package com.vision.vision_platform_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.model.InferenceHistory;
import com.vision.vision_platform_backend.model.User;
import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.repository.DeviceRepository;
import com.vision.vision_platform_backend.repository.InferenceHistoryRepository;
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
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AI推理服务集成测试
 * 测试AI推理的完整流程
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("AI推理服务集成测试")
public class AIInferenceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private InferenceHistoryRepository inferenceHistoryRepository;

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
    private VideoStream testStream;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestUsers();
        setupTestDeviceAndStream();
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

    private void setupTestDeviceAndStream() {
        // 创建测试设备
        testDevice = new Device();
        testDevice.setDeviceId("CAM-AI-001");
        testDevice.setName("AI推理测试摄像头");
        testDevice.setType(Device.DeviceType.CAMERA);
        testDevice.setStatus(Device.DeviceStatus.ONLINE);
        testDevice.setLocation("AI测试位置");
        testDevice.setIpAddress("192.168.1.300");
        testDevice.setMacAddress("00:11:22:33:44:88");
        testDevice.setDescription("用于AI推理测试的设备");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        testDevice = deviceRepository.save(testDevice);

        // 创建测试视频流
        testStream = new VideoStream();
        testStream.setStreamId("AI-STREAM-001");
        testStream.setName("AI推理测试流");
        testStream.setDevice(testDevice);
        testStream.setStreamUrl("rtmp://192.168.1.300:1935/live/ai-stream");
        testStream.setType(VideoStream.StreamType.LIVE);
        testStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        testStream.setResolution("1920x1080");
        testStream.setFrameRate(30);
        testStream.setBitrate(2000);
        testStream.setDescription("AI推理测试视频流");
        testStream.setCreatedAt(LocalDateTime.now());
        testStream.setUpdatedAt(LocalDateTime.now());
        testStream = videoStreamRepository.save(testStream);
    }

    @Test
    @Order(1)
    @DisplayName("1. 启动AI推理任务测试")
    void testStartInferenceTask() throws Exception {
        // 准备推理任务数据
        Map<String, Object> inferenceData = Map.of(
                "streamId", testStream.getId(),
                "modelType", "OBJECT_DETECTION",
                "modelName", "YOLOv5",
                "confidence", 0.7,
                "parameters", Map.of(
                        "classes", List.of("person", "car", "bicycle"),
                        "maxDetections", 100,
                        "nmsThreshold", 0.5
                ),
                "description", "物体检测推理任务"
        );

        // 启动推理任务
        MvcResult result = mockMvc.perform(post("/api/inference/start")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inferenceData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.modelType").value("OBJECT_DETECTION"))
                .andExpect(jsonPath("$.modelName").value("YOLOv5"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("推理任务启动响应: " + responseContent);
    }

    @Test
    @Order(2)
    @DisplayName("2. 批量图像推理测试")
    void testBatchImageInference() throws Exception {
        // 准备批量推理数据
        Map<String, Object> batchData = Map.of(
                "images", List.of(
                        "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=",
                        "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k="
                ),
                "modelType", "FACE_RECOGNITION",
                "modelName", "FaceNet",
                "confidence", 0.8
        );

        // 执行批量推理
        mockMvc.perform(post("/api/inference/batch")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").exists())
                .andExpect(jsonPath("$.totalImages").value(2))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    @Order(3)
    @DisplayName("3. 实时推理结果接收测试")
    void testRealtimeInferenceResults() throws Exception {
        // 创建推理历史记录
        InferenceHistory history = createTestInferenceHistory();
        history = inferenceHistoryRepository.save(history);

        // 模拟推理结果
        Map<String, Object> resultData = Map.of(
                "taskId", history.getTaskId(),
                "timestamp", System.currentTimeMillis(),
                "detections", List.of(
                        Map.of(
                                "class", "person",
                                "confidence", 0.95,
                                "bbox", List.of(100, 150, 200, 300)
                        ),
                        Map.of(
                                "class", "car",
                                "confidence", 0.88,
                                "bbox", List.of(300, 200, 450, 350)
                        )
                ),
                "processingTime", 45.2
        );

        // 提交推理结果
        mockMvc.perform(post("/api/inference/results")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resultData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Order(4)
    @DisplayName("4. 获取推理历史记录测试")
    void testGetInferenceHistory() throws Exception {
        // 创建多个推理历史记录
        for (int i = 0; i < 3; i++) {
            InferenceHistory history = createTestInferenceHistory();
            history.setTaskId("TASK-" + i);
            history.setModelName("TestModel" + i);
            inferenceHistoryRepository.save(history);
        }

        // 获取推理历史列表
        mockMvc.perform(get("/api/inference/history")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)));

        // 分页获取推理历史
        mockMvc.perform(get("/api/inference/history")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)));
    }

    @Test
    @Order(5)
    @DisplayName("5. 根据ID获取推理详情测试")
    void testGetInferenceById() throws Exception {
        // 创建推理历史记录
        InferenceHistory history = createTestInferenceHistory();
        history = inferenceHistoryRepository.save(history);

        // 获取推理详情
        mockMvc.perform(get("/api/inference/" + history.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(history.getTaskId()))
                .andExpect(jsonPath("$.modelType").value("OBJECT_DETECTION"))
                .andExpect(jsonPath("$.modelName").value(history.getModelName()));

        // 测试不存在的推理记录
        mockMvc.perform(get("/api/inference/99999")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @DisplayName("6. 停止推理任务测试")
    void testStopInferenceTask() throws Exception {
        // 创建运行中的推理任务
        InferenceHistory history = createTestInferenceHistory();
        history.setStatus(InferenceHistory.InferenceStatus.RUNNING);
        history = inferenceHistoryRepository.save(history);

        // 停止推理任务
        mockMvc.perform(post("/api/inference/" + history.getTaskId() + "/stop")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STOPPED"));

        // 验证任务状态已更新
        InferenceHistory updatedHistory = inferenceHistoryRepository.findById(history.getId()).orElse(null);
        assertNotNull(updatedHistory);
        assertEquals(InferenceHistory.InferenceStatus.STOPPED, updatedHistory.getStatus());
    }

    @Test
    @Order(7)
    @DisplayName("7. 推理模型管理测试")
    void testInferenceModelManagement() throws Exception {
        // 获取可用模型列表
        mockMvc.perform(get("/api/inference/models")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 获取特定类型的模型
        mockMvc.perform(get("/api/inference/models")
                .header("Authorization", "Bearer " + userToken)
                .param("type", "OBJECT_DETECTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 管理员添加新模型
        Map<String, Object> modelData = Map.of(
                "name", "CustomYOLO",
                "type", "OBJECT_DETECTION",
                "version", "1.0",
                "description", "自定义YOLO模型",
                "parameters", Map.of(
                        "inputSize", "640x640",
                        "classes", 80,
                        "anchors", List.of(10, 13, 16, 30, 33, 23)
                )
        );

        mockMvc.perform(post("/api/inference/models")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modelData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("CustomYOLO"))
                .andExpect(jsonPath("$.type").value("OBJECT_DETECTION"));
    }

    @Test
    @Order(8)
    @DisplayName("8. 推理性能监控测试")
    void testInferencePerformanceMonitoring() throws Exception {
        // 创建多个推理记录用于性能统计
        for (int i = 0; i < 5; i++) {
            InferenceHistory history = createTestInferenceHistory();
            history.setTaskId("PERF-TASK-" + i);
            history.setProcessingTime(50.0 + i * 10);
            history.setAccuracy(0.85 + i * 0.02);
            history.setStatus(InferenceHistory.InferenceStatus.COMPLETED);
            inferenceHistoryRepository.save(history);
        }

        // 获取推理性能统计
        mockMvc.perform(get("/api/inference/performance")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInferences").exists())
                .andExpect(jsonPath("$.averageProcessingTime").exists())
                .andExpect(jsonPath("$.averageAccuracy").exists())
                .andExpect(jsonPath("$.successRate").exists());

        // 获取特定时间范围的性能统计
        mockMvc.perform(get("/api/inference/performance")
                .header("Authorization", "Bearer " + adminToken)
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInferences").exists());
    }

    @Test
    @Order(9)
    @DisplayName("9. 推理结果搜索和过滤测试")
    void testInferenceSearchAndFilter() throws Exception {
        // 创建不同类型的推理记录
        InferenceHistory objectDetection = createTestInferenceHistory();
        objectDetection.setModelType(InferenceHistory.ModelType.OBJECT_DETECTION);
        objectDetection.setModelName("YOLOv5");
        
        InferenceHistory faceRecognition = createTestInferenceHistory();
        faceRecognition.setTaskId("FACE-TASK-001");
        faceRecognition.setModelType(InferenceHistory.ModelType.FACE_RECOGNITION);
        faceRecognition.setModelName("FaceNet");
        
        inferenceHistoryRepository.save(objectDetection);
        inferenceHistoryRepository.save(faceRecognition);

        // 按模型类型搜索
        mockMvc.perform(get("/api/inference/search")
                .header("Authorization", "Bearer " + userToken)
                .param("modelType", "OBJECT_DETECTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 按任务ID搜索
        mockMvc.perform(get("/api/inference/search")
                .header("Authorization", "Bearer " + userToken)
                .param("taskId", "FACE-TASK-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        // 按状态过滤
        mockMvc.perform(get("/api/inference/search")
                .header("Authorization", "Bearer " + userToken)
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(10)
    @DisplayName("10. 推理错误处理测试")
    void testInferenceErrorHandling() throws Exception {
        // 创建失败的推理任务
        InferenceHistory failedHistory = createTestInferenceHistory();
        failedHistory.setStatus(InferenceHistory.InferenceStatus.FAILED);
        failedHistory.setErrorMessage("模型加载失败");
        failedHistory = inferenceHistoryRepository.save(failedHistory);

        // 报告推理错误
        Map<String, Object> errorData = Map.of(
                "taskId", failedHistory.getTaskId(),
                "errorMessage", "GPU内存不足",
                "errorCode", "GPU_OOM_ERROR",
                "timestamp", System.currentTimeMillis()
        );

        mockMvc.perform(post("/api/inference/error")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(errorData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 获取错误统计
        mockMvc.perform(get("/api/inference/errors")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalErrors").exists())
                .andExpect(jsonPath("$.errorTypes").exists());
    }

    @Test
    @Order(11)
    @DisplayName("11. 推理配置管理测试")
    void testInferenceConfigurationManagement() throws Exception {
        // 获取当前推理配置
        mockMvc.perform(get("/api/inference/config")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxConcurrentTasks").exists())
                .andExpect(jsonPath("$.defaultConfidence").exists())
                .andExpect(jsonPath("$.gpuEnabled").exists());

        // 更新推理配置
        Map<String, Object> configData = Map.of(
                "maxConcurrentTasks", 10,
                "defaultConfidence", 0.75,
                "gpuEnabled", true,
                "batchSize", 32,
                "timeout", 30000
        );

        mockMvc.perform(put("/api/inference/config")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(configData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxConcurrentTasks").value(10))
                .andExpect(jsonPath("$.defaultConfidence").value(0.75));

        // 普通用户尝试更新配置应该被拒绝
        mockMvc.perform(put("/api/inference/config")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(configData)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(12)
    @DisplayName("12. 完整AI推理流程测试")
    void testCompleteInferenceWorkflow() throws Exception {
        // 1. 启动推理任务
        Map<String, Object> inferenceData = Map.of(
                "streamId", testStream.getId(),
                "modelType", "OBJECT_DETECTION",
                "modelName", "YOLOv5",
                "confidence", 0.8,
                "parameters", Map.of("classes", List.of("person", "vehicle"))
        );

        MvcResult startResult = mockMvc.perform(post("/api/inference/start")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inferenceData)))
                .andExpect(status().isOk())
                .andReturn();

        String startResponse = startResult.getResponse().getContentAsString();
        Map<String, Object> taskInfo = objectMapper.readValue(startResponse, Map.class);
        String taskId = (String) taskInfo.get("taskId");

        // 2. 模拟推理结果
        Map<String, Object> resultData = Map.of(
                "taskId", taskId,
                "timestamp", System.currentTimeMillis(),
                "detections", List.of(
                        Map.of("class", "person", "confidence", 0.92, "bbox", List.of(50, 100, 150, 250))
                ),
                "processingTime", 35.5
        );

        mockMvc.perform(post("/api/inference/results")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resultData)))
                .andExpect(status().isOk());

        // 3. 获取推理详情
        mockMvc.perform(get("/api/inference/task/" + taskId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.status").exists());

        // 4. 停止推理任务
        mockMvc.perform(post("/api/inference/" + taskId + "/stop")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STOPPED"));

        System.out.println("完整AI推理流程测试完成，任务ID: " + taskId);
    }

    private InferenceHistory createTestInferenceHistory() {
        InferenceHistory history = new InferenceHistory();
        history.setTaskId("TEST-TASK-" + System.currentTimeMillis());
        history.setVideoStream(testStream);
        history.setModelType(InferenceHistory.ModelType.OBJECT_DETECTION);
        history.setModelName("YOLOv5");
        history.setStatus(InferenceHistory.InferenceStatus.COMPLETED);
        history.setStartTime(LocalDateTime.now().minusMinutes(5));
        history.setEndTime(LocalDateTime.now());
        history.setProcessingTime(45.0);
        history.setAccuracy(0.89);
        history.setConfidence(0.8);
        history.setResultData("{\"detections\": [{\"class\": \"person\", \"confidence\": 0.95}]}");
        history.setCreatedAt(LocalDateTime.now());
        history.setUpdatedAt(LocalDateTime.now());
        return history;
    }
}