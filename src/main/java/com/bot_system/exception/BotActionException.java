package com.bot_system.exception;

/**
 * @className: BotActionException
 * @author: Java之父
 * @date: 2025/10/1 16:55
 * @version: 1.0.0
 * @description: 机器人操作异常
 */
public class BotActionException extends RuntimeException {
    public BotActionException(String message) {
        super(message);
    }

    public BotActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
