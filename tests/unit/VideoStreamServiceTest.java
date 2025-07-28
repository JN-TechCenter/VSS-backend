package com.vision.vision_platform_backend.service;

import com.vision.vision_platform_backend.dto.VideoStreamDto;
import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.model.Device;
import com.vision.vision_platform_backend.repository.VideoStreamRepository;
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
class VideoStreamServiceTest {

    @Mock
    private VideoStreamRepository videoStreamRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private VideoStreamService videoStreamService;

    private VideoStream testVideoStream;
    private VideoStreamDto testVideoStreamDto;
    private Device testDevice;

    @BeforeEach
    void setUp() {
        // 创建测试设备
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setDeviceId("device-001");
        testDevice.setName("Test Camera");
        testDevice.setType(Device.DeviceType.CAMERA);
        testDevice.setStatus(Device.DeviceStatus.ONLINE);

        // 创建测试视频流
        testVideoStream = new VideoStream();
        testVideoStream.setId(1L);
        testVideoStream.setStreamId("stream-001");
        testVideoStream.setName("Test Stream");
        testVideoStream.setDescription("测试视频流");
        testVideoStream.setType(VideoStream.StreamType.LIVE);
        testVideoStream.setSourceUrl("rtmp://example.com/live/stream");
        testVideoStream.setOutputUrl("http://example.com/hls/stream.m3u8");
        testVideoStream.setProtocol("RTMP");
        testVideoStream.setQuality("HD");
        testVideoStream.setWidth(1920);
        testVideoStream.setHeight(1080);
        testVideoStream.setFrameRate(30.0);
        testVideoStream.setBitrate(2000);
        testVideoStream.setStatus(VideoStream.StreamStatus.INACTIVE);
        testVideoStream.setViewerCount(0L);
        testVideoStream.setErrorCount(0);
        testVideoStream.setDevice(testDevice);
        testVideoStream.setRecordingEnabled(true);
        testVideoStream.setRecordingPath("/recordings/");
        testVideoStream.setRecordingDuration(3600);
        testVideoStream.setTranscodeEnabled(false);
        testVideoStream.setCreatedAt(LocalDateTime.now());
        testVideoStream.setUpdatedAt(LocalDateTime.now());

        // 创建测试DTO
        testVideoStreamDto = new VideoStreamDto();
        testVideoStreamDto.setStreamId("stream-001");
        testVideoStreamDto.setName("Test Stream");
        testVideoStreamDto.setDescription("测试视频流");
        testVideoStreamDto.setType(VideoStream.StreamType.LIVE);
        testVideoStreamDto.setSourceUrl("rtmp://example.com/live/stream");
        testVideoStreamDto.setOutputUrl("http://example.com/hls/stream.m3u8");
        testVideoStreamDto.setProtocol("RTMP");
        testVideoStreamDto.setQuality("HD");
        testVideoStreamDto.setWidth(1920);
        testVideoStreamDto.setHeight(1080);
        testVideoStreamDto.setFrameRate(30.0);
        testVideoStreamDto.setBitrate(2000);
        testVideoStreamDto.setDeviceId(1L);
        testVideoStreamDto.setRecordingEnabled(true);
        testVideoStreamDto.setRecordingPath("/recordings/");
        testVideoStreamDto.setRecordingDuration(3600);
        testVideoStreamDto.setTranscodeEnabled(false);
    }

