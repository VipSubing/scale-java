package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Invocable;
import java.util.concurrent.CompletableFuture;

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

    public ScaleController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
        log.info("Received request with id: {}", request.getId());
        log.debug("Request items: {}", request.getItems());
        
        try {
            // 获取远程JavaScript脚本
            String scriptContent = restTemplate.getForObject(SCRIPT_URL + "/" + request.getId() + ".js", String.class);
            
            // 将请求数据转换为JSON字符串
            String itemsJson = objectMapper.writeValueAsString(request.getItems());
            
            // 初始化JavaScript引擎
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            if (engine == null) {
                throw new RuntimeException("JavaScript engine not found");
            }
            
            // 执行脚本
            engine.eval(scriptContent);
            
            // 调用评分计算函数
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction("scoreCalculator", itemsJson);
            
            return CompletableFuture.completedFuture(Response.success(result));
            
        } catch (Exception e) {
            log.error("Error processing script for id {}: {}", request.getId(), e.getMessage());
            return CompletableFuture.completedFuture(Response.error("Failed to process script: " + e.getMessage()));
        }
    }
} 