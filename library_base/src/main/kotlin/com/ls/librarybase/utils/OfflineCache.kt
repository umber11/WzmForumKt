package com.ls.librarybase.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ls.librarybase.bean.ResArticleDetail
import com.ls.librarybase.bean.ResArticleList

/**
 * 离线缓存：把最近一次成功加载的「文章列表 / 文章详情 / 频道分类」持久化到本地，
 * 断网时读取缓存实现"离线可读"。
 *
 * 缓存策略：每次网络请求成功就覆盖缓存，因此缓存内容始终是"最近一次看到的最新数据"。
 */
object OfflineCache {

    private const val PREFS = "offline_cache"
    private val gson = Gson()

    private fun prefs(context: Context): android.content.SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun put(context: Context, key: String, value: Any?) {
        if (value == null) return
        prefs(context).edit().putString(key, gson.toJson(value)).apply()
    }

    fun remove(context: Context, key: String) {
        prefs(context).edit().remove(key).apply()
    }

    fun <T> get(context: Context, key: String, type: TypeToken<T>): T? {
        val json = prefs(context).getString(key, null) ?: return null
        return try {
            gson.fromJson<T>(json, type.type)
        } catch (e: Exception) {
            null
        }
    }

    // ==== 文章列表缓存（key: list_{screen}_{channelId}）====
    private fun listKey(screen: String, channelId: String) = "list_${screen}_${channelId}"

    fun saveList(context: Context, screen: String, channelId: String, list: List<ResArticleList.ListBean>) {
        put(context, listKey(screen, channelId), list)
    }

    fun loadList(context: Context, screen: String, channelId: String): List<ResArticleList.ListBean>? {
        return get(context, listKey(screen, channelId), object : TypeToken<List<ResArticleList.ListBean>>() {})
    }

    // ==== 文章详情缓存（key: detail_{articleId}）====
    fun saveDetail(context: Context, articleId: String, detail: ResArticleDetail) {
        put(context, "detail_$articleId", detail)
    }

    fun loadDetail(context: Context, articleId: String): ResArticleDetail? {
        return get(context, "detail_$articleId", object : TypeToken<ResArticleDetail>() {})
    }
}
