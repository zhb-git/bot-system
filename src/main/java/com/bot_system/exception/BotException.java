package com.bot_system.exception;

/**
 * @className: BotException
 * @author: Java之父
 * @date: 2025/9/20 15:05
 * @version: 1.0.0
 * @description: 机器人异常
 */
public class BotException extends RuntimeException {
    public BotException(String message) {
        super(message);
    }

    public BotException(String message, Throwable cause) {
        super(message, cause);
    }
}
