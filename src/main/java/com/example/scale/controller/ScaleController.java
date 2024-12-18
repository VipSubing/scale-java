package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import com.example.scale.service.ComputeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/compute")
public class ScaleController {
    
    private final ComputeService computeService;

    public ScaleController(ComputeService computeService) {
        this.computeService = computeService;
    }

    @PostMapping
    public CompletableFuture<Response> processItems(@RequestBody ComputeRequest request) {
        return computeService.processItems(request);
    }
} 