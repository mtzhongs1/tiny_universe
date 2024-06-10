package com.ailu.context;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午9:26
 */

public class BaseContext {
    private static final ThreadLocal<Long> threadlocal = new ThreadLocal<>();
    public static void setCurrentId(Long id) {
        threadlocal.set(id);
    }
    public static Long getCurrentId() {
        return threadlocal.get();
    }
    public static void removeCurrentId() {
        threadlocal.remove();
    }
}
