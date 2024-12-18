package com.example.scale.controller;

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
     * @return 缓存的推荐JSON数据
     */
    @GetMapping("/pro-test")
    public String getJsonData() {
        log.debug("接收到/pro-test JSON数据请求");
        return jsonService.getCachedJsonData();
    }

    /**
     * 获取所有测试JSON数据
     * 
     * @return 缓存的所有测试JSON数据
     */
    @GetMapping("/all-test")
    public String getAllTests() {
        log.debug("接收到all-test SON数据请求");
        return jsonService.getAllTestsData();
    }
} 