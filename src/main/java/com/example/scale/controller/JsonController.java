package com.example.scale.controller;

import com.example.scale.entity.Response;
import com.example.scale.service.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

/**
 * JSON数据控制器
 * 提供JSON数据的访问接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class JsonController {
    
    /** JSON服务 */
    private final JsonService jsonService;
    
    /**
     * 获取推荐JSON数据
     * 
     * @return 统一响应格式的推荐JSON数据
     */
    @PostMapping("/pro-test")
    public Response getJsonData() {
        log.info("开始处理/pro-test JSON数据请求");
        Response response = jsonService.getCachedJsonData();
        log.info("处理/pro-test请求完成，响应数据: {}", response);
        return response;
    }

    /**
     * 获取所有测试JSON数据
     * 
     * @return 统一响应格式的所有测试JSON数据
     */
    @PostMapping("/all-test")
    public Response getAllTests() {
        log.info("开始处理/all-test JSON数据请求");
        Response response = jsonService.getAllTestsData();
        log.info("处理/all-test请求完成，响应数据: {}", response);
        return response;
    }

    /**
     * 刷新指定类型的缓存数据
     * 
     * @param type 缓存类型：all-所有测试数据, pro-推荐数据, script-脚本数据, 空值-所有缓存
     * @return 统一响应格式的刷新结果
     */
    @PostMapping("/refreshData")
    public Response refreshAllData(@RequestBody(required = false) String type) {
        log.info("接收到刷新缓存请求, type: {}", type);
        return jsonService.refreshCache(type);
    }
} 