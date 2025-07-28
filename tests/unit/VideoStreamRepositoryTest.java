package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.model.Device;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
 * VideoStreamRepository 单元测试
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("VideoStreamRepository 测试")
public class VideoStreamRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VideoStreamRepository repository;

    private Device testDevice1;
    private Device testDevice2;
    private VideoStream testStream1;
    private VideoStream testStream2;
    private VideoStream testStream3;

    @BeforeEach
    void setUp() {
        // 创建测试设备
        testDevice1 = new Device();
        testDevice1.setDeviceId("device-001");
        testDevice1.setName("Camera 1");
        testDevice1.setType(Device.DeviceType.CAMERA);
        testDevice1.setStatus(Device.DeviceStatus.ONLINE);
        testDevice1.setLocation("Location 1");
        testDevice1.setIpAddress("192.168.1.100");
        testDevice1.setCreatedAt(LocalDateTime.now());
        testDevice1.setUpdatedAt(LocalDateTime.now());
        testDevice1 = entityManager.persistAndFlush(testDevice1);

        testDevice2 = new Device();
        testDevice2.setDeviceId("device-002");
        testDevice2.setName("Camera 2");
        testDevice2.setType(Device.DeviceType.CAMERA);
        testDevice2.setStatus(Device.DeviceStatus.ONLINE);
        testDevice2.setLocation("Location 2");
        testDevice2.setIpAddress("192.168.1.101");
        testDevice2.setCreatedAt(LocalDateTime.now());
        testDevice2.setUpdatedAt(LocalDateTime.now());
        testDevice2 = entityManager.persistAndFlush(testDevice2);

        // 创建测试视频流
        testStream1 = new VideoStream();
        testStream1.setStreamId("stream-001");
        testStream1.setName("Test Stream 1");
        testStream1.setDescription("Test Description 1");
        testStream1.setType(VideoStream.StreamType.LIVE);
        testStream1.setStatus(VideoStream.StreamStatus.ACTIVE);
        testStream1.setSourceUrl("rtmp://source1.url");
        testStream1.setOutputUrl("http://output1.url");
        testStream1.setProtocol(VideoStream.StreamProtocol.RTMP);
        testStream1.setQuality(VideoStream.StreamQuality.HD);
        testStream1.setWidth(1920);
        testStream1.setHeight(1080);
        testStream1.setFrameRate(30);
        testStream1.setBitrate(5000);
        testStream1.setDevice(testDevice1);
        testStream1.setRecordingEnabled(true);
        testStream1.setRecordingPath("/recordings/stream1");
        testStream1.setRecordingDuration(60);
        testStream1.setTranscodeEnabled(false);
        testStream1.setLastActiveTime(LocalDateTime.now().minusMinutes(5));
        testStream1.setViewerCount(10L);
        testStream1.setCpuUsage(45.5);
        testStream1.setMemoryUsage(30.2);
        testStream1.setNetworkBandwidth(100.0);
        testStream1.setErrorCount(0);
        testStream1.setCreatedAt(LocalDateTime.now().minusDays(1));
        testStream1.setUpdatedAt(LocalDateTime.now().minusDays(1));
        testStream1.setCreatedBy("admin");
        testStream1.setUpdatedBy("admin");

        testStream2 = new VideoStream();
        testStream2.setStreamId("stream-002");
        testStream2.setName("Test Stream 2");
        testStream2.setDescription("Test Description 2");
        testStream2.setType(VideoStream.StreamType.RECORDED);
        testStream2.setStatus(VideoStream.StreamStatus.INACTIVE);
        testStream2.setSourceUrl("http://source2.url");
        testStream2.setOutputUrl("http://output2.url");
        testStream2.setProtocol(VideoStream.StreamProtocol.HTTP);
        testStream2.setQuality(VideoStream.StreamQuality.FHD);
        testStream2.setWidth(1920);
        testStream2.setHeight(1080);
        testStream2.setFrameRate(25);
        testStream2.setBitrate(3000);
        testStream2.setDevice(testDevice2);
        testStream2.setRecordingEnabled(false);
        testStream2.setTranscodeEnabled(true);
        testStream2.setTranscodeFormat("mp4");
        testStream2.setTranscodeQuality("high");
        testStream2.setLastActiveTime(LocalDateTime.now().minusHours(2));
        testStream2.setViewerCount(5L);
        testStream2.setCpuUsage(25.0);
        testStream2.setMemoryUsage(20.5);
        testStream2.setNetworkBandwidth(50.0);
        testStream2.setErrorCount(2);
        testStream2.setCreatedAt(LocalDateTime.now().minusHours(12));
        testStream2.setUpdatedAt(LocalDateTime.now().minusHours(12));
        testStream2.setCreatedBy("user");
        testStream2.setUpdatedBy("user");

        testStream3 = new VideoStream();
        testStream3.setStreamId("stream-003");
        testStream3.setName("Error Stream");
        testStream3.setDescription("Stream with errors");
        testStream3.setType(VideoStream.StreamType.LIVE);
        testStream3.setStatus(VideoStream.StreamStatus.ERROR);
        testStream3.setSourceUrl("rtmp://source3.url");
        testStream3.setOutputUrl("http://output3.url");
        testStream3.setProtocol(VideoStream.StreamProtocol.RTMP);
        testStream3.setQuality(VideoStream.StreamQuality.SD);
        testStream3.setWidth(1280);
        testStream3.setHeight(720);
        testStream3.setFrameRate(30);
        testStream3.setBitrate(2000);
        testStream3.setDevice(testDevice1);
        testStream3.setRecordingEnabled(true);
        testStream3.setTranscodeEnabled(false);
        testStream3.setLastActiveTime(LocalDateTime.now().minusHours(6));
        testStream3.setViewerCount(0L);
        testStream3.setCpuUsage(80.0);
        testStream3.setMemoryUsage(70.0);
        testStream3.setNetworkBandwidth(0.0);
        testStream3.setLastError("Connection failed");
        testStream3.setLastErrorTime(LocalDateTime.now().minusHours(1));
        testStream3.setErrorCount(5);
        testStream3.setCreatedAt(LocalDateTime.now().minusHours(6));
        testStream3.setUpdatedAt(LocalDateTime.now().minusHours(1));
        testStream3.setCreatedBy("admin");
        testStream3.setUpdatedBy("admin");

        entityManager.persistAndFlush(testStream1);
        entityManager.persistAndFlush(testStream2);
        entityManager.persistAndFlush(testStream3);
    }

    @Nested
    @DisplayName("基本查询测试")
    class BasicQueryTest {

        @Test
        @DisplayName("根据流ID查找")
        void testFindByStreamId() {
            Optional<VideoStream> result = repository.findByStreamId("stream-001");
            assertTrue(result.isPresent());
            assertEquals("stream-001", result.get().getStreamId());
            assertEquals("Test Stream 1", result.get().getName());
        }

        @Test
        @DisplayName("根据流ID查找 - 不存在")
        void testFindByStreamIdNotFound() {
            Optional<VideoStream> result = repository.findByStreamId("non-existent");
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("根据设备查找流")
        void testFindByDevice() {
            List<VideoStream> result = repository.findByDevice(testDevice1);
            assertEquals(2, result.size());
            assertTrue(result.stream().allMatch(s -> s.getDevice().getId().equals(testDevice1.getId())));
        }

        @Test
        @DisplayName("根据设备ID查找流")
        void testFindByDeviceId() {
            List<VideoStream> result = repository.findByDeviceId(testDevice2.getId());
            assertEquals(1, result.size());
            assertEquals("stream-002", result.get(0).getStreamId());
        }

        @Test
        @DisplayName("根据状态查找")
        void testFindByStatus() {
            List<VideoStream> activeStreams = repository.findByStatus(VideoStream.StreamStatus.ACTIVE);
            assertEquals(1, activeStreams.size());
            assertEquals("stream-001", activeStreams.get(0).getStreamId());

            List<VideoStream> errorStreams = repository.findByStatus(VideoStream.StreamStatus.ERROR);
            assertEquals(1, errorStreams.size());
            assertEquals("stream-003", errorStreams.get(0).getStreamId());
        }

        @Test
        @DisplayName("根据状态查找 - 分页")
        void testFindByStatusWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<VideoStream> result = repository.findByStatus(VideoStream.StreamStatus.ACTIVE, pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("stream-001", result.getContent().get(0).getStreamId());
        }

        @Test
        @DisplayName("根据类型查找")
        void testFindByType() {
            List<VideoStream> liveStreams = repository.findByType(VideoStream.StreamType.LIVE);
            assertEquals(2, liveStreams.size());

            List<VideoStream> recordedStreams = repository.findByType(VideoStream.StreamType.RECORDED);
            assertEquals(1, recordedStreams.size());
            assertEquals("stream-002", recordedStreams.get(0).getStreamId());
        }

        @Test
        @DisplayName("根据类型查找 - 分页")
        void testFindByTypeWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<VideoStream> result = repository.findByType(VideoStream.StreamType.LIVE, pageable);
            
            assertEquals(2, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("自定义查询测试")
    class CustomQueryTest {

        @Test
        @DisplayName("查找活跃的流")
        void testFindActiveStreams() {
            List<VideoStream> result = repository.findActiveStreams();
            assertEquals(1, result.size());
            assertEquals("stream-001", result.get(0).getStreamId());
            assertEquals(VideoStream.StreamStatus.ACTIVE, result.get(0).getStatus());
        }

        @Test
        @DisplayName("查找有错误的流")
        void testFindErrorStreams() {
            List<VideoStream> result = repository.findErrorStreams();
            assertEquals(1, result.size());
            assertEquals("stream-003", result.get(0).getStreamId());
            assertEquals(VideoStream.StreamStatus.ERROR, result.get(0).getStatus());
        }

        @Test
        @DisplayName("查找需要录制的流")
        void testFindRecordingStreams() {
            List<VideoStream> result = repository.findRecordingStreams();
            assertEquals(1, result.size());
            assertEquals("stream-001", result.get(0).getStreamId());
            assertTrue(result.get(0).getRecordingEnabled());
            assertEquals(VideoStream.StreamStatus.ACTIVE, result.get(0).getStatus());
        }

        @Test
        @DisplayName("根据关键词搜索")
        void testSearchByKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<VideoStream> result = repository.searchByKeyword("Test Stream 1", pageable);
            
            assertEquals(1, result.getContent().size());
            assertEquals("stream-001", result.getContent().get(0).getStreamId());

            // 测试描述搜索
            Page<VideoStream> descResult = repository.searchByKeyword("Description 2", pageable);
            assertEquals(1, descResult.getContent().size());
            assertEquals("stream-002", descResult.getContent().get(0).getStreamId());
        }

        @Test
        @DisplayName("查找指定时间后活跃的流")
        void testFindActiveStreamsSince() {
            LocalDateTime time = LocalDateTime.now().minusMinutes(10);
            List<VideoStream> result = repository.findActiveStreamsSince(time);
            
            assertEquals(1, result.size());
            assertEquals("stream-001", result.get(0).getStreamId());
        }

        @Test
        @DisplayName("查找长时间未活跃的流")
        void testFindInactiveStreams() {
            LocalDateTime time = LocalDateTime.now().minusMinutes(10);
            List<VideoStream> result = repository.findInactiveStreams(time);
            
            // 没有活跃状态但长时间未活跃的流
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("查找高错误率的流")
        void testFindHighErrorStreams() {
            List<VideoStream> result = repository.findHighErrorStreams(3);
            assertEquals(1, result.size());
            assertEquals("stream-003", result.get(0).getStreamId());
            assertTrue(result.get(0).getErrorCount() > 3);
        }

        @Test
        @DisplayName("查找观看人数最多的流")
        void testFindMostViewedStreams() {
            Pageable pageable = PageRequest.of(0, 5);
            List<VideoStream> result = repository.findMostViewedStreams(pageable);
            
            assertEquals(1, result.size()); // 只有活跃的流
            assertEquals("stream-001", result.get(0).getStreamId());
            assertEquals(10L, result.get(0).getViewerCount());
        }

        @Test
        @DisplayName("查找CPU使用率高的流")
        void testFindHighCpuStreams() {
            List<VideoStream> result = repository.findHighCpuStreams(50.0);
            assertEquals(1, result.size());
            assertEquals("stream-003", result.get(0).getStreamId());
            assertTrue(result.get(0).getCpuUsage() > 50.0);
        }

        @Test
        @DisplayName("查找内存使用率高的流")
        void testFindHighMemoryStreams() {
            List<VideoStream> result = repository.findHighMemoryStreams(50.0);
            assertEquals(1, result.size());
            assertEquals("stream-003", result.get(0).getStreamId());
            assertTrue(result.get(0).getMemoryUsage() > 50.0);
        }
    }

    @Nested
    @DisplayName("统计查询测试")
    class StatisticsTest {

        @Test
        @DisplayName("统计各状态的流数量")
        void testCountByStatus() {
            List<Object[]> results = repository.countByStatus();
            assertEquals(3, results.size());
            
            // 验证包含所有状态
            boolean hasActive = results.stream().anyMatch(r -> VideoStream.StreamStatus.ACTIVE.equals(r[0]));
            boolean hasInactive = results.stream().anyMatch(r -> VideoStream.StreamStatus.INACTIVE.equals(r[0]));
            boolean hasError = results.stream().anyMatch(r -> VideoStream.StreamStatus.ERROR.equals(r[0]));
            
            assertTrue(hasActive);
            assertTrue(hasInactive);
            assertTrue(hasError);
        }

        @Test
        @DisplayName("统计各类型的流数量")
        void testCountByType() {
            List<Object[]> results = repository.countByType();
            assertEquals(2, results.size());
            
            // 验证包含预期类型
            boolean hasLive = results.stream().anyMatch(r -> VideoStream.StreamType.LIVE.equals(r[0]));
            boolean hasRecorded = results.stream().anyMatch(r -> VideoStream.StreamType.RECORDED.equals(r[0]));
            
            assertTrue(hasLive);
            assertTrue(hasRecorded);
        }

        @Test
        @DisplayName("统计总观看人数")
        void testGetTotalViewerCount() {
            Long totalViewers = repository.getTotalViewerCount();
            assertEquals(10L, totalViewers); // 只有活跃的流计算观看人数
        }

        @Test
        @DisplayName("获取平均CPU使用率")
        void testGetAverageCpuUsage() {
            Double avgCpu = repository.getAverageCpuUsage();
            assertNotNull(avgCpu);
            assertEquals(45.5, avgCpu); // 只有活跃的流
        }

        @Test
        @DisplayName("获取平均内存使用率")
        void testGetAverageMemoryUsage() {
            Double avgMemory = repository.getAverageMemoryUsage();
            assertNotNull(avgMemory);
            assertEquals(30.2, avgMemory); // 只有活跃的流
        }

        @Test
        @DisplayName("获取总网络带宽")
        void testGetTotalNetworkBandwidth() {
            Double totalBandwidth = repository.getTotalNetworkBandwidth();
            assertNotNull(totalBandwidth);
            assertEquals(100.0, totalBandwidth); // 只有活跃的流
        }
    }

    @Nested
    @DisplayName("删除操作测试")
    class DeleteOperationTest {

        @Test
        @DisplayName("根据设备删除流")
        void testDeleteByDevice() {
            // 验证删除前的数量
            List<VideoStream> beforeDelete = repository.findByDevice(testDevice1);
            assertEquals(2, beforeDelete.size());

            // 执行删除
            repository.deleteByDevice(testDevice1);
            entityManager.flush();

            // 验证删除后的数量
            List<VideoStream> afterDelete = repository.findByDevice(testDevice1);
            assertEquals(0, afterDelete.size());

            // 验证其他设备的流未受影响
            List<VideoStream> otherDeviceStreams = repository.findByDevice(testDevice2);
            assertEquals(1, otherDeviceStreams.size());
        }

        @Test
        @DisplayName("根据设备ID删除流")
        void testDeleteByDeviceId() {
            // 验证删除前的数量
            List<VideoStream> beforeDelete = repository.findByDeviceId(testDevice2.getId());
            assertEquals(1, beforeDelete.size());

            // 执行删除
            repository.deleteByDeviceId(testDevice2.getId());
            entityManager.flush();

            // 验证删除后的数量
            List<VideoStream> afterDelete = repository.findByDeviceId(testDevice2.getId());
            assertEquals(0, afterDelete.size());
        }
    }

    @Test
    @DisplayName("检查流ID是否存在")
    void testExistsByStreamId() {
        assertTrue(repository.existsByStreamId("stream-001"));
        assertTrue(repository.existsByStreamId("stream-002"));
        assertTrue(repository.existsByStreamId("stream-003"));
        assertFalse(repository.existsByStreamId("non-existent"));
    }

    @Test
    @DisplayName("分页功能测试")
    void testPagination() {
        // 添加更多测试数据
        for (int i = 4; i <= 15; i++) {
            VideoStream stream = new VideoStream();
            stream.setStreamId("stream-" + String.format("%03d", i));
            stream.setName("Test Stream " + i);
            stream.setType(VideoStream.StreamType.LIVE);
            stream.setStatus(VideoStream.StreamStatus.ACTIVE);
            stream.setSourceUrl("rtmp://source" + i + ".url");
            stream.setDevice(testDevice1);
            stream.setCreatedAt(LocalDateTime.now().minusMinutes(i));
            stream.setUpdatedAt(LocalDateTime.now().minusMinutes(i));
            entityManager.persistAndFlush(stream);
        }

        // 测试第一页
        Pageable firstPage = PageRequest.of(0, 5);
        Page<VideoStream> firstResult = repository.findByStatus(VideoStream.StreamStatus.ACTIVE, firstPage);
        
        assertEquals(5, firstResult.getContent().size());
        assertEquals(0, firstResult.getNumber());
        assertTrue(firstResult.hasNext());
        assertFalse(firstResult.hasPrevious());

        // 测试第二页
        Pageable secondPage = PageRequest.of(1, 5);
        Page<VideoStream> secondResult = repository.findByStatus(VideoStream.StreamStatus.ACTIVE, secondPage);
        
        assertEquals(5, secondResult.getContent().size());
        assertEquals(1, secondResult.getNumber());
        assertTrue(secondResult.hasNext());
        assertTrue(secondResult.hasPrevious());
    }

    @Test
    @DisplayName("空结果测试")
    void testEmptyResults() {
        // 测试不存在的设备ID
        List<VideoStream> result = repository.findByDeviceId(999L);
        assertTrue(result.isEmpty());

        // 测试不存在的状态（虽然枚举不会有不存在的情况，但测试空结果）
        Pageable pageable = PageRequest.of(0, 10);
        Page<VideoStream> searchResult = repository.searchByKeyword("non-existent-keyword", pageable);
        assertTrue(searchResult.getContent().isEmpty());
        assertEquals(0, searchResult.getTotalElements());

        // 测试未来时间的活跃流查询
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        List<VideoStream> futureResult = repository.findActiveStreamsSince(futureTime);
        assertTrue(futureResult.isEmpty());
    }

    @Test
    @DisplayName("边界条件测试")
    void testBoundaryConditions() {
        // 测试阈值边界
        List<VideoStream> exactThresholdCpu = repository.findHighCpuStreams(45.5);
        assertEquals(1, exactThresholdCpu.size()); // CPU使用率为80.0的流

        List<VideoStream> belowThresholdCpu = repository.findHighCpuStreams(85.0);
        assertEquals(0, belowThresholdCpu.size());

        // 测试错误计数边界
        List<VideoStream> exactErrorCount = repository.findHighErrorStreams(5);
        assertEquals(0, exactErrorCount.size()); // 错误计数为5，不大于5

        List<VideoStream> belowErrorCount = repository.findHighErrorStreams(4);
        assertEquals(1, belowErrorCount.size()); // 错误计数为5，大于4
    }
}