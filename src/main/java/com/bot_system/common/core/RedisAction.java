package com.bot_system.common.core;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @className: RedisAction
 * @author: Java之父
 * @date: 2025/10/5 18:59
 * @version: 1.0.0
 * @description: redis操作
 */
@Slf4j
@Component
public class RedisAction {
    @Resource
    private RedisTemplate<String, String> redisTemplate;
}
