package com.example.scale.service;

import com.example.scale.entity.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

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

    /**
     * 根据类型刷新缓存数据
     * 
     * @param type 缓存类型
     */
    public Response refreshCache(String type) {
        log.info(">>> 开始刷新缓存数据, type: {}", type);
        try {
            if (type == null) {
                // 刷新所有缓存
                clearAllCache();
                getCachedJsonData();
                getAllTestsData();
                log.info("<<< 所有缓存刷新成功");
                return Response.success("所有缓存刷新成功");
            }

            switch (type.toLowerCase()) {
                case "all":
                    clearAllTestCache();
                    getAllTestsData();
                    log.info("<<< 所有测试数据缓存刷新成功");
                    return Response.success("所有测试数据缓存刷新成功");
                
                case "pro":
                    clearRecommendCache();
                    getCachedJsonData();
                    log.info("<<< 推荐数据缓存刷新成功");
                    return Response.success("推荐数据缓存刷新成功");
                
                case "script":
                    clearScriptCache();
                    log.info("<<< 脚本缓存刷新成功");
                    return Response.success("脚本缓存刷新成功");
                
                default:
                    return Response.error("未知的缓存类型: " + type);
            }
        } catch (Exception e) {
            log.error("缓存刷新失败: {}", e.getMessage(), e);
            return Response.error("缓存刷新失败: " + e.getMessage());
        }
    }

    @CacheEvict(value = "allTestsData", allEntries = true)
    public void clearAllTestCache() {
        log.info("清除所有测试数据缓存");
    }

    @CacheEvict(value = "recommendData", allEntries = true)
    public void clearRecommendCache() {
        log.info("清除推荐数据缓存");
    }

    @CacheEvict(value = "scriptData", allEntries = true)
    public void clearScriptCache() {
        log.info("清除脚本缓存");
    }

    @CacheEvict(value = {"recommendData", "allTestsData", "scriptData"}, allEntries = true)
    public void clearAllCache() {
        log.info("清除所有缓存");
    }
} 