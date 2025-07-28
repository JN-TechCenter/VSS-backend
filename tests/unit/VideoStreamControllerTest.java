package com.vision.vision_platform_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.vision_platform_backend.dto.VideoStreamDto;
import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.service.VideoStreamService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 视频流控制器单元测试
 */
@ExtendWith(MockitoExtension.class)
class VideoStreamControllerTest {

    @Mock
    private VideoStreamService videoStreamService;

    @InjectMocks
    private VideoStreamController videoStreamController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private VideoStreamDto testStreamDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(videoStreamController).build();
        objectMapper = new ObjectMapper();
        
        testStreamDto = new VideoStreamDto();
        testStreamDto.setId(1L);
        testStreamDto.setStreamId("stream-001");
        testStreamDto.setName("测试视频流");
        testStreamDto.setDescription("测试描述");
        testStreamDto.setStreamUrl("rtmp://test.com/stream");
        testStreamDto.setStatus(VideoStream.StreamStatus.ACTIVE);
        testStreamDto.setType(VideoStream.StreamType.LIVE);
        testStreamDto.setDeviceId(1L);
        testStreamDto.setCreatedAt(LocalDateTime.now());
        testStreamDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateVideoStream_Success() throws Exception {
        // Given
        when(videoStreamService.createVideoStream(any(VideoStreamDto.class))).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(post("/api/video-streams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStreamDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("视频流创建成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).createVideoStream(any(VideoStreamDto.class));
    }

    @Test
    void testCreateVideoStream_StreamIdExists() throws Exception {
        // Given
        when(videoStreamService.createVideoStream(any(VideoStreamDto.class)))
                .thenThrow(new RuntimeException("流ID已存在"));

        // When & Then
        mockMvc.perform(post("/api/video-streams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStreamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("创建视频流失败: 流ID已存在"));
    }

    @Test
    void testUpdateVideoStream_Success() throws Exception {
        // Given
        when(videoStreamService.updateVideoStream(eq(1L), any(VideoStreamDto.class))).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(put("/api/video-streams/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStreamDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("视频流更新成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).updateVideoStream(eq(1L), any(VideoStreamDto.class));
    }

    @Test
    void testUpdateVideoStream_NotFound() throws Exception {
        // Given
        when(videoStreamService.updateVideoStream(eq(1L), any(VideoStreamDto.class)))
                .thenThrow(new RuntimeException("视频流不存在"));

        // When & Then
        mockMvc.perform(put("/api/video-streams/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testStreamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("更新视频流失败: 视频流不存在"));
    }

    @Test
    void testDeleteVideoStream_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).deleteVideoStream(1L);

        // When & Then
        mockMvc.perform(delete("/api/video-streams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("视频流删除成功"));

        verify(videoStreamService).deleteVideoStream(1L);
    }

    @Test
    void testDeleteVideoStream_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("视频流不存在")).when(videoStreamService).deleteVideoStream(1L);

        // When & Then
        mockMvc.perform(delete("/api/video-streams/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("删除视频流失败: 视频流不存在"));
    }

    @Test
    void testGetVideoStream_Success() throws Exception {
        // Given
        when(videoStreamService.getVideoStream(1L)).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(get("/api/video-streams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取视频流成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).getVideoStream(1L);
    }

    @Test
    void testGetVideoStream_NotFound() throws Exception {
        // Given
        when(videoStreamService.getVideoStream(1L)).thenThrow(new RuntimeException("视频流不存在"));

        // When & Then
        mockMvc.perform(get("/api/video-streams/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("获取视频流失败: 视频流不存在"));
    }

    @Test
    void testGetVideoStreamByStreamId_Success() throws Exception {
        // Given
        when(videoStreamService.getVideoStreamByStreamId("stream-001")).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(get("/api/video-streams/stream/stream-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取视频流成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).getVideoStreamByStreamId("stream-001");
    }

    @Test
    void testGetVideoStreams_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        Page<VideoStreamDto> page = new PageImpl<>(streams, PageRequest.of(0, 10), 1);
        when(videoStreamService.getVideoStreams(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/video-streams")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取视频流列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(videoStreamService).getVideoStreams(any(Pageable.class));
    }

    @Test
    void testSearchVideoStreams_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        Page<VideoStreamDto> page = new PageImpl<>(streams, PageRequest.of(0, 10), 1);
        when(videoStreamService.searchVideoStreams(eq("test"), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/video-streams/search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("搜索视频流成功"))
                .andExpect(jsonPath("$.keyword").value("test"))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).searchVideoStreams(eq("test"), any(Pageable.class));
    }

    @Test
    void testGetVideoStreamsByStatus_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        Page<VideoStreamDto> page = new PageImpl<>(streams, PageRequest.of(0, 10), 1);
        when(videoStreamService.getVideoStreamsByStatus(eq(VideoStream.StreamStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/video-streams/status/ACTIVE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取ACTIVE状态视频流成功"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).getVideoStreamsByStatus(eq(VideoStream.StreamStatus.ACTIVE), any(Pageable.class));
    }

    @Test
    void testGetVideoStreamsByType_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        Page<VideoStreamDto> page = new PageImpl<>(streams, PageRequest.of(0, 10), 1);
        when(videoStreamService.getVideoStreamsByType(eq(VideoStream.StreamType.LIVE), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/video-streams/type/LIVE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取LIVE类型视频流成功"))
                .andExpect(jsonPath("$.type").value("LIVE"))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).getVideoStreamsByType(eq(VideoStream.StreamType.LIVE), any(Pageable.class));
    }

    @Test
    void testGetVideoStreamsByDevice_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        when(videoStreamService.getVideoStreamsByDevice(1L)).thenReturn(streams);

        // When & Then
        mockMvc.perform(get("/api/video-streams/device/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取设备视频流成功"))
                .andExpect(jsonPath("$.deviceId").value(1))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).getVideoStreamsByDevice(1L);
    }

    @Test
    void testGetActiveStreams_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        when(videoStreamService.getActiveStreams()).thenReturn(streams);

        // When & Then
        mockMvc.perform(get("/api/video-streams/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取活跃视频流成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).getActiveStreams();
    }

    @Test
    void testGetErrorStreams_Success() throws Exception {
        // Given
        List<VideoStreamDto> streams = Arrays.asList(testStreamDto);
        when(videoStreamService.getErrorStreams()).thenReturn(streams);

        // When & Then
        mockMvc.perform(get("/api/video-streams/errors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取错误视频流成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).getErrorStreams();
    }

    @Test
    void testStartStream_Success() throws Exception {
        // Given
        when(videoStreamService.startStream(1L)).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(post("/api/video-streams/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("视频流启动成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).startStream(1L);
    }

    @Test
    void testStartStream_AlreadyRunning() throws Exception {
        // Given
        when(videoStreamService.startStream(1L)).thenThrow(new RuntimeException("视频流已在运行"));

        // When & Then
        mockMvc.perform(post("/api/video-streams/1/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("启动视频流失败: 视频流已在运行"));
    }

    @Test
    void testStopStream_Success() throws Exception {
        // Given
        when(videoStreamService.stopStream(1L)).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(post("/api/video-streams/1/stop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("视频流停止成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).stopStream(1L);
    }

    @Test
    void testRestartStream_Success() throws Exception {
        // Given
        when(videoStreamService.restartStream(1L)).thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(post("/api/video-streams/1/restart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("视频流重启成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).restartStream(1L);
    }

    @Test
    void testUpdateStreamStatus_Success() throws Exception {
        // Given
        when(videoStreamService.updateStreamStatus("stream-001", VideoStream.StreamStatus.ACTIVE))
                .thenReturn(testStreamDto);

        // When & Then
        mockMvc.perform(put("/api/video-streams/stream/stream-001/status")
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("流状态更新成功"))
                .andExpect(jsonPath("$.data.streamId").value("stream-001"));

        verify(videoStreamService).updateStreamStatus("stream-001", VideoStream.StreamStatus.ACTIVE);
    }

    @Test
    void testUpdateStreamMetrics_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).updateStreamMetrics("stream-001", 50.0, 60.0, 100.0);

        // When & Then
        mockMvc.perform(put("/api/video-streams/stream/stream-001/metrics")
                .param("cpuUsage", "50.0")
                .param("memoryUsage", "60.0")
                .param("networkBandwidth", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("流监控信息更新成功"));

        verify(videoStreamService).updateStreamMetrics("stream-001", 50.0, 60.0, 100.0);
    }

    @Test
    void testJoinViewer_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).incrementViewerCount("stream-001");

        // When & Then
        mockMvc.perform(post("/api/video-streams/stream/stream-001/viewer/join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("观看人数增加成功"));

        verify(videoStreamService).incrementViewerCount("stream-001");
    }

    @Test
    void testLeaveViewer_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).decrementViewerCount("stream-001");

        // When & Then
        mockMvc.perform(post("/api/video-streams/stream/stream-001/viewer/leave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("观看人数减少成功"));

        verify(videoStreamService).decrementViewerCount("stream-001");
    }

    @Test
    void testRecordStreamError_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).recordStreamError("stream-001", "连接超时");

        // When & Then
        mockMvc.perform(post("/api/video-streams/stream/stream-001/error")
                .param("error", "连接超时"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("流错误记录成功"));

        verify(videoStreamService).recordStreamError("stream-001", "连接超时");
    }

    @Test
    void testGetStreamStatistics_Success() throws Exception {
        // Given
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStreams", 10);
        stats.put("activeStreams", 8);
        stats.put("errorStreams", 2);
        when(videoStreamService.getStreamStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/video-streams/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("获取流统计信息成功"))
                .andExpect(jsonPath("$.data.totalStreams").value(10))
                .andExpect(jsonPath("$.data.activeStreams").value(8));

        verify(videoStreamService).getStreamStatistics();
    }

    @Test
    void testCleanupInactiveStreams_Success() throws Exception {
        // Given
        List<VideoStreamDto> cleaned = Arrays.asList(testStreamDto);
        when(videoStreamService.cleanupInactiveStreams(30)).thenReturn(cleaned);

        // When & Then
        mockMvc.perform(post("/api/video-streams/cleanup")
                .param("minutes", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("清理未活跃流成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data").isArray());

        verify(videoStreamService).cleanupInactiveStreams(30);
    }

    @Test
    void testBatchUpdateStatus_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).batchUpdateStatus(anyList(), eq(VideoStream.StreamStatus.INACTIVE));

        // When & Then
        mockMvc.perform(put("/api/video-streams/batch/status")
                .param("ids", "1,2,3")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("批量更新状态成功"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(videoStreamService).batchUpdateStatus(anyList(), eq(VideoStream.StreamStatus.INACTIVE));
    }

    @Test
    void testBatchDelete_Success() throws Exception {
        // Given
        doNothing().when(videoStreamService).batchDelete(anyList());

        // When & Then
        mockMvc.perform(delete("/api/video-streams/batch")
                .param("ids", "1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("批量删除成功"));

        verify(videoStreamService).batchDelete(anyList());
    }
}