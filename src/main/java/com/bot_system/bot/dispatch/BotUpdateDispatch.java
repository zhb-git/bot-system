package com.bot_system.bot.dispatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @className: BotUpdateDispatch
 * @author: Java之父
 * @date: 2025/10/5 17:04
 * @version: 1.0.0
 * @description: 机器人更新分配
 */
@Slf4j
@Component
public class BotUpdateDispatch {
    public BotApiMethod<?> dispatch(Update update) {

    }
}
