package com.ls.librarybase.utils

import java.util.HashMap
/**
 * 浏览量内存追踪：按文章 id 记录并查询浏览量。
 */
object ViewTracker {
    private val sViewCounts = HashMap<Int, Int>()

    fun setViews(articleId: Int, views: Int) {
        sViewCounts[articleId] = views
    }

    fun getViews(articleId: Int): Int? {
        return sViewCounts[articleId]
    }
}
