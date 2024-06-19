package com.ailu.constant;

/**
 * @Description:
 * @Author: your name
 * @Date: 13/6/2024 下午2:23
 */

public enum ArticleConstant {
    ARTICLE_STATUS_PRIVATE(false),
    ARTICLE_STATUS_PUBLIC(true),;
    private final boolean isPublic;

    ArticleConstant(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "ArticleConstant{" +
                "isPublic=" + isPublic +
                '}';
    }
}
