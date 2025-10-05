package com.bot_system.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 * -
 * 提供常用的时间格式化与解析方法。
 *
 * @author Java之父
 * @since 2025/10/4
 */
public final class TimUtil {
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    // 标准格式化模板
    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 将时间差（秒）格式化为“几秒前 / 几分钟前 / 几小时前 / 几天前”。
     *
     * @param seconds 时间差（单位：秒）
     * @return 友好的人类可读时间描述
     */
    public static String timeAgo(long seconds) {
        if (seconds < 60) {
            return seconds + " 秒前";
        }
        if (seconds < 3600) {
            return (seconds / 60) + " 分钟前";
        }
        if (seconds < 86400) {
            return (seconds / 3600) + " 小时前";
        }
        return (seconds / 86400) + " 天前";
    }

    /**
     * 将 LocalDateTime 格式化为“yyyy-MM-dd HH:mm:ss”。
     *
     * @param time 时间对象
     * @return 格式化后的字符串
     */
    public static String formatFull(LocalDateTime time) {
        return time == null ? null : FULL_FORMATTER.format(time);
    }

    /**
     * 将 LocalDateTime 格式化为“yyyy-MM-dd”。
     *
     * @param time 时间对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(LocalDateTime time) {
        return time == null ? null : DATE_FORMATTER.format(time);
    }

    /**
     * 将 LocalDateTime 格式化为“HH:mm:ss”。
     *
     * @param time 时间对象
     * @return 格式化后的时间字符串
     */
    public static String formatTime(LocalDateTime time) {
        return time == null ? null : TIME_FORMATTER.format(time);
    }

    /**
     * 解析“yyyy-MM-dd HH:mm:ss”字符串为 LocalDateTime。
     *
     * @param text 时间字符串
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parseFull(String text) {
        return (text == null || text.isEmpty()) ? null : LocalDateTime.parse(text, FULL_FORMATTER);
    }

    /**
     * 解析“yyyy-MM-dd”字符串为 LocalDateTime（时分秒自动补 00:00:00）。
     *
     * @param text 日期字符串
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parseDate(String text) {
        if (text == null || text.isEmpty()) return null;
        return LocalDateTime.parse(text + " 00:00:00", FULL_FORMATTER);
    }

    /**
     * 将时间戳（毫秒）转换为 LocalDateTime。
     *
     * @param epochMilli 毫秒级时间戳
     * @return LocalDateTime 对象
     */
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE);
    }

    /**
     * 将 LocalDateTime 转换为毫秒级时间戳。
     *
     * @param time LocalDateTime 对象
     * @return 时间戳（毫秒）
     */
    public static long toEpochMilli(LocalDateTime time) {
        if (time == null) return 0;
        return time.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
    }
}
