package com.ls.librarybase.utils

import android.content.Context
import com.ls.librarybase.base.BaseApplication
import java.util.HashMap
import java.util.HashSet

object CollectionTracker {
    private const val PREFS_NAME = "collection_tracker"
    private const val KEY_COLLECTED_IDS = "collected_ids"

    private val sCollectedIds = HashSet<Int>()
    private val sCollectionCounts = HashMap<Int, Int>()
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

    fun setCollected(articleId: Int, collected: Boolean, count: Int) {
        ensureLoaded()
        if (collected) {
            sCollectedIds.add(articleId)
        } else {
            sCollectedIds.remove(articleId)
        }
        sCollectionCounts[articleId] = count
        save()
    }
}
