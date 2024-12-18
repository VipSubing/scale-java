package com.example.scale.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

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
    
    /** RestTemplate用于发送HTTP请求 */
    private final RestTemplate restTemplate;
    
    /**
     * 获取JSON数据
     * 使用Spring Cache缓存结果
     */
    @Cacheable(value = "jsonData")
    public String getCachedJsonData() {
        log.debug("从远程获取JSON数据");
        try {
            String data = restTemplate.getForObject(REMOTE_JSON_URL, String.class);
            return data != null ? data : "[]";
        } catch (Exception e) {
            log.error("远程JSON数据加载失败: {}", e.getMessage(), e);
            return "[]";
        }
    }

    /**
     * 定时清除缓存
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    @CacheEvict(value = "jsonData", allEntries = true)
    public void refreshCache() {
        log.debug("清除JSON数据缓存");
    }
} 