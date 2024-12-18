package com.example.scale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;

@Configuration
public class WebConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // 设置UTF-8编码
        restTemplate.getMessageConverters()
            .set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
} 