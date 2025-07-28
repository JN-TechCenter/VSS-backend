package com.vision.vision_platform_backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VideoStream模型类单元测试
 */
@ExtendWith(MockitoExtension.class)
class VideoStreamTest {

    private VideoStream videoStream;

    @BeforeEach
    void setUp() {
        videoStream = new VideoStream();
    }

    @Test
    void testDefaultConstructor() {
        // When
        VideoStream stream = new VideoStream();

        // Then
        assertNotNull(stream);
        assertNull(stream.getId());
        assertNull(stream.getStreamId());
        assertNull(stream.getName());
        assertNull(stream.getType());
        assertNull(stream.getStatus());
        assertNull(stream.getViewerCount());
        assertNull(stream.getErrorCount());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Long id = 1L;
        String streamId = "STREAM-001";
        String name = "Main Camera Stream";
        String description = "Primary security camera stream";
        VideoStream.StreamType type = VideoStream.StreamType.RTSP;
        VideoStream.StreamStatus status = VideoStream.StreamStatus.ACTIVE;
        String sourceUrl = "rtsp://192.168.1.100:554/stream";
        String outputUrl = "http://localhost:8080/stream/output";
        VideoStream.StreamProtocol protocol = VideoStream.StreamProtocol.TCP;
        VideoStream.StreamQuality quality = VideoStream.StreamQuality.HIGH;
        Integer width = 1920;
        Integer height = 1080;
        Integer frameRate = 30;
        Integer bitrate = 5000;
        Device device = new Device();
        Boolean recordingEnabled = true;
        String recordingPath = "/recordings/stream001";
        Integer recordingDuration = 60;
        Boolean transcodeEnabled = true;
        String transcodeFormat = "H.264";
        String transcodeQuality = "high";
        LocalDateTime lastActiveTime = LocalDateTime.now();
        Long viewerCount = 5L;
        Double cpuUsage = 45.5;
        Double memoryUsage = 512.0;
        Double networkBandwidth = 100.0;
        String lastError = "Connection timeout";
        LocalDateTime lastErrorTime = LocalDateTime.now();
        Integer errorCount = 3;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String createdBy = "admin";
        String updatedBy = "operator";

        // When
        videoStream.setId(id);
        videoStream.setStreamId(streamId);
        videoStream.setName(name);
        videoStream.setDescription(description);
        videoStream.setType(type);
        videoStream.setStatus(status);
        videoStream.setSourceUrl(sourceUrl);
        videoStream.setOutputUrl(outputUrl);
        videoStream.setProtocol(protocol);
        videoStream.setQuality(quality);
        videoStream.setWidth(width);
        videoStream.setHeight(height);
        videoStream.setFrameRate(frameRate);
        videoStream.setBitrate(bitrate);
        videoStream.setDevice(device);
        videoStream.setRecordingEnabled(recordingEnabled);
        videoStream.setRecordingPath(recordingPath);
        videoStream.setRecordingDuration(recordingDuration);
        videoStream.setTranscodeEnabled(transcodeEnabled);
        videoStream.setTranscodeFormat(transcodeFormat);
        videoStream.setTranscodeQuality(transcodeQuality);
        videoStream.setLastActiveTime(lastActiveTime);
        videoStream.setViewerCount(viewerCount);
        videoStream.setCpuUsage(cpuUsage);
        videoStream.setMemoryUsage(memoryUsage);
        videoStream.setNetworkBandwidth(networkBandwidth);
        videoStream.setLastError(lastError);
        videoStream.setLastErrorTime(lastErrorTime);
        videoStream.setErrorCount(errorCount);
        videoStream.setCreatedAt(createdAt);
        videoStream.setUpdatedAt(updatedAt);
        videoStream.setCreatedBy(createdBy);
        videoStream.setUpdatedBy(updatedBy);

        // Then
        assertEquals(id, videoStream.getId());
        assertEquals(streamId, videoStream.getStreamId());
        assertEquals(name, videoStream.getName());
        assertEquals(description, videoStream.getDescription());
        assertEquals(type, videoStream.getType());
        assertEquals(status, videoStream.getStatus());
        assertEquals(sourceUrl, videoStream.getSourceUrl());
        assertEquals(outputUrl, videoStream.getOutputUrl());
        assertEquals(protocol, videoStream.getProtocol());
        assertEquals(quality, videoStream.getQuality());
        assertEquals(width, videoStream.getWidth());
        assertEquals(height, videoStream.getHeight());
        assertEquals(frameRate, videoStream.getFrameRate());
        assertEquals(bitrate, videoStream.getBitrate());
        assertEquals(device, videoStream.getDevice());
        assertEquals(recordingEnabled, videoStream.getRecordingEnabled());
        assertEquals(recordingPath, videoStream.getRecordingPath());
        assertEquals(recordingDuration, videoStream.getRecordingDuration());
        assertEquals(transcodeEnabled, videoStream.getTranscodeEnabled());
        assertEquals(transcodeFormat, videoStream.getTranscodeFormat());
        assertEquals(transcodeQuality, videoStream.getTranscodeQuality());
        assertEquals(lastActiveTime, videoStream.getLastActiveTime());
        assertEquals(viewerCount, videoStream.getViewerCount());
        assertEquals(cpuUsage, videoStream.getCpuUsage());
        assertEquals(memoryUsage, videoStream.getMemoryUsage());
        assertEquals(networkBandwidth, videoStream.getNetworkBandwidth());
        assertEquals(lastError, videoStream.getLastError());
        assertEquals(lastErrorTime, videoStream.getLastErrorTime());
        assertEquals(errorCount, videoStream.getErrorCount());
        assertEquals(createdAt, videoStream.getCreatedAt());
        assertEquals(updatedAt, videoStream.getUpdatedAt());
        assertEquals(createdBy, videoStream.getCreatedBy());
        assertEquals(updatedBy, videoStream.getUpdatedBy());
    }

