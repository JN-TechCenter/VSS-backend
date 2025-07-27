package com.vision.vision_platform_backend.repository;

import com.vision.vision_platform_backend.entity.InferenceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 推理历史记录Repository
 */
@Repository
public interface InferenceHistoryRepository extends JpaRepository<InferenceHistory, Long> {

    /**
     * 根据任务ID查找
     */
    Optional<InferenceHistory> findByTaskId(String taskId);

    /**
     * 根据用户ID查找推理历史（分页）
     */
    Page<InferenceHistory> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * 根据用户名查找推理历史（分页）
     */
    Page<InferenceHistory> findByUsernameAndIsDeletedFalseOrderByCreatedAtDesc(String username, Pageable pageable);

    /**
     * 查找所有未删除的推理历史（分页）
     */
    Page<InferenceHistory> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 根据推理类型查找
     */
    Page<InferenceHistory> findByInferenceTypeAndIsDeletedFalseOrderByCreatedAtDesc(String inferenceType, Pageable pageable);

    /**
     * 根据模型名称查找
     */
    Page<InferenceHistory> findByModelNameAndIsDeletedFalseOrderByCreatedAtDesc(String modelName, Pageable pageable);

    /**
     * 根据状态查找
     */
    Page<InferenceHistory> findByStatusAndIsDeletedFalseOrderByCreatedAtDesc(String status, Pageable pageable);

    /**
     * 根据时间范围查找
     */
    Page<InferenceHistory> findByCreatedAtBetweenAndIsDeletedFalseOrderByCreatedAtDesc(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 查找收藏的推理记录
     */
    Page<InferenceHistory> findByIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 根据用户查找收藏的推理记录
     */
    Page<InferenceHistory> findByUserIdAndIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(
            UUID userId, Pageable pageable);

    /**
     * 复合搜索查询
     */
    @Query("SELECT ih FROM InferenceHistory ih WHERE " +
           "ih.isDeleted = false AND " +
           "(:keyword IS NULL OR " +
           "ih.originalFilename LIKE %:keyword% OR " +
           "ih.modelName LIKE %:keyword% OR " +
           "ih.username LIKE %:keyword% OR " +
           "ih.tags LIKE %:keyword% OR " +
           "ih.notes LIKE %:keyword%) AND " +
           "(:inferenceType IS NULL OR ih.inferenceType = :inferenceType) AND " +
           "(:modelName IS NULL OR ih.modelName = :modelName) AND " +
           "(:status IS NULL OR ih.status = :status) AND " +
           "(:userId IS NULL OR ih.userId = :userId) AND " +
           "(:startTime IS NULL OR ih.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR ih.createdAt <= :endTime) " +
           "ORDER BY ih.createdAt DESC")
    Page<InferenceHistory> searchInferenceHistory(
            @Param("keyword") String keyword,
            @Param("inferenceType") String inferenceType,
            @Param("modelName") String modelName,
            @Param("status") String status,
            @Param("userId") UUID userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 统计查询
     */
    
    // 统计总推理次数
    @Query("SELECT COUNT(ih) FROM InferenceHistory ih WHERE ih.isDeleted = false")
    Long countTotalInferences();

    // 统计成功推理次数
    @Query("SELECT COUNT(ih) FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.status = 'SUCCESS'")
    Long countSuccessfulInferences();

    // 统计失败推理次数
    @Query("SELECT COUNT(ih) FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.status = 'FAILED'")
    Long countFailedInferences();

    // 统计用户推理次数
    @Query("SELECT COUNT(ih) FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.userId = :userId")
    Long countInferencesByUser(@Param("userId") UUID userId);

    // 统计各模型使用次数
    @Query("SELECT ih.modelName, COUNT(ih) FROM InferenceHistory ih WHERE ih.isDeleted = false GROUP BY ih.modelName")
    List<Object[]> countInferencesByModel();

    // 统计各推理类型使用次数
    @Query("SELECT ih.inferenceType, COUNT(ih) FROM InferenceHistory ih WHERE ih.isDeleted = false GROUP BY ih.inferenceType")
    List<Object[]> countInferencesByType();

    // 统计每日推理次数（最近30天）
    @Query("SELECT DATE(ih.createdAt), COUNT(ih) FROM InferenceHistory ih " +
           "WHERE ih.isDeleted = false AND ih.createdAt >= :startDate " +
           "GROUP BY DATE(ih.createdAt) ORDER BY DATE(ih.createdAt)")
    List<Object[]> countDailyInferences(@Param("startDate") LocalDateTime startDate);

    // 计算平均处理时间
    @Query("SELECT AVG(ih.processingTime) FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.status = 'SUCCESS'")
    Double getAverageProcessingTime();

    // 计算平均检测目标数量
    @Query("SELECT AVG(ih.detectedObjectsCount) FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.status = 'SUCCESS'")
    Double getAverageDetectedObjectsCount();

    // 查找最近的推理记录
    @Query("SELECT ih FROM InferenceHistory ih WHERE ih.isDeleted = false ORDER BY ih.createdAt DESC")
    List<InferenceHistory> findRecentInferences(Pageable pageable);

    // 查找处理时间最长的推理记录
    @Query("SELECT ih FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.status = 'SUCCESS' ORDER BY ih.processingTime DESC")
    List<InferenceHistory> findSlowestInferences(Pageable pageable);

    // 查找检测目标最多的推理记录
    @Query("SELECT ih FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.status = 'SUCCESS' ORDER BY ih.detectedObjectsCount DESC")
    List<InferenceHistory> findMostDetectedInferences(Pageable pageable);

    // 根据评分查找
    Page<InferenceHistory> findByResultRatingAndIsDeletedFalseOrderByCreatedAtDesc(Integer rating, Pageable pageable);

    // 查找高评分推理记录
    @Query("SELECT ih FROM InferenceHistory ih WHERE ih.isDeleted = false AND ih.resultRating >= :minRating ORDER BY ih.resultRating DESC, ih.createdAt DESC")
    Page<InferenceHistory> findHighRatedInferences(@Param("minRating") Integer minRating, Pageable pageable);

    // 软删除（批量）
    @Query("UPDATE InferenceHistory ih SET ih.isDeleted = true, ih.updatedAt = CURRENT_TIMESTAMP WHERE ih.id IN :ids")
    void softDeleteByIds(@Param("ids") List<UUID> ids);

    // 恢复软删除的记录
    @Query("UPDATE InferenceHistory ih SET ih.isDeleted = false, ih.updatedAt = CURRENT_TIMESTAMP WHERE ih.id = :id")
    void restoreById(@Param("id") UUID id);

    // 清理旧记录（物理删除超过指定天数的记录）
    @Query("DELETE FROM InferenceHistory ih WHERE ih.isDeleted = true AND ih.updatedAt < :cutoffDate")
    void cleanupOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);
}