package com.bot_system.common.core;

import com.alibaba.fastjson2.JSON;
import com.bot_system.model.pojo.BotSessionTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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

    private static final String BOT_SESSION_TASK = "botSessionTask";

    /**
     * 设置会话任务（默认60s过期）
     * @param telegramId 飞机号
     * @param task        任务
     */
    public void setBotSessionTask(Long telegramId, BotSessionTask task) {
        redisTemplate.opsForValue().set(BOT_SESSION_TASK + ":" + telegramId, JSON.toJSONString(task), 60, TimeUnit.SECONDS);
    }

    /**
     * 获取会话任务
     * @param telegramId 飞机号
     * @return            任务
     */
    public BotSessionTask getBoeSessionTask(Long telegramId) {
        String json = redisTemplate.opsForValue().get(BOT_SESSION_TASK + ":" + telegramId);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, BotSessionTask.class);
    }

    /**
     * 删除会话任务
     * @param telegramId 飞机号
     */
    public void deleteBotSessionTask(Long telegramId) {
        redisTemplate.delete(BOT_SESSION_TASK + ":" + telegramId);
    }
}
