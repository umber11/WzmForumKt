package com.ls.librarybase.utils

import android.content.Context
import com.ls.librarybase.base.BaseApplication
import java.util.HashSet
/**
 * 收藏状态追踪：持久化记录已收藏的文章 id 集合。
 */
object CollectionTracker {
    private const val PREFS_NAME = "collection_tracker"
    private const val KEY_COLLECTED_IDS = "collected_ids"

    private val sCollectedIds = HashSet<Int>()
    private var sLoaded = false

    private fun ensureLoaded() {
        if (sLoaded) return
        sLoaded = true
        val context = BaseApplication.getContext()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedIds = prefs.getStringSet(KEY_COLLECTED_IDS, HashSet()) ?: HashSet()
        for (idStr in storedIds) {
            try {
                sCollectedIds.add(idStr.toInt())
            } catch (_: NumberFormatException) {
            }
        }
    }

    private fun save() {
        val context = BaseApplication.getContext()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val idsToSave = HashSet<String>()
        for (id in sCollectedIds) {
            idsToSave.add(id.toString())
        }
        prefs.edit().putStringSet(KEY_COLLECTED_IDS, idsToSave).apply()
    }

    @Synchronized
    fun setCollected(articleId: Int, collected: Boolean) {
        ensureLoaded()
        if (collected) {
            sCollectedIds.add(articleId)
        } else {
            sCollectedIds.remove(articleId)
        }
        save()
    }
}
