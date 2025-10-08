package com.bot_system.bot.dispatch;

import com.bot_system.bot.handler.BotCallbackHandler;
import com.bot_system.bot.handler.BotInlineHandler;
import com.bot_system.bot.handler.BotMessageHandler;
import com.bot_system.exception.BotException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    @Resource
    private BotCallbackHandler botCallbackHandler;

    @Resource
    private BotInlineHandler botInlineHandler;

    @Resource
    private BotMessageHandler botMessageHandler;

    public BotApiMethod<?> dispatch(Update update) {
        try {
            if (update.hasMessage()) {
                return botMessageHandler.handler(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                return botCallbackHandler.handler(update.getCallbackQuery());
            } else if (update.hasInlineQuery()) {
                return botInlineHandler.handler(update.getInlineQuery());
            }
        } catch (TelegramApiException e) {
            throw new BotException("botApi执行异常", e);
        }
        return null;
    }
}