    @Test
    void testIsActive() {
        // Test when stream is active
        videoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        assertTrue(videoStream.isActive());

        // Test when stream is inactive
        videoStream.setStatus(VideoStream.StreamStatus.INACTIVE);
        assertFalse(videoStream.isActive());

        // Test when stream is in error state
        videoStream.setStatus(VideoStream.StreamStatus.ERROR);
        assertFalse(videoStream.isActive());
    }

    @Test
    void testIsInactive() {
        // Test when stream is inactive
        videoStream.setStatus(VideoStream.StreamStatus.INACTIVE);
        assertTrue(videoStream.isInactive());

        // Test when stream is active
        videoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        assertFalse(videoStream.isInactive());

        // Test when stream is starting
        videoStream.setStatus(VideoStream.StreamStatus.STARTING);
        assertFalse(videoStream.isInactive());
    }

    @Test
    void testHasError() {
        // Test when stream has error
        videoStream.setStatus(VideoStream.StreamStatus.ERROR);
        assertTrue(videoStream.hasError());

        // Test when stream is active
        videoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        assertFalse(videoStream.hasError());

        // Test when stream is inactive
        videoStream.setStatus(VideoStream.StreamStatus.INACTIVE);
        assertFalse(videoStream.hasError());
    }

    @Test
    void testUpdateLastActiveTime() {
        // Given
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // When
        videoStream.updateLastActiveTime();
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertNotNull(videoStream.getLastActiveTime());
        assertTrue(videoStream.getLastActiveTime().isAfter(beforeUpdate));
        assertTrue(videoStream.getLastActiveTime().isBefore(afterUpdate));
    }

    @Test
    void testIncrementViewerCount_FromNull() {
        // Given
        videoStream.setViewerCount(null);

        // When
        videoStream.incrementViewerCount();

        // Then
        assertEquals(1L, videoStream.getViewerCount());
    }

    @Test
    void testIncrementViewerCount_FromExistingValue() {
        // Given
        videoStream.setViewerCount(5L);

        // When
        videoStream.incrementViewerCount();

        // Then
        assertEquals(6L, videoStream.getViewerCount());
    }

    @Test
    void testDecrementViewerCount_FromNull() {
        // Given
        videoStream.setViewerCount(null);

        // When
        videoStream.decrementViewerCount();

        // Then
        assertEquals(0L, videoStream.getViewerCount());
    }

    @Test
    void testDecrementViewerCount_FromExistingValue() {
        // Given
        videoStream.setViewerCount(5L);

        // When
        videoStream.decrementViewerCount();

        // Then
        assertEquals(4L, videoStream.getViewerCount());
    }

    @Test
    void testDecrementViewerCount_ToZero() {
        // Given
        videoStream.setViewerCount(1L);

        // When
        videoStream.decrementViewerCount();

        // Then
        assertEquals(0L, videoStream.getViewerCount());
    }

