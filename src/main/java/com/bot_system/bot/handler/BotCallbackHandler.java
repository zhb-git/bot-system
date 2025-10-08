package com.bot_system.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @className: BotCallbackHandler
 * @author: Java之父
 * @date: 2025/10/4 22:03
 * @version: 1.0.0
 * @description: 按钮回调处理
 */
@Slf4j
@Component
public class BotCallbackHandler {
    public BotApiMethod<?> handler(CallbackQuery callbackQuery) throws TelegramApiException {
        return null;
    }
}
