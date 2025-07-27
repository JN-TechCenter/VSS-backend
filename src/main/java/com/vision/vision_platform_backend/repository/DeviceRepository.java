package com.vision.vision_platform_backend.repository;

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
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    // 根据设备ID查找
    Optional<Device> findByDeviceId(String deviceId);
    
    // 根据设备名称查找
    Optional<Device> findByName(String name);
    
    // 根据设备类型查找
    List<Device> findByType(Device.DeviceType type);
    
    // 根据设备状态查找
    List<Device> findByStatus(Device.DeviceStatus status);
    
    // 根据位置查找
    List<Device> findByLocationContaining(String location);
    
    // 根据IP地址查找
    Optional<Device> findByIpAddress(String ipAddress);
    
    // 根据MAC地址查找
    Optional<Device> findByMacAddress(String macAddress);
    
    // 根据制造商查找
    List<Device> findByManufacturer(String manufacturer);
    
    // 分页查询设备
    Page<Device> findByTypeAndStatus(Device.DeviceType type, Device.DeviceStatus status, Pageable pageable);
    
    // 搜索设备（根据名称、位置、IP地址）
    @Query("SELECT d FROM Device d WHERE " +
           "d.name LIKE %:keyword% OR " +
           "d.location LIKE %:keyword% OR " +
           "d.ipAddress LIKE %:keyword% OR " +
           "d.deviceId LIKE %:keyword%")
    List<Device> searchDevices(@Param("keyword") String keyword);
    
    // 分页搜索设备
    @Query("SELECT d FROM Device d WHERE " +
           "d.name LIKE %:keyword% OR " +
           "d.location LIKE %:keyword% OR " +
           "d.ipAddress LIKE %:keyword% OR " +
           "d.deviceId LIKE %:keyword%")
    Page<Device> searchDevices(@Param("keyword") String keyword, Pageable pageable);
    
    // 查找在线设备
    @Query("SELECT d FROM Device d WHERE d.status = 'ONLINE'")
    List<Device> findOnlineDevices();
    
    // 查找离线设备
    @Query("SELECT d FROM Device d WHERE d.status = 'OFFLINE'")
    List<Device> findOfflineDevices();
    
    // 查找需要维护的设备
    @Query("SELECT d FROM Device d WHERE d.nextMaintenanceAt IS NOT NULL AND d.nextMaintenanceAt < :now")
    List<Device> findDevicesNeedingMaintenance(@Param("now") LocalDateTime now);
    
    // 查找长时间未心跳的设备
    @Query("SELECT d FROM Device d WHERE d.lastHeartbeat IS NOT NULL AND d.lastHeartbeat < :threshold")
    List<Device> findDevicesWithOldHeartbeat(@Param("threshold") LocalDateTime threshold);
    
    // 统计各种状态的设备数量
    @Query("SELECT d.status, COUNT(d) FROM Device d GROUP BY d.status")
    List<Object[]> countDevicesByStatus();
    
    // 统计各种类型的设备数量
    @Query("SELECT d.type, COUNT(d) FROM Device d GROUP BY d.type")
    List<Object[]> countDevicesByType();
    
    // 根据状态统计设备数量
    long countByStatus(Device.DeviceStatus status);
    
    // 根据类型统计设备数量
    long countByType(Device.DeviceType type);
    
    // 查找指定时间范围内创建的设备
    List<Device> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 查找指定时间范围内更新的设备
    List<Device> findByUpdatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 根据创建者查找设备
    List<Device> findByCreatedBy(String createdBy);
    
    // 检查设备ID是否存在
    boolean existsByDeviceId(String deviceId);
    
    // 检查IP地址是否存在
    boolean existsByIpAddress(String ipAddress);
    
    // 检查MAC地址是否存在
    boolean existsByMacAddress(String macAddress);
}