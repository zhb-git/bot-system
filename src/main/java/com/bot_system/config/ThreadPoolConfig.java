package com.bot_system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @className: TelegramBotConfig
 * @author: Java之父
 * @date: 2025/10/4 21:47
 * @version: 1.0.0
 * @description: 线程池配置
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {
    /**
     * 异步任务线程池
     * Bean 名必须为 "taskExecutor"，否则 @Async 不会自动识别。
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 平台线程模式
        // 合理的线程池参数计算（可根据业务场景调优）
        int corePoolSize = cpuCores * 2;      // 常驻线程数
        int maxPoolSize = cpuCores * 50;      // 最大线程数（高并发上限）
        int queueCapacity = cpuCores * 100;   // 任务排队队列容量
        int keepAliveSeconds = 60;            // 非核心线程最大空闲时间（秒）

        log.info("【Async配置】平台线程模式：cpuCores={}，core={}，max={}，queue={}", cpuCores, corePoolSize, maxPoolSize, queueCapacity);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 长轮询专用线程池
     * 用于处理轮询接收到的消息、更新等逻辑
     */
    @Bean(name = "longPollExecutor")
    public Executor longPollExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 长轮询线程池策略：消息 IO 密集型，适度放大核心线程数
        int corePoolSize = Math.max(4, cpuCores * 3);   // 常驻线程（消息处理并发）
        int maxPoolSize = cpuCores * 20;                // 最大线程数（极端突发）
        int queueCapacity = cpuCores * 200;             // 等待队列容量（防止积压消息）
        int keepAliveSeconds = 90;                      // 非核心线程最大空闲时间

        log.info("【LongPoll线程池】初始化：core={}，max={}，queue={}，keepAlive={}s",
                corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("long-poll-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
