package com.cultureradar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.Executor;

/**
 * Configuration class for asynchronous task execution and scheduled tasks.
 * This allows the application to perform background operations like periodic
 * API calls to external event sources without blocking the main application thread.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    
    @Value("${spring.task.execution.pool.core-size:5}")
    private int corePoolSize;
    
    @Value("${spring.task.execution.pool.max-size:10}")
    private int maxPoolSize;
    
    @Value("${spring.task.execution.pool.queue-capacity:25}")
    private int queueCapacity;
    
    /**
     * Configures the task executor used for async operations.
     * @return Executor instance with configured thread pool parameters
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("CultureRadar-Async-");
        executor.setAwaitTerminationSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
