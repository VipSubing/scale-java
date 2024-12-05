package com.example.scale.entity;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class Response {
    private Object result;
    private Integer code;
    private String msg;

    public static Response success(Object result) {
        return Response.builder()
                .result(result)
                .code(200)
                .msg("success")
                .build();
    }

    public static Response error(String msg) {
        return Response.builder()
                .code(500)
                .msg(msg)
                .build();
    }
} 