package com.vision.vision_platform_backend.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * AIInferenceDto 单元测试
 */
@DisplayName("AIInferenceDto 测试")
public class AIInferenceDtoTest {

    @Nested
    @DisplayName("InferenceRequest 测试")
    class InferenceRequestTest {
        
        private AIInferenceDto.InferenceRequest request;
        
        @BeforeEach
        void setUp() {
            request = new AIInferenceDto.InferenceRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(request);
            assertNull(request.getTask());
            assertNull(request.getModelName());
            assertNull(request.getImageData());
            assertNull(request.getConfidenceThreshold());
            assertNull(request.getIouThreshold());
            assertNull(request.getMaxDetections());
            assertNull(request.getImageFormat());
            assertNull(request.getReturnVisualization());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            request.setTask("detection");
            request.setModelName("yolov8n");
            request.setImageData("base64data");
            request.setConfidenceThreshold(0.5);
            request.setIouThreshold(0.45);
            request.setMaxDetections(100);
            request.setImageFormat("jpg");
            request.setReturnVisualization(true);
            
            assertEquals("detection", request.getTask());
            assertEquals("yolov8n", request.getModelName());
            assertEquals("base64data", request.getImageData());
            assertEquals(0.5, request.getConfidenceThreshold());
            assertEquals(0.45, request.getIouThreshold());
            assertEquals(100, request.getMaxDetections());
            assertEquals("jpg", request.getImageFormat());
            assertTrue(request.getReturnVisualization());
        }
        
        @Test
        @DisplayName("完整推理请求场景")
        void testCompleteInferenceRequest() {
            request.setTask("segmentation");
            request.setModelName("yolov8n-seg");
            request.setImageData("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
            request.setConfidenceThreshold(0.6);
            request.setIouThreshold(0.5);
            request.setMaxDetections(50);
            request.setImageFormat("png");
            request.setReturnVisualization(false);
            
            assertNotNull(request.getTask());
            assertNotNull(request.getModelName());
            assertNotNull(request.getImageData());
            assertTrue(request.getConfidenceThreshold() > 0);
            assertTrue(request.getIouThreshold() > 0);
            assertTrue(request.getMaxDetections() > 0);
            assertNotNull(request.getImageFormat());
            assertNotNull(request.getReturnVisualization());
        }
    }
    
    @Nested
    @DisplayName("BatchInferenceRequest 测试")
    class BatchInferenceRequestTest {
        
        private AIInferenceDto.BatchInferenceRequest batchRequest;
        
        @BeforeEach
        void setUp() {
            batchRequest = new AIInferenceDto.BatchInferenceRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(batchRequest);
            assertNull(batchRequest.getTask());
            assertNull(batchRequest.getModelName());
            assertNull(batchRequest.getImageDataList());
            assertNull(batchRequest.getConfidenceThreshold());
            assertNull(batchRequest.getIouThreshold());
            assertNull(batchRequest.getMaxDetections());
            assertNull(batchRequest.getImageFormat());
            assertNull(batchRequest.getReturnVisualization());
            assertNull(batchRequest.getBatchSize());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            List<String> imageDataList = Arrays.asList("image1", "image2", "image3");
            
            batchRequest.setTask("detection");
            batchRequest.setModelName("yolov8n");
            batchRequest.setImageDataList(imageDataList);
            batchRequest.setConfidenceThreshold(0.5);
            batchRequest.setIouThreshold(0.45);
            batchRequest.setMaxDetections(100);
            batchRequest.setImageFormat("jpg");
            batchRequest.setReturnVisualization(true);
            batchRequest.setBatchSize(3);
            
            assertEquals("detection", batchRequest.getTask());
            assertEquals("yolov8n", batchRequest.getModelName());
            assertEquals(imageDataList, batchRequest.getImageDataList());
            assertEquals(0.5, batchRequest.getConfidenceThreshold());
            assertEquals(0.45, batchRequest.getIouThreshold());
            assertEquals(100, batchRequest.getMaxDetections());
            assertEquals("jpg", batchRequest.getImageFormat());
            assertTrue(batchRequest.getReturnVisualization());
            assertEquals(3, batchRequest.getBatchSize());
        }
    }
    
    @Nested
    @DisplayName("DetectionResult 测试")
    class DetectionResultTest {
        
        private AIInferenceDto.DetectionResult detection;
        
        @BeforeEach
        void setUp() {
            detection = new AIInferenceDto.DetectionResult();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(detection);
            assertNull(detection.getClassId());
            assertNull(detection.getClassName());
            assertNull(detection.getConfidence());
            assertNull(detection.getBbox());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            List<Double> bbox = Arrays.asList(10.0, 20.0, 100.0, 200.0);
            
            detection.setClassId(1);
            detection.setClassName("person");
            detection.setConfidence(0.95);
            detection.setBbox(bbox);
            
            assertEquals(1, detection.getClassId());
            assertEquals("person", detection.getClassName());
            assertEquals(0.95, detection.getConfidence());
            assertEquals(bbox, detection.getBbox());
        }
        
