package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/compute")
public class ScaleController {

    @PostMapping
    public Response processItems(@RequestBody ComputeRequest request) {
        log.info("Received request with id: {}", request.getId());
        log.debug("Request items: {}", request.getItems());
        
        return Response.success(Map.of(
            "id", request.getId(),
            "itemsCount", request.getItems().size()
        ));
    }
} 