package com.ls.librarybase.utils

import java.util.HashMap

object ViewTracker {
    private val sViewCounts = HashMap<Int, Int>()

    fun setViews(articleId: Int, views: Int) {
        sViewCounts[articleId] = views
    }

    fun getViews(articleId: Int): Int? {
        return sViewCounts[articleId]
    }
}
