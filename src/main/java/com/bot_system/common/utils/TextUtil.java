package com.bot_system.common.utils;

/**
 * @className: TextUtil
 * @author: Java之父
 * @date: 2025/10/4 23:55
 * @version: 1.0.0
 * @description: 文本工具类
 */
public final class TextUtil {
    /**
     * 将数字用空格分组（例如 1234567 → 1 234 567）
     *
     * @param num 数字
     * @return 格式化字符串
     */
    public static String formatNumber(long num) {
        return String.format("%,d", num).replace(",", " ");
    }
}
