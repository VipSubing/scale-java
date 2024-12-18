package com.example.scale.service;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ComputeService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Environment environment;
    
    /** 远程脚本URL基础地址 */
    private static final String SCRIPT_URL = "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/scriptes";
    
    /** JavaScript引擎实例 */
    private final ScriptEngine scriptEngine;
    
    /** 脚本缓存，key为脚本ID，value为缓存对象 */
    private final Map<String, ScriptCache> scriptCache;
    
    /** 缓存过期时间（小时） */
    private static final long CACHE_EXPIRE_HOURS = 1;

    public ComputeService(RestTemplate restTemplate, ObjectMapper objectMapper, Environment environment) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.environment = environment;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        this.scriptCache = new ConcurrentHashMap<>();
        
        if (this.scriptEngine == null) {
            throw new RuntimeException("JavaScript引擎初始化失败");
        }
    }

    /**
     * 处理评分请求
     */
    @Async
    public CompletableFuture<Response> processItems(ComputeRequest request) {
        log.info("收到请求，ID: {}", request.getId());
        log.debug("请求数据: {}", request.getItems());
    
        try {
            String scriptContent = getScript(request.getId());
            String itemsJson = objectMapper.writeValueAsString(request.getItems());
            
            synchronized (scriptEngine) {
                scriptEngine.eval(scriptContent);
                Invocable invocable = (Invocable) scriptEngine;
                Object result = invocable.invokeFunction("scoreCalculator", itemsJson);
                log.info("计算结果: {}", result);
                return CompletableFuture.completedFuture(Response.success(result));
            }
        } catch (Exception e) {
            log.error("处理脚本出错，ID: {}, 错误: {}", request.getId(), e.getMessage());
            return CompletableFuture.completedFuture(Response.error("脚本处理失败: " + e.getMessage()));
        }
    }

    /**
     * 获取脚本内容，开发环境不使用缓存
     */
    private String getScript(String scriptId) {
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        
        if (isDev) {
            log.debug("开发环境，直接获取脚本: {}", scriptId);
            return restTemplate.getForObject(
                SCRIPT_URL + "/" + scriptId + ".js", 
                String.class
            );
        }
        
        ScriptCache cache = scriptCache.get(scriptId);
        
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
     * 脚本缓存对象
     */
    @Data
    private static class ScriptCache {
        private final String content;
        private final LocalDateTime cacheTime;
        
        public boolean isExpired() {
            return LocalDateTime.now().minusHours(CACHE_EXPIRE_HOURS).isAfter(cacheTime);
        }
    }
} 