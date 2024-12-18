package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import com.example.scale.service.ComputeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

/**
 * 评分计算控制器
 * 提供评分计算的REST接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ScaleController {
    
    /** 计算服务 */
    private final ComputeService computeService;
    
    /**
     * 处理评分计算请求
     * 
     * @param request 包含ID和问题答案的请求对象
     * @return 异步响应，包含计算结果
     */
    @PostMapping("/compute")
    public CompletableFuture<Response> processItems(@RequestBody ComputeRequest request) {
        log.debug("接收到评分计算请求, ID: {}", request.getId());
        return computeService.processItems(request);
    }
} 