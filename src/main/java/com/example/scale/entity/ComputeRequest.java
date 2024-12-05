package com.example.scale.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ComputeRequest {
    private JsonNode items;
    private String id;
} 