        @Test
        @DisplayName("检测结果有效性")
        void testDetectionResultValidity() {
            List<Double> bbox = Arrays.asList(50.0, 60.0, 150.0, 160.0);
            
            detection.setClassId(0);
            detection.setClassName("car");
            detection.setConfidence(0.87);
            detection.setBbox(bbox);
            
            assertTrue(detection.getClassId() >= 0);
            assertNotNull(detection.getClassName());
            assertTrue(detection.getConfidence() >= 0 && detection.getConfidence() <= 1);
            assertEquals(4, detection.getBbox().size());
        }
    }
    
    @Nested
    @DisplayName("SegmentationResult 测试")
    class SegmentationResultTest {
        
        private AIInferenceDto.SegmentationResult segmentation;
        
        @BeforeEach
        void setUp() {
            segmentation = new AIInferenceDto.SegmentationResult();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(segmentation);
            assertNull(segmentation.getClassId());
            assertNull(segmentation.getClassName());
            assertNull(segmentation.getConfidence());
            assertNull(segmentation.getMask());
            assertNull(segmentation.getBbox());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            List<Double> bbox = Arrays.asList(10.0, 20.0, 100.0, 200.0);
            
            segmentation.setClassId(2);
            segmentation.setClassName("dog");
            segmentation.setConfidence(0.92);
            segmentation.setMask("mask_data");
            segmentation.setBbox(bbox);
            
            assertEquals(2, segmentation.getClassId());
            assertEquals("dog", segmentation.getClassName());
            assertEquals(0.92, segmentation.getConfidence());
            assertEquals("mask_data", segmentation.getMask());
            assertEquals(bbox, segmentation.getBbox());
        }
    }
    
    @Nested
    @DisplayName("InferenceResponse 测试")
    class InferenceResponseTest {
        
        private AIInferenceDto.InferenceResponse response;
        
        @BeforeEach
        void setUp() {
            response = new AIInferenceDto.InferenceResponse();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(response);
            assertNull(response.getSuccess());
            assertNull(response.getTask());
            assertNull(response.getModelName());
            assertNull(response.getInferenceTime());
            assertNull(response.getImageSize());
            assertNull(response.getDetections());
            assertNull(response.getSegmentations());
            assertNull(response.getVisualizationImage());
            assertNull(response.getErrorMessage());
            assertNull(response.getTimestamp());
        }
        
        @Test
        @DisplayName("成功响应")
        void testSuccessfulResponse() {
            List<Integer> imageSize = Arrays.asList(640, 480);
            List<AIInferenceDto.DetectionResult> detections = Arrays.asList(new AIInferenceDto.DetectionResult());
            LocalDateTime timestamp = LocalDateTime.now();
            
            response.setSuccess(true);
            response.setTask("detection");
            response.setModelName("yolov8n");
            response.setInferenceTime(0.15);
            response.setImageSize(imageSize);
            response.setDetections(detections);
            response.setVisualizationImage("base64_image");
            response.setTimestamp(timestamp);
            
            assertTrue(response.getSuccess());
            assertEquals("detection", response.getTask());
            assertEquals("yolov8n", response.getModelName());
            assertEquals(0.15, response.getInferenceTime());
            assertEquals(imageSize, response.getImageSize());
            assertEquals(detections, response.getDetections());
            assertEquals("base64_image", response.getVisualizationImage());
            assertEquals(timestamp, response.getTimestamp());
            assertNull(response.getErrorMessage());
        }
        
        @Test
        @DisplayName("失败响应")
        void testFailedResponse() {
            LocalDateTime timestamp = LocalDateTime.now();
            
            response.setSuccess(false);
            response.setTask("detection");
            response.setModelName("yolov8n");
            response.setErrorMessage("Model not found");
            response.setTimestamp(timestamp);
            
            assertFalse(response.getSuccess());
            assertEquals("detection", response.getTask());
            assertEquals("yolov8n", response.getModelName());
            assertEquals("Model not found", response.getErrorMessage());
            assertEquals(timestamp, response.getTimestamp());
            assertNull(response.getInferenceTime());
            assertNull(response.getDetections());
        }
    }
    
    @Nested
    @DisplayName("ModelInfo 测试")
    class ModelInfoTest {
        
        private AIInferenceDto.ModelInfo modelInfo;
        
