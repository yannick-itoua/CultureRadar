package com.cultureradar.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration class for caching.
 * This improves performance by caching frequently accessed data,
 * such as events, locations, and responses from external APIs.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures the cache manager with specific cache names.
     * 
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
            "events", 
            "eventsByCity",
            "eventsByCategory",
            "locations",
            "googleMapsResults",
            "eventbriteResults"
        ));
        return cacheManager;
    }
}