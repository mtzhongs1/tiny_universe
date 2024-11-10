package com.ailu.context;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午5:08
 */

public class IndexNameContext {
    private static final ThreadLocal<String> threadlocal = new ThreadLocal<>();
    public static void setIndexName(String indexName) {
        threadlocal.set(indexName);
    }
    public static String getIndexName() {
        return threadlocal.get();
    }
    public static void removeIndexName() {
        threadlocal.remove();
    }
}