        @BeforeEach
        void setUp() {
            modelInfo = new AIInferenceDto.ModelInfo();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(modelInfo);
            assertNull(modelInfo.getName());
            assertNull(modelInfo.getType());
            assertNull(modelInfo.getDescription());
            assertNull(modelInfo.getVersion());
            assertNull(modelInfo.getInputSize());
            assertNull(modelInfo.getClassNames());
            assertNull(modelInfo.getIsLoaded());
            assertNull(modelInfo.getLoadTime());
            assertNull(modelInfo.getModelSize());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            List<Integer> inputSize = Arrays.asList(640, 640);
            List<String> classNames = Arrays.asList("person", "car", "dog");
            LocalDateTime loadTime = LocalDateTime.now();
            
            modelInfo.setName("yolov8n");
            modelInfo.setType("detection");
            modelInfo.setDescription("YOLOv8 nano model");
            modelInfo.setVersion("1.0.0");
            modelInfo.setInputSize(inputSize);
            modelInfo.setClassNames(classNames);
            modelInfo.setIsLoaded(true);
            modelInfo.setLoadTime(loadTime);
            modelInfo.setModelSize(6291456L);
            
            assertEquals("yolov8n", modelInfo.getName());
            assertEquals("detection", modelInfo.getType());
            assertEquals("YOLOv8 nano model", modelInfo.getDescription());
            assertEquals("1.0.0", modelInfo.getVersion());
            assertEquals(inputSize, modelInfo.getInputSize());
            assertEquals(classNames, modelInfo.getClassNames());
            assertTrue(modelInfo.getIsLoaded());
            assertEquals(loadTime, modelInfo.getLoadTime());
            assertEquals(6291456L, modelInfo.getModelSize());
        }
    }
    
    @Nested
    @DisplayName("InferenceStats 测试")
    class InferenceStatsTest {
        
        private AIInferenceDto.InferenceStats stats;
        
        @BeforeEach
        void setUp() {
            stats = new AIInferenceDto.InferenceStats();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(stats);
            assertNull(stats.getTotalRequests());
            assertNull(stats.getSuccessfulRequests());
            assertNull(stats.getFailedRequests());
            assertNull(stats.getAverageInferenceTime());
            assertNull(stats.getTotalInferenceTime());
            assertNull(stats.getStartTime());
            assertNull(stats.getLastRequestTime());
            assertNull(stats.getRequestsPerMinute());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            LocalDateTime startTime = LocalDateTime.now().minusHours(1);
            LocalDateTime lastRequestTime = LocalDateTime.now();
            
            stats.setTotalRequests(100L);
            stats.setSuccessfulRequests(95L);
            stats.setFailedRequests(5L);
            stats.setAverageInferenceTime(0.15);
            stats.setTotalInferenceTime(15.0);
            stats.setStartTime(startTime);
            stats.setLastRequestTime(lastRequestTime);
            stats.setRequestsPerMinute(1.67);
            
            assertEquals(100L, stats.getTotalRequests());
            assertEquals(95L, stats.getSuccessfulRequests());
            assertEquals(5L, stats.getFailedRequests());
            assertEquals(0.15, stats.getAverageInferenceTime());
            assertEquals(15.0, stats.getTotalInferenceTime());
            assertEquals(startTime, stats.getStartTime());
            assertEquals(lastRequestTime, stats.getLastRequestTime());
            assertEquals(1.67, stats.getRequestsPerMinute());
        }
        
        @Test
        @DisplayName("统计数据一致性")
        void testStatsConsistency() {
            stats.setTotalRequests(100L);
            stats.setSuccessfulRequests(95L);
            stats.setFailedRequests(5L);
            
            assertEquals(stats.getTotalRequests(), 
                        stats.getSuccessfulRequests() + stats.getFailedRequests());
        }
    }
    
    @Nested
    @DisplayName("ModelLoadRequest 测试")
    class ModelLoadRequestTest {
        
        private AIInferenceDto.ModelLoadRequest loadRequest;
        
        @BeforeEach
        void setUp() {
            loadRequest = new AIInferenceDto.ModelLoadRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(loadRequest);
            assertNull(loadRequest.getModelName());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            loadRequest.setModelName("yolov8n");
            assertEquals("yolov8n", loadRequest.getModelName());
        }
    }
    
    @Nested
    @DisplayName("ModelUnloadRequest 测试")
    class ModelUnloadRequestTest {
        
        private AIInferenceDto.ModelUnloadRequest unloadRequest;
        
        @BeforeEach
        void setUp() {
            unloadRequest = new AIInferenceDto.ModelUnloadRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(unloadRequest);
            assertNull(unloadRequest.getModelName());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            unloadRequest.setModelName("yolov8n");
            assertEquals("yolov8n", unloadRequest.getModelName());
        }
    }
    
    @Nested
    @DisplayName("ConfigUpdateRequest 测试")
    class ConfigUpdateRequestTest {
        
        private AIInferenceDto.ConfigUpdateRequest configRequest;
        
        @BeforeEach
        void setUp() {
            configRequest = new AIInferenceDto.ConfigUpdateRequest();
        }
        
        @Test
        @DisplayName("默认构造函数")
        void testDefaultConstructor() {
            assertNotNull(configRequest);
            assertNull(configRequest.getSection());
            assertNull(configRequest.getValues());
        }
        
        @Test
        @DisplayName("Setter和Getter方法")
        void testSettersAndGetters() {
            Map<String, Object> values = new HashMap<>();
            values.put("confidence_threshold", 0.5);
            values.put("max_detections", 100);
            
            configRequest.setSection("inference");
            configRequest.setValues(values);
            
            assertEquals("inference", configRequest.getSection());
            assertEquals(values, configRequest.getValues());
        }
    }
}