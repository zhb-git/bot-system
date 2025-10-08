package com.bot_system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @className: SystemConfig
 * @author: Java之父
 * @date: 2025/10/4 21:48
 * @version: 1.0.0
 * @description: 本地系统配置
 */
@Data
@Configuration
@ConfigurationProperties("system")
public class SystemConfig {
    private String domain;
}
