package com.bot_system.common.core;

import com.bot_system.annotation.BotFeedback;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import java.util.Collections;

/**
 * @className: BotErrorProcessor
 * @author: Java之父
 * @date: 2025/10/5 18:11
 * @version: 1.0.0
 * @description: 机器人异常处理器
 */
@Slf4j
public final class BotErrorProcessor {
    /**
     * 处理异常并生成 Telegram 返回结构
     * @param update Telegram Update
     * @param e 异常
     * @return BotApiMethod<?> 结构，可直接返回
     */
    public static BotApiMethod<?> process(Update update, Exception e) {
        String feedback = resolveMessage(e);

        // inline_query 模式
        if (update.hasInlineQuery()) {
            String queryId = update.getInlineQuery().getId();

            // 注意：results 必须存在，即使是空数组
            // 这里放一个“错误提示占位” InlineQueryResultArticle
            InlineQueryResultArticle result = InlineQueryResultArticle.builder()
                    .id("error_" + System.currentTimeMillis())
                    .title("出错啦 ⚠️")
                    .description(feedback)
                    .inputMessageContent(InputTextMessageContent.builder()
                            .messageText(feedback)
                            .build())
                    .build();

            return AnswerInlineQuery.builder()
                    .inlineQueryId(queryId)
                    .results(Collections.singletonList(result))
                    .cacheTime(0)
                    .isPersonal(true)
                    .build();
        }

        // 回调按钮反馈（弹窗）
        if (update.hasCallbackQuery()) {
            String callbackId = update.getCallbackQuery().getId();
            return AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackId)
                    .text(feedback)
                    // 弹出警告框而不是 toast
                    .showAlert(true)
                    .build();
        }

        // 普通消息反馈（群组 / 私聊）
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            Integer replyId = null;
            if (update.getMessage().isGroupMessage() || update.getMessage().isSuperGroupMessage()) {
                replyId = update.getMessage().getMessageId();
            }

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(feedback)
                    .build();

            if (replyId != null) {
                sendMessage.setReplyToMessageId(replyId);
            }

            return sendMessage;
        }

        // Inline 模式或未知情况
        log.warn("无法识别的异常反馈场景: {}", e.getMessage());
        return null;
    }

    /**
     * 判断异常是否可安全反馈
     */
    private static String resolveMessage(Exception e) {
        if (e == null) {
            return "未知错误 ❌";
        }

        Class<?> clazz = e.getClass();
        if (clazz.isAnnotationPresent(BotFeedback.class)) {
            return e.getMessage().trim() + " ❌";
        }

        log.error("系统异常: {}", e.getMessage(), e);
        return "系统开小差了，请稍后再试~ ❌";
    }
}