    @Test
    void testDecrementViewerCount_BelowZero() {
        // Given
        videoStream.setViewerCount(0L);

        // When
        videoStream.decrementViewerCount();

        // Then
        assertEquals(0L, videoStream.getViewerCount()); // Should not go below 0
    }

    @Test
    void testRecordError() {
        // Given
        String errorMessage = "Network connection failed";
        LocalDateTime beforeError = LocalDateTime.now().minusSeconds(1);
        videoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        videoStream.setErrorCount(2);

        // When
        videoStream.recordError(errorMessage);
        LocalDateTime afterError = LocalDateTime.now().plusSeconds(1);

        // Then
        assertEquals(errorMessage, videoStream.getLastError());
        assertNotNull(videoStream.getLastErrorTime());
        assertTrue(videoStream.getLastErrorTime().isAfter(beforeError));
        assertTrue(videoStream.getLastErrorTime().isBefore(afterError));
        assertEquals(3, videoStream.getErrorCount());
        assertEquals(VideoStream.StreamStatus.ERROR, videoStream.getStatus());
    }

    @Test
    void testRecordError_FromNullErrorCount() {
        // Given
        String errorMessage = "First error";
        videoStream.setErrorCount(null);

        // When
        videoStream.recordError(errorMessage);

        // Then
        assertEquals(errorMessage, videoStream.getLastError());
        assertEquals(1, videoStream.getErrorCount());
        assertEquals(VideoStream.StreamStatus.ERROR, videoStream.getStatus());
    }

    @Test
    void testClearError() {
        // Given
        videoStream.setLastError("Previous error");
        videoStream.setLastErrorTime(LocalDateTime.now());
        videoStream.setErrorCount(5);

        // When
        videoStream.clearError();

        // Then
        assertNull(videoStream.getLastError());
        assertNull(videoStream.getLastErrorTime());
        assertEquals(0, videoStream.getErrorCount());
    }

    @Test
    void testStreamTypeEnum() {
        // Test all stream types
        VideoStream.StreamType[] types = VideoStream.StreamType.values();
        assertEquals(6, types.length);
        
        assertTrue(java.util.Arrays.asList(types).contains(VideoStream.StreamType.RTSP));
        assertTrue(java.util.Arrays.asList(types).contains(VideoStream.StreamType.RTMP));
        assertTrue(java.util.Arrays.asList(types).contains(VideoStream.StreamType.HTTP));
        assertTrue(java.util.Arrays.asList(types).contains(VideoStream.StreamType.HLS));
        assertTrue(java.util.Arrays.asList(types).contains(VideoStream.StreamType.WEBRTC));
        assertTrue(java.util.Arrays.asList(types).contains(VideoStream.StreamType.FILE));
    }

    @Test
    void testStreamStatusEnum() {
        // Test all stream statuses
        VideoStream.StreamStatus[] statuses = VideoStream.StreamStatus.values();
        assertEquals(6, statuses.length);
        
        assertTrue(java.util.Arrays.asList(statuses).contains(VideoStream.StreamStatus.INACTIVE));
        assertTrue(java.util.Arrays.asList(statuses).contains(VideoStream.StreamStatus.STARTING));
        assertTrue(java.util.Arrays.asList(statuses).contains(VideoStream.StreamStatus.ACTIVE));
        assertTrue(java.util.Arrays.asList(statuses).contains(VideoStream.StreamStatus.STOPPING));
        assertTrue(java.util.Arrays.asList(statuses).contains(VideoStream.StreamStatus.ERROR));
        assertTrue(java.util.Arrays.asList(statuses).contains(VideoStream.StreamStatus.MAINTENANCE));
    }

    @Test
    void testStreamProtocolEnum() {
        // Test all stream protocols
        VideoStream.StreamProtocol[] protocols = VideoStream.StreamProtocol.values();
        assertEquals(5, protocols.length);
        
        assertTrue(java.util.Arrays.asList(protocols).contains(VideoStream.StreamProtocol.TCP));
        assertTrue(java.util.Arrays.asList(protocols).contains(VideoStream.StreamProtocol.UDP));
        assertTrue(java.util.Arrays.asList(protocols).contains(VideoStream.StreamProtocol.HTTP));
        assertTrue(java.util.Arrays.asList(protocols).contains(VideoStream.StreamProtocol.HTTPS));
        assertTrue(java.util.Arrays.asList(protocols).contains(VideoStream.StreamProtocol.WEBSOCKET));
    }

