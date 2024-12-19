package com.example.scale.config;

import com.example.scale.entity.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Response handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("不支持的请求方法: {}", ex.getMessage());
        return new Response(405, "不支持的请求方法: " + ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleGeneralError(Exception ex) {
        log.error("服务器错误: ", ex);
        return new Response(500, "服务器内部错误", null);
    }
} 