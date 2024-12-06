package com.example.scale.entity;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * 统一响应对象
 * 
 * 用于封装API响应数据，包含状态码、消息和数据
 */
@Data
@AllArgsConstructor
public class Response {
    /** 响应状态码 */
    private int code;
    /** 响应消息 */
    private String message;
    /** 响应数据 */
    private Object data;

    /**
     * 创建成功响应
     * @param data 响应数据
     * @return Response对象
     */
    public static Response success(Object data) {
        return new Response(200, "success", data);
    }

    /**
     * 创建错误响应
     * @param message 错误消息
     * @return Response对象
     */
    public static Response error(String message) {
        return new Response(500, message, null);
    }
} 