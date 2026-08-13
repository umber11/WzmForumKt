package com.ls.librarybase.utils

import java.util.HashMap
/**
 * 文章信息内存缓存：按文章 id 缓存标题与封面图。
 */
object ArticleCache {
    private class ArticleInfo(var title: String?, var image: String?)

    private val sCache = HashMap<Int, ArticleInfo>()

    fun put(aid: Int, title: String?, image: String?) {
        sCache[aid] = ArticleInfo(title, image)
    }

    fun getTitle(aid: Int): String? {
        return sCache[aid]?.title
    }

    fun getImage(aid: Int): String? {
        return sCache[aid]?.image
    }
}
