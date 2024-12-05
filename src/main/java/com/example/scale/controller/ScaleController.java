package com.example.scale.controller;

import com.example.scale.entity.ComputeRequest;
import com.example.scale.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/compute")
public class ScaleController {

    private final RestTemplate restTemplate;
    private static final String SCRIPT_URL = "https://purre-green-1309961435.cos.ap-chengdu.myqcloud.com/Scale/scriptes";

    public ScaleController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public Response processItems(@RequestBody ComputeRequest request) {
        log.info("Received request with id: {}", request.getId());
        log.debug("Request items: {}", request.getItems());
        
        try {
            String scriptContent = restTemplate.getForObject(SCRIPT_URL + "/" + request.getId() + ".js", String.class);
            log.debug("Retrieved script content: {}", scriptContent);
            
            // TODO: Process the script content
            
            return Response.success(Map.of(
                "id", request.getId(),
                "itemsCount", request.getItems().size(),
                "script", scriptContent
            ));
        } catch (Exception e) {
            log.error("Error fetching script for id {}: {}", request.getId(), e.getMessage());
            return Response.error("Failed to fetch script: " + e.getMessage());
        }
    }
} 