package com.bot_system.common.lock;

import com.google.common.collect.MapMaker;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @className: DynamicLockPool
 * @author: Java之父
 * @date: 2025/8/2
 * @version: 1.0.0
 * @description:
 *  本地轻量级动态锁池，支持单个或多个业务 key 的并发加锁控制。
 *  特点：
 *   - 基于 Guava MapMaker 的弱引用缓存，避免内存泄漏
 *   - 支持任意 key 分离锁隔离，防止串扰
 *   - 支持多 key 同时加锁，避免死锁（排序加锁）
 */
public final class DynamicLockPool {

    /**
     * 锁池：
     * key   - 业务唯一标识，例如 user:123、order:abc 等
     * value - ReentrantLock，生命周期由 weakValues 控制（无引用时 GC 自动清理）
     */
    private static final Map<String, ReentrantLock> LOCK_MAP = new MapMaker().weakValues().makeMap();

    /**
     * 默认尝试加锁的超时时间（毫秒），用于 tryLock 版本
     */
    private static final long DEFAULT_TIMEOUT_MS = 1000;

    /**
     * 对单个 key 进行阻塞加锁（无返回值）
     */
    public static void execute(String key, Runnable task) {
        // 检查参数
        validExecuteArgs(key, task);
        // 加锁执行
        ReentrantLock lock = LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock(); // 阻塞直到获取锁
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 对单个 key 进行阻塞加锁（有返回值）
     */
    public static <T> T execute(String key, Supplier<T> task) {
        // 检查参数
        validExecuteArgs(key, task);
        // 加锁执行
        ReentrantLock lock = LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 对单个 key 进行限时加锁（无返回值）
     * 若在指定时间未获取锁，则抛出异常
     */
    public static void tryExecute(String key, Runnable task) {
        // 检查参数
        validExecuteArgs(key, task);
        // 加锁执行
        ReentrantLock lock = LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
        boolean locked = false;
        try {
            locked = lock.tryLock(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!locked) throw new RuntimeException("获取锁失败: " + key);
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            throw new RuntimeException("线程中断", e);
        } finally {
            if (locked) lock.unlock();
        }
    }

    /**
     * 对单个 key 进行限时加锁（有返回值）
     */
    public static <T> T tryExecute(String key, Supplier<T> task) {
        // 检查参数
        validExecuteArgs(key, task);
        // 加锁执行
        ReentrantLock lock = LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
        boolean locked = false;
        try {
            locked = lock.tryLock(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!locked) throw new RuntimeException("获取锁失败: " + key);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("线程中断", e);
        } finally {
            if (locked) lock.unlock();
        }
    }

    /**
     * 检查参数
     * @param key 锁key
     * @param task 业务方法
     */
    private static void validExecuteArgs(String key, Object task) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("锁key不能为null | ''");
        }
        if (task == null) {
            throw new IllegalArgumentException("业务方法不能为null");
        }
    }

    /**
     * 对多个 key 进行阻塞加锁（无返回值）
     * 按字典序排序 key，避免加锁顺序不一致导致死锁
     */
    public static void execute(Set<String> keys, Runnable task) {
        // 检查参数
        validExecuteArgs(keys, task);
        // 加锁执行
        doExecute(keys, false, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 对多个 key 进行阻塞加锁（有返回值）
     */
    public static <T> T execute(Set<String> keys, Supplier<T> task) {
        // 检查参数
        validExecuteArgs(keys, task);
        // 加锁执行
        return doExecute(keys, false, task);
    }

    /**
     * 对多个 key 进行限时加锁（无返回值）
     * 若任意锁加锁失败，则整体失败
     */
    public static void tryExecute(Set<String> keys, Runnable task) {
        // 检查参数
        validExecuteArgs(keys, task);
        // 加锁执行
        doExecute(keys, true, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 对多个 key 进行限时加锁（有返回值）
     */
    public static <T> T tryExecute(Set<String> keys, Supplier<T> task) {
        // 检查参数
        validExecuteArgs(keys, task);
        // 加锁执行
        return doExecute(keys, true, task);
    }

    /**
     * 检查参数
     * @param keys 锁key
     * @param task 业务方法
     */
    private static void validExecuteArgs(Set<String> keys, Object task) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("锁key不能为null | size=0");
        }
        for (String key : keys) {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("锁key不能为null | ''");
            }
        }
        if (task == null) {
            throw new IllegalArgumentException("业务方法不能为null");
        }
    }

    /**
     * 通用多 key 加锁逻辑
     *
     * @param keys     需要加锁的 key 集合（如 userId、orderId 等）
     * @param tryLock  是否为 tryLock 模式（true：限时尝试；false：阻塞直到成功）
     * @param task     要执行的业务逻辑
     * @return         返回执行结果
     */
    private static <T> T doExecute(Set<String> keys, boolean tryLock, Supplier<T> task) {
        if (keys == null || keys.isEmpty()) {
            return task.get(); // 无需加锁
        }

        // 对 key 排序，确保加锁顺序一致，避免死锁
        List<String> sortedKeys = new ArrayList<>(keys);
        Collections.sort(sortedKeys);

        List<ReentrantLock> locks = new ArrayList<>();
        int lockedCount = 0;

        try {
            for (String key : sortedKeys) {
                ReentrantLock lock = LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
                locks.add(lock);

                boolean locked;
                if (tryLock) {
                    // 限时加锁
                    locked = lock.tryLock(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    if (!locked) throw new RuntimeException("获取锁失败: " + key);
                } else {
                    // 阻塞加锁
                    lock.lock();
                }

                lockedCount++;
            }

            return task.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("线程中断", e);
        } finally {
            // 解锁顺序：反向解锁，防止死锁或依赖问题
            for (int i = lockedCount - 1; i >= 0; i--) {
                locks.get(i).unlock();
            }
        }
    }
}