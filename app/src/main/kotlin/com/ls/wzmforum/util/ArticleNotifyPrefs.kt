package com.ls.wzmforum.util

import android.content.Context

/**
 * 新内容提醒的基线记录：记住"上次已看到的最新文章 id"，
 * 服务轮询到更新的文章时据此判断是否需要提醒用户。
 */
object ArticleNotifyPrefs {

    private const val PREFS = "article_notify_pref"
    private const val KEY_LAST_SEEN_ID = "last_seen_id"

    fun lastSeenId(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_LAST_SEEN_ID, 0)
    }

    fun updateLastSeen(context: Context, latestId: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_LAST_SEEN_ID, latestId)
            .apply()
    }

    /** 用户打开 App 后把基线重置为 0，下一次轮询重新建立基线，避免打扰正在使用中的用户 */
    fun resetBaseline(context: Context) {
        updateLastSeen(context, 0)
    }
}
