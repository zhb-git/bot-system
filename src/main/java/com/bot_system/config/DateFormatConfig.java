package com.bot_system.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @className: DateFormatConfig
 * @author: Java之父
 * @date: 2025/8/13 17:02
 * @version: 1.0.0
 * @description: 全局接口返回时间格式处理
 */
@JsonComponent
public class DateFormatConfig {
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    @Value("${spring.jackson.time-zone:Asia/Shanghai}")
    private String timezone;


    @Bean
    public Jackson2ObjectMapperBuilderCustomizer dateCustomizer() {
        return builder -> {
            TimeZone tz = TimeZone.getTimeZone(timezone);
            DateFormat df = new SimpleDateFormat(pattern);
            df.setTimeZone(tz);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .dateFormat(df);
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeSerializer());
    }

    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }
}
