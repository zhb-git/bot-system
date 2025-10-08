package com.bot_system.common.lock;

/**
 * @className: Locks
 * @author: Java之父
 * @date: 2025/9/11 13:46
 * @version: 1.0.0
 * @description: 锁
 */
public final class Locks {
    /**
     * 创建管理员锁（根据账号来）
     * @param account 账号
     * @return        锁
     */
    public static String getCreateAdminLock(String account) {
        return "createAdmin:" + account;
    }
}
