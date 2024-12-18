package com.example.scale.controller;

import com.example.scale.entity.Response;
import com.example.scale.service.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/pro-test")
    public Response getJsonData() {
        log.debug("接收到/pro-test JSON数据请求");
        return jsonService.getCachedJsonData();
    }

    /**
     * 获取所有测试JSON数据
     * 
     * @return 统一响应格式的所有测试JSON数据
     */
    @GetMapping("/all-test")
    public Response getAllTests() {
        log.debug("接收到all-test JSON数据请求");
        return jsonService.getAllTestsData();
    }
} 