    @Test
    void testCreateVideoStream_Success() {
        // Given
        when(videoStreamRepository.existsByStreamId("stream-001")).thenReturn(false);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        VideoStreamDto result = videoStreamService.createVideoStream(testVideoStreamDto);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).existsByStreamId("stream-001");
        verify(deviceRepository).findById(1L);
        verify(videoStreamRepository).save(any(VideoStream.class));
    }

    @Test
    void testCreateVideoStream_StreamIdExists() {
        // Given
        when(videoStreamRepository.existsByStreamId("stream-001")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> videoStreamService.createVideoStream(testVideoStreamDto));
        assertEquals("流ID已存在: stream-001", exception.getMessage());
    }

    @Test
    void testCreateVideoStream_DeviceNotFound() {
        // Given
        when(videoStreamRepository.existsByStreamId("stream-001")).thenReturn(false);
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> videoStreamService.createVideoStream(testVideoStreamDto));
        assertEquals("设备不存在: 1", exception.getMessage());
    }

    @Test
    void testUpdateVideoStream_Success() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.existsByStreamId("stream-001")).thenReturn(false);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        VideoStreamDto result = videoStreamService.updateVideoStream(streamId, testVideoStreamDto);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).findById(streamId);
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testUpdateVideoStream_StreamNotFound() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> videoStreamService.updateVideoStream(streamId, testVideoStreamDto));
        assertEquals("视频流不存在: 1", exception.getMessage());
    }

    @Test
    void testDeleteVideoStream_Success() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));

        // When
        videoStreamService.deleteVideoStream(streamId);

        // Then
        verify(videoStreamRepository).findById(streamId);
        verify(videoStreamRepository).delete(testVideoStream);
    }

    @Test
    void testDeleteVideoStream_StreamNotFound() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> videoStreamService.deleteVideoStream(streamId));
        assertEquals("视频流不存在: 1", exception.getMessage());
    }

    @Test
    void testGetVideoStream_Success() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));

        // When
        VideoStreamDto result = videoStreamService.getVideoStream(streamId);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).findById(streamId);
    }

    @Test
    void testGetVideoStreamByStreamId_Success() {
        // Given
        String streamId = "stream-001";
        when(videoStreamRepository.findByStreamId(streamId)).thenReturn(Optional.of(testVideoStream));

        // When
        VideoStreamDto result = videoStreamService.getVideoStreamByStreamId(streamId);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).findByStreamId(streamId);
    }

    @Test
    void testGetVideoStreams_Success() {
        // Given
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        Page<VideoStream> page = new PageImpl<>(streams);
        Pageable pageable = PageRequest.of(0, 10);
        when(videoStreamRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<VideoStreamDto> result = videoStreamService.getVideoStreams(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(videoStreamRepository).findAll(pageable);
    }

    @Test
    void testSearchVideoStreams_Success() {
        // Given
        String keyword = "test";
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        Page<VideoStream> page = new PageImpl<>(streams);
        Pageable pageable = PageRequest.of(0, 10);
        when(videoStreamRepository.searchByKeyword(keyword, pageable)).thenReturn(page);

        // When
        Page<VideoStreamDto> result = videoStreamService.searchVideoStreams(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(videoStreamRepository).searchByKeyword(keyword, pageable);
    }

    @Test
    void testGetVideoStreamsByStatus_Success() {
        // Given
        VideoStream.StreamStatus status = VideoStream.StreamStatus.ACTIVE;
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        Page<VideoStream> page = new PageImpl<>(streams);
        Pageable pageable = PageRequest.of(0, 10);
        when(videoStreamRepository.findByStatus(status, pageable)).thenReturn(page);

        // When
        Page<VideoStreamDto> result = videoStreamService.getVideoStreamsByStatus(status, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(videoStreamRepository).findByStatus(status, pageable);
    }

    @Test
    void testGetVideoStreamsByType_Success() {
        // Given
        VideoStream.StreamType type = VideoStream.StreamType.LIVE;
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        Page<VideoStream> page = new PageImpl<>(streams);
        Pageable pageable = PageRequest.of(0, 10);
        when(videoStreamRepository.findByType(type, pageable)).thenReturn(page);

        // When
        Page<VideoStreamDto> result = videoStreamService.getVideoStreamsByType(type, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(videoStreamRepository).findByType(type, pageable);
    }

    @Test
    void testGetVideoStreamsByDevice_Success() {
        // Given
        Long deviceId = 1L;
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        when(videoStreamRepository.findByDeviceId(deviceId)).thenReturn(streams);

        // When
        List<VideoStreamDto> result = videoStreamService.getVideoStreamsByDevice(deviceId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(videoStreamRepository).findByDeviceId(deviceId);
    }

    @Test
    void testGetActiveStreams_Success() {
        // Given
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        when(videoStreamRepository.findActiveStreams()).thenReturn(streams);

        // When
        List<VideoStreamDto> result = videoStreamService.getActiveStreams();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(videoStreamRepository).findActiveStreams();
    }

    @Test
    void testGetErrorStreams_Success() {
        // Given
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        when(videoStreamRepository.findErrorStreams()).thenReturn(streams);

        // When
        List<VideoStreamDto> result = videoStreamService.getErrorStreams();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(videoStreamRepository).findErrorStreams();
    }

    @Test
    void testStartStream_Success() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        VideoStreamDto result = videoStreamService.startStream(streamId);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).findById(streamId);
        verify(videoStreamRepository, atLeast(2)).save(testVideoStream);
    }

    @Test
    void testStartStream_AlreadyActive() {
        // Given
        Long streamId = 1L;
        testVideoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> videoStreamService.startStream(streamId));
        assertEquals("视频流已经在运行中", exception.getMessage());
    }

    @Test
    void testStopStream_Success() {
        // Given
        Long streamId = 1L;
        testVideoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        VideoStreamDto result = videoStreamService.stopStream(streamId);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).findById(streamId);
        verify(videoStreamRepository, atLeast(2)).save(testVideoStream);
    }

    @Test
    void testStopStream_AlreadyInactive() {
        // Given
        Long streamId = 1L;
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> videoStreamService.stopStream(streamId));
        assertEquals("视频流已经停止", exception.getMessage());
    }

    @Test
    void testRestartStream_Success() {
        // Given
        Long streamId = 1L;
        testVideoStream.setStatus(VideoStream.StreamStatus.ACTIVE);
        when(videoStreamRepository.findById(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        VideoStreamDto result = videoStreamService.restartStream(streamId);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository, atLeast(2)).findById(streamId);
        verify(videoStreamRepository, atLeast(4)).save(testVideoStream);
    }

    @Test
    void testUpdateStreamStatus_Success() {
        // Given
        String streamId = "stream-001";
        VideoStream.StreamStatus status = VideoStream.StreamStatus.ACTIVE;
        when(videoStreamRepository.findByStreamId(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        VideoStreamDto result = videoStreamService.updateStreamStatus(streamId, status);

        // Then
        assertNotNull(result);
        verify(videoStreamRepository).findByStreamId(streamId);
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testUpdateStreamMetrics_Success() {
        // Given
        String streamId = "stream-001";
        Double cpuUsage = 50.0;
        Double memoryUsage = 60.0;
        Double networkBandwidth = 1000.0;
        when(videoStreamRepository.findByStreamId(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        videoStreamService.updateStreamMetrics(streamId, cpuUsage, memoryUsage, networkBandwidth);

        // Then
        verify(videoStreamRepository).findByStreamId(streamId);
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testIncrementViewerCount_Success() {
        // Given
        String streamId = "stream-001";
        when(videoStreamRepository.findByStreamId(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        videoStreamService.incrementViewerCount(streamId);

        // Then
        verify(videoStreamRepository).findByStreamId(streamId);
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testDecrementViewerCount_Success() {
        // Given
        String streamId = "stream-001";
        when(videoStreamRepository.findByStreamId(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        videoStreamService.decrementViewerCount(streamId);

        // Then
        verify(videoStreamRepository).findByStreamId(streamId);
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testRecordStreamError_Success() {
        // Given
        String streamId = "stream-001";
        String error = "Connection timeout";
        when(videoStreamRepository.findByStreamId(streamId)).thenReturn(Optional.of(testVideoStream));
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        videoStreamService.recordStreamError(streamId, error);

        // Then
        verify(videoStreamRepository).findByStreamId(streamId);
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testGetStreamStatistics_Success() {
        // Given
        List<Object[]> statusCounts = Arrays.asList(
            new Object[]{"ACTIVE", 5L},
            new Object[]{"INACTIVE", 3L}
        );
        List<Object[]> typeCounts = Arrays.asList(
            new Object[]{"LIVE", 4L},
            new Object[]{"RECORDED", 4L}
        );
        when(videoStreamRepository.countByStatus()).thenReturn(statusCounts);
        when(videoStreamRepository.countByType()).thenReturn(typeCounts);
        when(videoStreamRepository.count()).thenReturn(8L);
        when(videoStreamRepository.findActiveStreams()).thenReturn(Arrays.asList(testVideoStream));
        when(videoStreamRepository.findErrorStreams()).thenReturn(Arrays.asList());
        when(videoStreamRepository.getTotalViewerCount()).thenReturn(100L);
        when(videoStreamRepository.getAverageCpuUsage()).thenReturn(45.5);
        when(videoStreamRepository.getAverageMemoryUsage()).thenReturn(55.2);
        when(videoStreamRepository.getTotalNetworkBandwidth()).thenReturn(5000.0);

        // When
        Map<String, Object> result = videoStreamService.getStreamStatistics();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("statusCounts"));
        assertTrue(result.containsKey("typeCounts"));
        assertEquals(8L, result.get("totalStreams"));
        assertEquals(1, result.get("activeStreams"));
        assertEquals(0, result.get("errorStreams"));
        assertEquals(100L, result.get("totalViewers"));
    }

    @Test
    void testCleanupInactiveStreams_Success() {
        // Given
        int minutes = 30;
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutes);
        List<VideoStream> inactiveStreams = Arrays.asList(testVideoStream);
        when(videoStreamRepository.findInactiveStreams(any(LocalDateTime.class))).thenReturn(inactiveStreams);
        when(videoStreamRepository.save(any(VideoStream.class))).thenReturn(testVideoStream);

        // When
        List<VideoStreamDto> result = videoStreamService.cleanupInactiveStreams(minutes);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(videoStreamRepository).findInactiveStreams(any(LocalDateTime.class));
        verify(videoStreamRepository).save(testVideoStream);
    }

    @Test
    void testBatchUpdateStatus_Success() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        VideoStream.StreamStatus status = VideoStream.StreamStatus.INACTIVE;
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        when(videoStreamRepository.findAllById(ids)).thenReturn(streams);
        when(videoStreamRepository.saveAll(anyList())).thenReturn(streams);

        // When
        videoStreamService.batchUpdateStatus(ids, status);

        // Then
        verify(videoStreamRepository).findAllById(ids);
        verify(videoStreamRepository).saveAll(streams);
    }

    @Test
    void testBatchDelete_Success() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);
        List<VideoStream> streams = Arrays.asList(testVideoStream);
        when(videoStreamRepository.findAllById(ids)).thenReturn(streams);

        // When
        videoStreamService.batchDelete(ids);

        // Then
        verify(videoStreamRepository).findAllById(ids);
        verify(videoStreamRepository).deleteAllById(ids);
    }
}