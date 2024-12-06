package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Invocable;
import java.util.Map;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 评分计算控制器
 * 
 * 提供评分计算的REST接口，支持异步处理和动态JavaScript脚本执行
 */
@Slf4j
@RestController
@RequestMapping("/compute")
public class ScaleController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    /** 远程脚本URL基础地址 */
    private static final String SCRIPT_URL = "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/scriptes";
    
    /** JavaScript引擎实例 */
    private final ScriptEngine scriptEngine;
    
    /** 脚本缓存，key为脚本ID，value为缓存对象 */
    private final Map<String, ScriptCache> scriptCache;
    
    /** 缓存过期时间（小时） */
    private static final long CACHE_EXPIRE_HOURS = 1;

    /**
     * 脚本缓存对象
     */
    @Data
    private static class ScriptCache {
        /** 脚本内容 */
        private final String content;
        /** 缓存时间 */
        private final LocalDateTime cacheTime;
        
        /** 判断缓存是否过期 */
        public boolean isExpired() {
            return LocalDateTime.now().minusHours(CACHE_EXPIRE_HOURS).isAfter(cacheTime);
        }
    }

    public ScaleController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        this.scriptCache = new ConcurrentHashMap<>();
        
        if (this.scriptEngine == null) {
            throw new RuntimeException("JavaScript引擎初始化失败");
        }
    }

    /**
     * 获取脚本内容，优先从缓存获取
     */
    private String getScript(String scriptId) {
        ScriptCache cache = scriptCache.get(scriptId);
        
        // 如果缓存不存在或已过期，则重新获取
        if (cache == null || cache.isExpired()) {
            String scriptContent = restTemplate.getForObject(
                SCRIPT_URL + "/" + scriptId + ".js", 
                String.class
            );
            
            cache = new ScriptCache(scriptContent, LocalDateTime.now());
            scriptCache.put(scriptId, cache);
            log.debug("更新脚本缓存: {}", scriptId);
        }
        
        return cache.getContent();
    }

    /**
     * 处理评分请求
     * 
     * @param request 包含ID和问题答案的请求对象
     * @return 异步响应，包含计算结果
     */
    @PostMapping
    @Async
    public CompletableFuture<Response> processItems(@RequestBody ComputeRequest request) {
        log.info("收到请求，ID: {}", request.getId());
        log.debug("请求数据: {}", request.getItems());
        
        try {
            // 获取脚本内容（优先从缓存获取）
            String scriptContent = getScript(request.getId());
            
            // 将请求数据转换为JSON字符串
            String itemsJson = objectMapper.writeValueAsString(request.getItems());
            
            // 执行脚本
            synchronized (scriptEngine) {
                scriptEngine.eval(scriptContent);
                Invocable invocable = (Invocable) scriptEngine;
                Object result = invocable.invokeFunction("scoreCalculator", itemsJson);
                return CompletableFuture.completedFuture(Response.success(result));
            }
            
        } catch (Exception e) {
            log.error("处理脚本出错，ID: {}, 错误: {}", request.getId(), e.getMessage());
            return CompletableFuture.completedFuture(Response.error("脚本处理失败: " + e.getMessage()));
        }
    }
} 