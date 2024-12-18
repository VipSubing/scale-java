package com.example.scale.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.Getter;

/**
 * JSON文件处理服务
 * 提供JSON文件的读取和缓存功能
 */
@Slf4j
@Service
public class JsonService {
    
    /** JSON数据缓存 */
    @Getter
    private String cachedJsonData;
    
    /** JSON文件路径 */
    @Value("${json.file.path:/app/items.json}")
    private String jsonFilePath;
    
    /**
     * 服务启动时初始化JSON数据
     */
    @PostConstruct
    public void init() {
        loadJsonData();
    }
    
    /**
     * 从文件加载JSON数据到内存
     */
    private void loadJsonData() {
        try {
            cachedJsonData = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            log.info("JSON数据加载成功，文件路径: {}", jsonFilePath);
        } catch (Exception e) {
            cachedJsonData = "{}";
            log.error("JSON文件加载失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 定时刷新JSON数据
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void refreshJsonData() {
        log.debug("开始刷新JSON数据...");
        loadJsonData();
    }
} 