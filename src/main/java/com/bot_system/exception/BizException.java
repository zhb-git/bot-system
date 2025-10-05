package com.bot_system.exception;

import com.bot_system.annotation.BotFeedback;

/**
 * @className: BizException
 * @author: Java之父
 * @date: 2025/9/10 12:43
 * @version: 1.0.0
 * @description: 业务执行异常
 */
@BotFeedback
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
