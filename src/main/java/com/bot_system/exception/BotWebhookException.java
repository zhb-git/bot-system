package com.bot_system.exception;

/**
 * @className: BotWebhookException
 * @author: Java之父
 * @date: 2025/10/2 15:46
 * @version: 1.0.0
 * @description: 机器人webhook处理异常
 */
public class BotWebhookException extends RuntimeException {
    public BotWebhookException(String message) {
        super(message);
    }

    public BotWebhookException(String message, Throwable cause) {
        super(message, cause);
    }
}
