package com.bot_system.bot.mode;

import com.bot_system.bot.dispatch.BotUpdateDispatch;
import com.bot_system.common.core.Bot;
import com.bot_system.common.core.BotErrorProcessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executor;

/**
 * @className: BotLongPoll
 * @author: Java之父
 * @date: 2025/10/5 16:48
 * @version: 1.0.0
 * @description: 长轮询模式
 */
@Slf4j
@Component
public class BotLongPoll implements LongPollingSingleThreadUpdateConsumer {
    @Resource
    private BotUpdateDispatch botUpdateDispatch;

    @Resource
    private Bot bot;

    @Resource(name = "longPollExecutor")
    private Executor longPollExecutor;

    @Override
    public void consume(Update update) {
        // 异步处理
        longPollExecutor.execute(() -> {
            try {
                BotApiMethod<?> apiMethod = botUpdateDispatch.dispatch(update);
                if (apiMethod != null) {
                    bot.getClient().execute(apiMethod);
                }
            } catch (Exception e) {
                log.error("机器人执行异常: {}", e.getMessage());
                BotApiMethod<?> apiMethod = BotErrorProcessor.process(update, e);
                try {
                    bot.getClient().execute(apiMethod);
                } catch (TelegramApiException ex) {
                    log.error("异常反馈失败", e);
                }
            }
        });
    }
}
