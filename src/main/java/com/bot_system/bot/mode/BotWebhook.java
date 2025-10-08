package com.bot_system.bot.mode;

import com.bot_system.bot.dispatch.BotUpdateDispatch;
import com.bot_system.common.core.BotErrorProcessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @className: BotWebhook
 * @author: Java之父
 * @date: 2025/10/5 16:32
 * @version: 1.0.0
 * @description: webhook模式
 */
@Slf4j
@Component
public class BotWebhook {
    @Resource
    private BotUpdateDispatch botUpdateDispatch;

    public BotApiMethod<?> webhook(@RequestBody Update update) {
        try {
            return botUpdateDispatch.dispatch(update);
        } catch (Exception e) {
            log.error("机器人执行异常：{}", e.getMessage());
            return BotErrorProcessor.process(update, e);
        }
    }
}
