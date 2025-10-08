package com.bot_system.annotation;

import java.lang.annotation.*;

/**
 * @className: BotFeedback
 * @author: Java之父
 * @date: 2025/10/5 18:14
 * @version: 1.0.0
 * @description: 异常反馈注解（标明在异常类上，机器人将把异常的message反馈给用户）
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BotFeedback {
}
