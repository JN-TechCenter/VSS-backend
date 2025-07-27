package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.model.VideoStream;
import com.vision.vision_platform_backend.model.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoStreamRepository extends JpaRepository<VideoStream, Long> {
    
    // 根据流ID查找
    Optional<VideoStream> findByStreamId(String streamId);
    
    // 根据设备查找流
    List<VideoStream> findByDevice(Device device);
    List<VideoStream> findByDeviceId(Long deviceId);
    
    // 根据状态查找
    List<VideoStream> findByStatus(VideoStream.StreamStatus status);
    Page<VideoStream> findByStatus(VideoStream.StreamStatus status, Pageable pageable);
    
    // 根据类型查找
    List<VideoStream> findByType(VideoStream.StreamType type);
    Page<VideoStream> findByType(VideoStream.StreamType type, Pageable pageable);
    
    // 查找活跃的流
    @Query("SELECT v FROM VideoStream v WHERE v.status = 'ACTIVE'")
    List<VideoStream> findActiveStreams();
    
    // 查找有错误的流
    @Query("SELECT v FROM VideoStream v WHERE v.status = 'ERROR'")
    List<VideoStream> findErrorStreams();
    
    // 查找需要录制的流
    @Query("SELECT v FROM VideoStream v WHERE v.recordingEnabled = true AND v.status = 'ACTIVE'")
    List<VideoStream> findRecordingStreams();
    
    // 根据名称搜索（模糊匹配）
    @Query("SELECT v FROM VideoStream v WHERE v.name LIKE %:keyword% OR v.description LIKE %:keyword%")
    Page<VideoStream> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 查找指定时间后活跃的流
    @Query("SELECT v FROM VideoStream v WHERE v.lastActiveTime > :time")
    List<VideoStream> findActiveStreamsSince(@Param("time") LocalDateTime time);
    
    // 查找长时间未活跃的流
    @Query("SELECT v FROM VideoStream v WHERE v.lastActiveTime < :time AND v.status = 'ACTIVE'")
    List<VideoStream> findInactiveStreams(@Param("time") LocalDateTime time);
    
    // 统计各状态的流数量
    @Query("SELECT v.status, COUNT(v) FROM VideoStream v GROUP BY v.status")
    List<Object[]> countByStatus();
    
    // 统计各类型的流数量
    @Query("SELECT v.type, COUNT(v) FROM VideoStream v GROUP BY v.type")
    List<Object[]> countByType();
    
    // 查找高错误率的流
    @Query("SELECT v FROM VideoStream v WHERE v.errorCount > :threshold")
    List<VideoStream> findHighErrorStreams(@Param("threshold") Integer threshold);
    
    // 查找观看人数最多的流
    @Query("SELECT v FROM VideoStream v WHERE v.status = 'ACTIVE' ORDER BY v.viewerCount DESC")
    List<VideoStream> findMostViewedStreams(Pageable pageable);
    
    // 查找CPU使用率高的流
    @Query("SELECT v FROM VideoStream v WHERE v.cpuUsage > :threshold AND v.status = 'ACTIVE'")
    List<VideoStream> findHighCpuStreams(@Param("threshold") Double threshold);
    
    // 查找内存使用率高的流
    @Query("SELECT v FROM VideoStream v WHERE v.memoryUsage > :threshold AND v.status = 'ACTIVE'")
    List<VideoStream> findHighMemoryStreams(@Param("threshold") Double threshold);
    
    // 删除指定设备的所有流
    void deleteByDevice(Device device);
    void deleteByDeviceId(Long deviceId);
    
    // 检查流ID是否存在
    boolean existsByStreamId(String streamId);
    
    // 统计总观看人数
    @Query("SELECT SUM(v.viewerCount) FROM VideoStream v WHERE v.status = 'ACTIVE'")
    Long getTotalViewerCount();
    
    // 获取平均CPU使用率
    @Query("SELECT AVG(v.cpuUsage) FROM VideoStream v WHERE v.status = 'ACTIVE' AND v.cpuUsage IS NOT NULL")
    Double getAverageCpuUsage();
    
    // 获取平均内存使用率
    @Query("SELECT AVG(v.memoryUsage) FROM VideoStream v WHERE v.status = 'ACTIVE' AND v.memoryUsage IS NOT NULL")
    Double getAverageMemoryUsage();
    
    // 获取总网络带宽
    @Query("SELECT SUM(v.networkBandwidth) FROM VideoStream v WHERE v.status = 'ACTIVE' AND v.networkBandwidth IS NOT NULL")
    Double getTotalNetworkBandwidth();
}