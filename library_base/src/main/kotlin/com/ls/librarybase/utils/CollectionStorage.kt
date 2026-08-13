package com.ls.librarybase.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.bean.ResArticleList
import java.util.ArrayList
/**
 * 收藏数据持久化存储：将收藏列表以 JSON 形式保存到 SharedPreferences。
 */
object CollectionStorage {
    private const val PREFS_NAME = "collection_storage"
    private const val KEY_DATA = "collections_json"

    private val sCache = HashMap<Int, ResArticleList.ListBean>()
    private var sLoaded = false

    private fun ensureLoaded() {
        if (sLoaded) return
        sLoaded = true
        val context = BaseApplication.getContext()
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DATA, null)
        if (json != null) {
            try {
                val type = object : TypeToken<HashMap<Int, ResArticleList.ListBean>>() {}.type
                val saved = Gson().fromJson<HashMap<Int, ResArticleList.ListBean>>(json, type)
                if (saved != null) {
                    sCache.putAll(saved)
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun save() {
        val context = BaseApplication.getContext()
        val json = Gson().toJson(sCache)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_DATA, json)
            .apply()
    }

    fun put(aid: Int, data: ResArticleList.ListBean) {
        ensureLoaded()
        sCache[aid] = data
        save()
    }

    fun remove(aid: Int) {
        ensureLoaded()
        sCache.remove(aid)
        save()
    }

    fun getAll(): List<ResArticleList.ListBean> {
        ensureLoaded()
        return ArrayList(sCache.values)
    }
}