    @Test
    void testStreamQualityEnum() {
        // Test all stream qualities
        VideoStream.StreamQuality[] qualities = VideoStream.StreamQuality.values();
        assertEquals(5, qualities.length);
        
        assertTrue(java.util.Arrays.asList(qualities).contains(VideoStream.StreamQuality.LOW));
        assertTrue(java.util.Arrays.asList(qualities).contains(VideoStream.StreamQuality.MEDIUM));
        assertTrue(java.util.Arrays.asList(qualities).contains(VideoStream.StreamQuality.HIGH));
        assertTrue(java.util.Arrays.asList(qualities).contains(VideoStream.StreamQuality.ULTRA));
        assertTrue(java.util.Arrays.asList(qualities).contains(VideoStream.StreamQuality.AUTO));
    }

    @Test
    void testCompleteVideoStreamLifecycle() {
        // Given - Create a new video stream
        VideoStream stream = new VideoStream();
        stream.setStreamId("STREAM-LIFECYCLE");
        stream.setName("Lifecycle Test Stream");
        stream.setType(VideoStream.StreamType.RTSP);
        stream.setSourceUrl("rtsp://test.example.com/stream");
        
        // Initially inactive
        stream.setStatus(VideoStream.StreamStatus.INACTIVE);
        assertTrue(stream.isInactive());
        assertFalse(stream.isActive());
        assertFalse(stream.hasError());
        
        // Stream becomes active
        stream.setStatus(VideoStream.StreamStatus.ACTIVE);
        stream.updateLastActiveTime();
        assertTrue(stream.isActive());
        assertFalse(stream.isInactive());
        assertNotNull(stream.getLastActiveTime());
        
        // Viewers join
        stream.incrementViewerCount(); // 1
        stream.incrementViewerCount(); // 2
        stream.incrementViewerCount(); // 3
        assertEquals(3L, stream.getViewerCount());
        
        // One viewer leaves
        stream.decrementViewerCount(); // 2
        assertEquals(2L, stream.getViewerCount());
        
        // Stream encounters error
        stream.recordError("Connection lost");
        assertTrue(stream.hasError());
        assertEquals(VideoStream.StreamStatus.ERROR, stream.getStatus());
        assertEquals("Connection lost", stream.getLastError());
        assertEquals(1, stream.getErrorCount());
        
        // Error is cleared and stream recovers
        stream.clearError();
        stream.setStatus(VideoStream.StreamStatus.ACTIVE);
        assertNull(stream.getLastError());
        assertEquals(0, stream.getErrorCount());
        assertTrue(stream.isActive());
        
        // Stream is stopped
        stream.setStatus(VideoStream.StreamStatus.INACTIVE);
        assertTrue(stream.isInactive());
        assertFalse(stream.isActive());
    }

    @Test
    void testDeviceAssociation() {
        // Given
        Device device = new Device();
        device.setDeviceId("CAM-001");
        device.setName("Test Camera");

        // When
        videoStream.setDevice(device);

        // Then
        assertEquals(device, videoStream.getDevice());
        assertEquals("CAM-001", videoStream.getDevice().getDeviceId());
        assertEquals("Test Camera", videoStream.getDevice().getName());
    }

    @Test
    void testRecordingConfiguration() {
        // Given
        videoStream.setRecordingEnabled(true);
        videoStream.setRecordingPath("/var/recordings/stream001");
        videoStream.setRecordingDuration(120); // 2 hours

        // Then
        assertTrue(videoStream.getRecordingEnabled());
        assertEquals("/var/recordings/stream001", videoStream.getRecordingPath());
        assertEquals(120, videoStream.getRecordingDuration());
    }

    @Test
    void testTranscodingConfiguration() {
        // Given
        videoStream.setTranscodeEnabled(true);
        videoStream.setTranscodeFormat("H.265");
        videoStream.setTranscodeQuality("ultra");

        // Then
        assertTrue(videoStream.getTranscodeEnabled());
        assertEquals("H.265", videoStream.getTranscodeFormat());
        assertEquals("ultra", videoStream.getTranscodeQuality());
    }

    @Test
    void testMonitoringMetrics() {
        // Given
        videoStream.setCpuUsage(75.5);
        videoStream.setMemoryUsage(1024.0);
        videoStream.setNetworkBandwidth(250.0);

        // Then
        assertEquals(75.5, videoStream.getCpuUsage());
        assertEquals(1024.0, videoStream.getMemoryUsage());
        assertEquals(250.0, videoStream.getNetworkBandwidth());
    }
}