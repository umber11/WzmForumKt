package com.ls.librarybase.utils

import android.content.Context
import com.ls.librarybase.base.BaseApplication
import java.util.HashMap
import java.util.HashSet

object LikeTracker {
    private const val PREFS_NAME = "like_tracker"
    private const val KEY_LIKED_IDS = "liked_ids"
    private const val KEY_CHECKED_IDS = "checked_ids"

    private val sLikeCounts = HashMap<Int, Int>()
    private val sLikedIds = HashSet<Int>()
    private val sCheckedIds = HashSet<Int>()
    private var sLoaded = false

    private fun ensureLoaded() {
        if (sLoaded) return
        sLoaded = true
        val context = BaseApplication.getContext()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedIds = prefs.getStringSet(KEY_LIKED_IDS, HashSet()) ?: HashSet()
        for (idStr in storedIds) {
            try {
                sLikedIds.add(idStr.toInt())
            } catch (_: NumberFormatException) {
            }
        }
        val storedChecked = prefs.getStringSet(KEY_CHECKED_IDS, HashSet()) ?: HashSet()
        for (idStr in storedChecked) {
            try {
                sCheckedIds.add(idStr.toInt())
            } catch (_: NumberFormatException) {
            }
        }
    }

    private fun save() {
        val context = BaseApplication.getContext()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val idsToSave = HashSet<String>()
        for (id in sLikedIds) {
            idsToSave.add(id.toString())
        }
        val checkedToSave = HashSet<String>()
        for (id in sCheckedIds) {
            checkedToSave.add(id.toString())
        }
        prefs.edit()
            .putStringSet(KEY_LIKED_IDS, idsToSave)
            .putStringSet(KEY_CHECKED_IDS, checkedToSave)
            .apply()
    }

    fun setLiked(articleId: Int, liked: Boolean, count: Int) {
        ensureLoaded()
        if (liked) {
            sLikedIds.add(articleId)
        } else {
            sLikedIds.remove(articleId)
        }
        sLikeCounts[articleId] = count
        save()
    }

    fun isLiked(articleId: Int): Boolean {
        ensureLoaded()
        return sLikedIds.contains(articleId)
    }

    fun getLikeCount(articleId: Int): Int? {
        ensureLoaded()
        return sLikeCounts[articleId]
    }

    fun markChecked(articleId: Int) {
        ensureLoaded()
        sCheckedIds.add(articleId)
        save()
    }

    fun isChecked(articleId: Int): Boolean {
        ensureLoaded()
        return sCheckedIds.contains(articleId)
    }

    fun clear() {
        ensureLoaded()
        sLikedIds.clear()
        sCheckedIds.clear()
        sLikeCounts.clear()
        save()
    }
}
