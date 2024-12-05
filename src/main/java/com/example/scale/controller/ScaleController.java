package com.example.scale.controller;

import com.example.scale.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/scale/compute")
public class ScaleController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> processItems(@RequestBody Map<String, List<Item>> request) {
        log.info("Received request with {} items", request.get("items").size());
        
        List<Item> items = request.get("items");
        items.forEach(item -> log.debug("Processing item: {}", item));
        
        log.info("Items processed successfully");
        return ResponseEntity.ok(Map.of(
            "message", "Items processed successfully", 
            "count", items.size()
        ));
    }
} 