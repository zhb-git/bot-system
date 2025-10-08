package com.bot_system.bot.handler;

import com.bot_system.exception.BizException;
import com.bot_system.exception.BotActionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @className: BotMessageHandler
 * @author: Java之父
 * @date: 2025/10/4 22:03
 * @version: 1.0.0
 * @description: 消息处理
 */
@Slf4j
@Component
public class BotMessageHandler {
    public BotApiMethod<?> handler(Message message) throws TelegramApiException {
        if (message.hasText()) {
            if (message.getText().equals("1")) {
                throw new BizException("业务执行异常");
            } else if (message.getText().equals("2")) {
                throw new BotActionException("机器人操作异常");
            } else if (message.getText().equals("3")) {
                throw new RuntimeException("未知异常");
            }
        }
        return null;
    }
}
