package com.vision.vision_platform_backend.dto;

import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.model.Device;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * VideoStreamDto 单元测试
 */
@DisplayName("VideoStreamDto 测试")
public class VideoStreamDtoTest {

    private Validator validator;
    private VideoStreamDto dto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        dto = new VideoStreamDto();
    }

    @Test
    @DisplayName("默认构造函数")
    void testDefaultConstructor() {
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getStreamId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getType());
        assertNull(dto.getStatus());
        assertNull(dto.getSourceUrl());
        assertNull(dto.getOutputUrl());
        assertNull(dto.getProtocol());
        assertNull(dto.getQuality());
        assertNull(dto.getWidth());
        assertNull(dto.getHeight());
        assertNull(dto.getFrameRate());
        assertNull(dto.getBitrate());
        assertNull(dto.getDeviceId());
        assertNull(dto.getDeviceName());
        assertNull(dto.getRecordingEnabled());
        assertNull(dto.getRecordingPath());
        assertNull(dto.getRecordingDuration());
        assertNull(dto.getTranscodeEnabled());
        assertNull(dto.getTranscodeFormat());
        assertNull(dto.getTranscodeQuality());
        assertNull(dto.getLastActiveTime());
        assertNull(dto.getViewerCount());
        assertNull(dto.getCpuUsage());
        assertNull(dto.getMemoryUsage());
        assertNull(dto.getNetworkBandwidth());
        assertNull(dto.getLastError());
        assertNull(dto.getLastErrorTime());
        assertNull(dto.getErrorCount());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getCreatedBy());
        assertNull(dto.getUpdatedBy());
    }

    @Test
    @DisplayName("从VideoStream实体构造")
    void testConstructorFromEntity() {
        // 创建设备
        Device device = new Device();
        device.setId(1L);
        device.setName("Camera-001");

        // 创建VideoStream实体
        VideoStream videoStream = new VideoStream();
        videoStream.setId(1L);
        videoStream.setStreamId("stream-001");
        videoStream.setName("Test Stream");
        videoStream.setDescription("Test Description");
        videoStream.setType(VideoStream.StreamType.LIVE);
        videoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        videoStream.setSourceUrl("rtmp://source.url");
        videoStream.setOutputUrl("http://output.url");
        videoStream.setProtocol(VideoStream.StreamProtocol.RTMP);
        videoStream.setQuality(VideoStream.StreamQuality.HD);
        videoStream.setWidth(1920);
        videoStream.setHeight(1080);
        videoStream.setFrameRate(30);
        videoStream.setBitrate(5000);
        videoStream.setDevice(device);
        videoStream.setRecordingEnabled(true);
        videoStream.setRecordingPath("/recordings");
        videoStream.setRecordingDuration(60);
        videoStream.setTranscodeEnabled(false);
        videoStream.setTranscodeFormat("mp4");
        videoStream.setTranscodeQuality("high");
        videoStream.setLastActiveTime(LocalDateTime.now());
        videoStream.setViewerCount(10L);
        videoStream.setCpuUsage(50.5);
        videoStream.setMemoryUsage(30.2);
        videoStream.setNetworkBandwidth(100.0);
        videoStream.setLastError("Test error");
        videoStream.setLastErrorTime(LocalDateTime.now());
        videoStream.setErrorCount(1);
        videoStream.setCreatedAt(LocalDateTime.now());
        videoStream.setUpdatedAt(LocalDateTime.now());
        videoStream.setCreatedBy("admin");
        videoStream.setUpdatedBy("admin");

        // 从实体构造DTO
        VideoStreamDto dto = new VideoStreamDto(videoStream);

        assertEquals(1L, dto.getId());
        assertEquals("stream-001", dto.getStreamId());
        assertEquals("Test Stream", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(VideoStream.StreamType.LIVE, dto.getType());
        assertEquals(VideoStream.StreamStatus.ACTIVE, dto.getStatus());
        assertEquals("rtmp://source.url", dto.getSourceUrl());
        assertEquals("http://output.url", dto.getOutputUrl());
        assertEquals(VideoStream.StreamProtocol.RTMP, dto.getProtocol());
        assertEquals(VideoStream.StreamQuality.HD, dto.getQuality());
        assertEquals(1920, dto.getWidth());
        assertEquals(1080, dto.getHeight());
        assertEquals(30, dto.getFrameRate());
        assertEquals(5000, dto.getBitrate());
        assertEquals(1L, dto.getDeviceId());
        assertEquals("Camera-001", dto.getDeviceName());
        assertTrue(dto.getRecordingEnabled());
        assertEquals("/recordings", dto.getRecordingPath());
        assertEquals(60, dto.getRecordingDuration());
        assertFalse(dto.getTranscodeEnabled());
        assertEquals("mp4", dto.getTranscodeFormat());
        assertEquals("high", dto.getTranscodeQuality());
        assertNotNull(dto.getLastActiveTime());
        assertEquals(10L, dto.getViewerCount());
        assertEquals(50.5, dto.getCpuUsage());
        assertEquals(30.2, dto.getMemoryUsage());
        assertEquals(100.0, dto.getNetworkBandwidth());
        assertEquals("Test error", dto.getLastError());
        assertNotNull(dto.getLastErrorTime());
        assertEquals(1, dto.getErrorCount());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertEquals("admin", dto.getCreatedBy());
        assertEquals("admin", dto.getUpdatedBy());
    }

    @Test
    @DisplayName("转换为实体对象")
    void testToEntity() {
        dto.setId(1L);
        dto.setStreamId("stream-002");
        dto.setName("Test Stream 2");
        dto.setDescription("Test Description 2");
        dto.setType(VideoStream.StreamType.RECORDED);
        dto.setStatus(VideoStream.StreamStatus.INACTIVE);
        dto.setSourceUrl("http://source2.url");
        dto.setOutputUrl("http://output2.url");
        dto.setProtocol(VideoStream.StreamProtocol.HTTP);
        dto.setQuality(VideoStream.StreamQuality.FHD);
        dto.setWidth(1920);
        dto.setHeight(1080);
        dto.setFrameRate(25);
        dto.setBitrate(3000);
        dto.setRecordingEnabled(false);
        dto.setRecordingPath("/recordings2");
        dto.setRecordingDuration(120);
        dto.setTranscodeEnabled(true);
        dto.setTranscodeFormat("avi");
        dto.setTranscodeQuality("medium");
        dto.setViewerCount(5L);
        dto.setErrorCount(2);

        VideoStream entity = dto.toEntity();

        assertEquals(1L, entity.getId());
        assertEquals("stream-002", entity.getStreamId());
        assertEquals("Test Stream 2", entity.getName());
        assertEquals("Test Description 2", entity.getDescription());
        assertEquals(VideoStream.StreamType.RECORDED, entity.getType());
        assertEquals(VideoStream.StreamStatus.INACTIVE, entity.getStatus());
        assertEquals("http://source2.url", entity.getSourceUrl());
        assertEquals("http://output2.url", entity.getOutputUrl());
        assertEquals(VideoStream.StreamProtocol.HTTP, entity.getProtocol());
        assertEquals(VideoStream.StreamQuality.FHD, entity.getQuality());
        assertEquals(1920, entity.getWidth());
        assertEquals(1080, entity.getHeight());
        assertEquals(25, entity.getFrameRate());
        assertEquals(3000, entity.getBitrate());
        assertFalse(entity.getRecordingEnabled());
        assertEquals("/recordings2", entity.getRecordingPath());
        assertEquals(120, entity.getRecordingDuration());
        assertTrue(entity.getTranscodeEnabled());
        assertEquals("avi", entity.getTranscodeFormat());
        assertEquals("medium", entity.getTranscodeQuality());
        assertEquals(5L, entity.getViewerCount());
        assertEquals(2, entity.getErrorCount());
    }

    @Test
    @DisplayName("转换为实体对象 - 默认值")
    void testToEntityWithDefaults() {
        dto.setStreamId("stream-003");
        dto.setName("Test Stream 3");
        dto.setType(VideoStream.StreamType.LIVE);
        dto.setSourceUrl("rtmp://source3.url");

        VideoStream entity = dto.toEntity();

        assertEquals(VideoStream.StreamStatus.INACTIVE, entity.getStatus());
        assertFalse(entity.getRecordingEnabled());
        assertFalse(entity.getTranscodeEnabled());
        assertEquals(0L, entity.getViewerCount());
        assertEquals(0, entity.getErrorCount());
    }

    @Nested
    @DisplayName("便捷方法测试")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("isActive方法")
        void testIsActive() {
            dto.setStatus(VideoStream.StreamStatus.ACTIVE);
            assertTrue(dto.isActive());

            dto.setStatus(VideoStream.StreamStatus.INACTIVE);
            assertFalse(dto.isActive());

            dto.setStatus(VideoStream.StreamStatus.ERROR);
            assertFalse(dto.isActive());
        }

        @Test
        @DisplayName("hasError方法")
        void testHasError() {
            dto.setStatus(VideoStream.StreamStatus.ERROR);
            assertTrue(dto.hasError());

            dto.setStatus(VideoStream.StreamStatus.ACTIVE);
            assertFalse(dto.hasError());

            dto.setStatus(VideoStream.StreamStatus.INACTIVE);
            assertFalse(dto.hasError());
        }

        @Test
        @DisplayName("getResolution方法")
        void testGetResolution() {
            dto.setWidth(1920);
            dto.setHeight(1080);
            assertEquals("1920x1080", dto.getResolution());

            dto.setWidth(null);
            assertNull(dto.getResolution());

            dto.setWidth(1280);
            dto.setHeight(null);
            assertNull(dto.getResolution());
        }

        @Test
        @DisplayName("getStreamInfo方法")
        void testGetStreamInfo() {
            dto.setName("Test Stream");
            dto.setWidth(1920);
            dto.setHeight(1080);
            dto.setFrameRate(30);

            String info = dto.getStreamInfo();
            assertEquals("Test Stream (1920x1080) @30fps", info);

            dto.setFrameRate(null);
            info = dto.getStreamInfo();
            assertEquals("Test Stream (1920x1080)", info);

            dto.setWidth(null);
            info = dto.getStreamInfo();
            assertEquals("Test Stream", info);
        }
    }

    @Nested
    @DisplayName("数据校验测试")
    class ValidationTest {

        @Test
        @DisplayName("有效的VideoStreamDto")
        void testValidVideoStreamDto() {
            dto.setStreamId("stream-001");
            dto.setName("Valid Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://valid.source.url");
            dto.setWidth(1920);
            dto.setHeight(1080);
            dto.setFrameRate(30);
            dto.setBitrate(5000);

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("流ID为空")
        void testStreamIdBlank() {
            dto.setStreamId("");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("流ID不能为空")));
        }

        @Test
        @DisplayName("流ID过长")
        void testStreamIdTooLong() {
            dto.setStreamId("a".repeat(51));
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("流ID长度不能超过50个字符")));
        }

        @Test
        @DisplayName("流名称为空")
        void testNameBlank() {
            dto.setStreamId("stream-001");
            dto.setName("");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("流名称不能为空")));
        }

        @Test
        @DisplayName("流名称过长")
        void testNameTooLong() {
            dto.setStreamId("stream-001");
            dto.setName("a".repeat(101));
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("流名称长度不能超过100个字符")));
        }

        @Test
        @DisplayName("描述过长")
        void testDescriptionTooLong() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setDescription("a".repeat(501));
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("描述长度不能超过500个字符")));
        }

        @Test
        @DisplayName("流类型为空")
        void testTypeNull() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(null);
            dto.setSourceUrl("rtmp://source.url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("流类型不能为空")));
        }

        @Test
        @DisplayName("源URL为空")
        void testSourceUrlBlank() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("源URL不能为空")));
        }

        @Test
        @DisplayName("源URL过长")
        void testSourceUrlTooLong() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://" + "a".repeat(500) + ".url");

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("源URL长度不能超过500个字符")));
        }

        @Test
        @DisplayName("宽度超出范围")
        void testWidthOutOfRange() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");
            dto.setWidth(0);

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("宽度必须大于0")));

            dto.setWidth(8000);
            violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("宽度不能超过7680")));
        }

        @Test
        @DisplayName("高度超出范围")
        void testHeightOutOfRange() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");
            dto.setHeight(0);

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("高度必须大于0")));

            dto.setHeight(5000);
            violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("高度不能超过4320")));
        }

        @Test
        @DisplayName("帧率超出范围")
        void testFrameRateOutOfRange() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");
            dto.setFrameRate(0);

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("帧率必须大于0")));

            dto.setFrameRate(150);
            violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("帧率不能超过120")));
        }

        @Test
        @DisplayName("比特率无效")
        void testBitrateInvalid() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");
            dto.setBitrate(0);

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("比特率必须大于0")));
        }

        @Test
        @DisplayName("录制时长超出范围")
        void testRecordingDurationOutOfRange() {
            dto.setStreamId("stream-001");
            dto.setName("Test Stream");
            dto.setType(VideoStream.StreamType.LIVE);
            dto.setSourceUrl("rtmp://source.url");
            dto.setRecordingDuration(0);

            Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("录制时长必须大于0分钟")));

            dto.setRecordingDuration(1500);
            violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("录制时长不能超过1440分钟")));
        }
    }

    @Test
    @DisplayName("Setter和Getter方法")
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        dto.setId(1L);
        dto.setStreamId("stream-001");
        dto.setName("Test Stream");
        dto.setDescription("Test Description");
        dto.setType(VideoStream.StreamType.LIVE);
        dto.setStatus(VideoStream.StreamStatus.ACTIVE);
        dto.setSourceUrl("rtmp://source.url");
        dto.setOutputUrl("http://output.url");
        dto.setProtocol(VideoStream.StreamProtocol.RTMP);
        dto.setQuality(VideoStream.StreamQuality.HD);
        dto.setWidth(1920);
        dto.setHeight(1080);
        dto.setFrameRate(30);
        dto.setBitrate(5000);
        dto.setDeviceId(1L);
        dto.setDeviceName("Camera-001");
        dto.setRecordingEnabled(true);
        dto.setRecordingPath("/recordings");
        dto.setRecordingDuration(60);
        dto.setTranscodeEnabled(false);
        dto.setTranscodeFormat("mp4");
        dto.setTranscodeQuality("high");
        dto.setLastActiveTime(now);
        dto.setViewerCount(10L);
        dto.setCpuUsage(50.5);
        dto.setMemoryUsage(30.2);
        dto.setNetworkBandwidth(100.0);
        dto.setLastError("Test error");
        dto.setLastErrorTime(now);
        dto.setErrorCount(1);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setCreatedBy("admin");
        dto.setUpdatedBy("admin");

        assertEquals(1L, dto.getId());
        assertEquals("stream-001", dto.getStreamId());
        assertEquals("Test Stream", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(VideoStream.StreamType.LIVE, dto.getType());
        assertEquals(VideoStream.StreamStatus.ACTIVE, dto.getStatus());
        assertEquals("rtmp://source.url", dto.getSourceUrl());
        assertEquals("http://output.url", dto.getOutputUrl());
        assertEquals(VideoStream.StreamProtocol.RTMP, dto.getProtocol());
        assertEquals(VideoStream.StreamQuality.HD, dto.getQuality());
        assertEquals(1920, dto.getWidth());
        assertEquals(1080, dto.getHeight());
        assertEquals(30, dto.getFrameRate());
        assertEquals(5000, dto.getBitrate());
        assertEquals(1L, dto.getDeviceId());
        assertEquals("Camera-001", dto.getDeviceName());
        assertTrue(dto.getRecordingEnabled());
        assertEquals("/recordings", dto.getRecordingPath());
        assertEquals(60, dto.getRecordingDuration());
        assertFalse(dto.getTranscodeEnabled());
        assertEquals("mp4", dto.getTranscodeFormat());
        assertEquals("high", dto.getTranscodeQuality());
        assertEquals(now, dto.getLastActiveTime());
        assertEquals(10L, dto.getViewerCount());
        assertEquals(50.5, dto.getCpuUsage());
        assertEquals(30.2, dto.getMemoryUsage());
        assertEquals(100.0, dto.getNetworkBandwidth());
        assertEquals("Test error", dto.getLastError());
        assertEquals(now, dto.getLastErrorTime());
        assertEquals(1, dto.getErrorCount());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertEquals("admin", dto.getCreatedBy());
        assertEquals("admin", dto.getUpdatedBy());
    }

    @Test
    @DisplayName("完整VideoStreamDto场景")
    void testCompleteVideoStreamDtoScenario() {
        // 创建完整的VideoStreamDto
        dto.setStreamId("stream-complete");
        dto.setName("Complete Test Stream");
        dto.setDescription("Complete test description");
        dto.setType(VideoStream.StreamType.LIVE);
        dto.setStatus(VideoStream.StreamStatus.ACTIVE);
        dto.setSourceUrl("rtmp://complete.source.url");
        dto.setOutputUrl("http://complete.output.url");
        dto.setProtocol(VideoStream.StreamProtocol.RTMP);
        dto.setQuality(VideoStream.StreamQuality.FHD);
        dto.setWidth(1920);
        dto.setHeight(1080);
        dto.setFrameRate(60);
        dto.setBitrate(8000);
        dto.setDeviceId(1L);
        dto.setDeviceName("Complete Camera");
        dto.setRecordingEnabled(true);
        dto.setRecordingPath("/complete/recordings");
        dto.setRecordingDuration(120);
        dto.setTranscodeEnabled(true);
        dto.setTranscodeFormat("mp4");
        dto.setTranscodeQuality("ultra");

        // 验证数据有效性
        Set<ConstraintViolation<VideoStreamDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        // 验证便捷方法
        assertTrue(dto.isActive());
        assertFalse(dto.hasError());
        assertEquals("1920x1080", dto.getResolution());
        assertEquals("Complete Test Stream (1920x1080) @60fps", dto.getStreamInfo());

        // 转换为实体并验证
        VideoStream entity = dto.toEntity();
        assertNotNull(entity);
        assertEquals("stream-complete", entity.getStreamId());
        assertEquals("Complete Test Stream", entity.getName());
        assertEquals(VideoStream.StreamType.LIVE, entity.getType());
        assertEquals(VideoStream.StreamStatus.ACTIVE, entity.getStatus());
        assertTrue(entity.getRecordingEnabled());
        assertTrue(entity.getTranscodeEnabled());
    }
}