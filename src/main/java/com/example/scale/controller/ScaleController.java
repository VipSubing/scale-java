package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Invocable;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/compute")
public class ScaleController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String SCRIPT_URL = "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/scriptes";

    public ScaleController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public Response processItems(@RequestBody ComputeRequest request) {
        log.info("Received request with id: {}", request.getId());
        log.debug("Request items: {}", request.getItems());
        
        try {
            String scriptContent = restTemplate.getForObject(SCRIPT_URL + "/" + request.getId() + ".js", String.class);
            log.debug("Retrieved script content: {}", scriptContent);
            
            // Convert items to JSON string
            String itemsJson = objectMapper.writeValueAsString(request.getItems());
            
            // Create JavaScript engine
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            if (engine == null) {
                throw new RuntimeException("JavaScript engine not found");
            }
            
            // Evaluate the script
            engine.eval(scriptContent);
            
            // Cast to Invocable and call the function
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction("scoreCalculator", itemsJson);
            
            return Response.success(Map.of(
                "id", request.getId(),
                "itemsCount", request.getItems().size(),
                "score", result
            ));
            
        } catch (Exception e) {
            log.error("Error processing script for id {}: {}", request.getId(), e.getMessage());
            return Response.error("Failed to process script: " + e.getMessage());
        }
    }
} 