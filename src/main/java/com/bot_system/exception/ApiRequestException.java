package com.bot_system.exception;

/**
 * @className: ApiRequestException
 * @author: Java之父
 * @date: 2025/10/5 0:00
 * @version: 1.0.0
 * @description: 接口请求异常
 */
public class ApiRequestException extends RuntimeException {
    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
