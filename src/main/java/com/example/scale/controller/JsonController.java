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
     * 获取JSON数据
     * 
     * @return 缓存的JSON数据
     */
    @GetMapping("/pro-test")
    public String getJsonData() {
        log.debug("接收到JSON数据请求");
        return jsonService.getCachedJsonData();
    }
} 