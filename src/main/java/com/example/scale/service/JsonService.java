package com.example.scale.service;

import com.example.scale.entity.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;

/**
 * JSON文件处理服务
 * 提供JSON数据的远程获取和缓存功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JsonService {
    
    /** 远程JSON数据URL */
    private static final String REMOTE_JSON_URL = 
        "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/recommands-test.json";
    
    /** 所有测试数据URL */
    private static final String ALL_TESTS_URL = 
        "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/all-tests.json";
    
    /** RestTemplate用于发送HTTP请求 */
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    /**
     * 获取推荐JSON数据
     * 使用Spring Cache缓存结果
     */
    @Cacheable(value = "recommendData")
    public Response getCachedJsonData() {
        log.info(">>> 缓存未命中，从远程获取推荐JSON数据");
        try {
            String data = restTemplate.getForObject(REMOTE_JSON_URL, String.class);
            log.info("<<< 远程推荐数据获取成功，将被缓存");
            Object jsonData = objectMapper.readValue(data != null ? data : "[]", Object.class);
            return Response.success(jsonData);
        } catch (Exception e) {
            log.error("远程推荐数据加载失败: {}", e.getMessage(), e);
            return Response.error("远程推荐数据加载失败");
        }
    }

    /**
     * 获取所有测试JSON数据
     * 使用Spring Cache缓存结果
     */
    @Cacheable(value = "allTestsData")
    public Response getAllTestsData() {
        log.info(">>> 缓存未命中，从远程获取所有测试数据");
        try {
            String data = restTemplate.getForObject(ALL_TESTS_URL, String.class);
            log.info("<<< 远程测试数据获取成功，将被缓存");
            Object jsonData = objectMapper.readValue(data != null ? data : "[]", Object.class);
            return Response.success(jsonData);
        } catch (Exception e) {
            log.error("远程测试数据加载失败: {}", e.getMessage(), e);
            return Response.error("远程测试数据加载失败");
        }
    }
} 