package com.example.scale.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class CacheConfig {
    
    /**
     * 生产环境使用ConcurrentMapCacheManager
     */
    @Bean
    @Profile("prod")
    public CacheManager cacheManagerProd() {
        return new ConcurrentMapCacheManager();
    }
    
    /**
     * 开发环境使用NoOpCacheManager（不缓存）
     */
    @Bean
    @Profile("dev")
    public CacheManager cacheManagerDev() {
        return new NoOpCacheManager();
    }
} 