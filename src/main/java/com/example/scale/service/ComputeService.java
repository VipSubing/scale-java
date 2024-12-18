package com.example.scale.service;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.concurrent.CompletableFuture;

/**
 * 评分计算服务
 * 提供评分计算和脚本缓存功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComputeService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    /** 远程脚本URL基础地址 */
    private static final String SCRIPT_URL = 
        "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/scriptes";
    
    /** JavaScript引擎实例 */
    private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

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
     * 获取脚本内容
     * 使用Spring Cache缓存结果
     */
    @Cacheable(value = "scriptData", key = "#scriptId")
    private String getScript(String scriptId) {
        log.info(">>> 缓存未命中，从远程获取脚本: {}", scriptId);
        try {
            String script = restTemplate.getForObject(
                SCRIPT_URL + "/" + scriptId + ".js", 
                String.class
            );
            log.info("<<< 远程脚本获取成功，将被缓存");
            return script != null ? script : "";
        } catch (Exception e) {
            log.error("远程脚本获取失败: {}", e.getMessage(), e);
            return "";
        }
    }
} 