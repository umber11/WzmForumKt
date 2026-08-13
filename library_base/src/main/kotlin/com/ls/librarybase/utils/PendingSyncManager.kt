package com.ls.librarybase.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.manager.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 待同步队列：离线时把"点赞/取消点赞/收藏/取消收藏"先乐观更新到本地，
 * 并在这里记录"最终期望状态"，联网后按最终状态补传服务器（幂等、可覆盖）。
 *
 * 队列按文章 id 只保存最终状态（like/collect 各一个布尔值）：
 * 例如离线时先点赞再取消点赞，最终只同步"取消点赞"这一次操作。
 */
object PendingSyncManager {

    private const val PREFS = "pending_sync"
    private const val KEY = "pending_json"
    private const val TAG = "PendingSyncManager"

    private val gson = Gson()
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    /** 防止 App 启动与网络恢复同时触发导致的并发同步 */
    private val processingLock = AtomicBoolean(false)

    private data class PendingState(
        var like: Boolean? = null,
        var collect: Boolean? = null
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    @Synchronized
    fun setLike(context: Context, articleId: Int, liked: Boolean) {
        val map = load(context)
        val state = map[articleId] ?: PendingState().also { map[articleId] = it }
        state.like = liked
        save(context, map)
        processQueue(context)
    }

    @Synchronized
    fun setCollect(context: Context, articleId: Int, collected: Boolean) {
        val map = load(context)
        val state = map[articleId] ?: PendingState().also { map[articleId] = it }
        state.collect = collected
        save(context, map)
        processQueue(context)
    }

    /**
     * 清空待同步队列。
     * 登出时调用，避免上一账号的离线操作被补传到下一个登录账号。
     */
    @Synchronized
    fun clear(context: Context) {
        prefs(context).edit().remove(KEY).apply()
    }

    /**
     * 同步全部待同步操作到服务器。
     * 联网 + 已登录才执行；单篇文章任一次请求失败（如再次断网）则停止本轮同步，保留剩余队列。
     */
    fun processQueue(context: Context) {
        if (!NetworkUtils.isNetworkAvailable()) return
        if (!UserManager.getInstance().isLogin()) return
        if (!processingLock.compareAndSet(false, true)) return
        ioScope.launch {
            try {
                doProcess(context.applicationContext)
            } finally {
                processingLock.set(false)
            }
        }
    }

    /**
     * 与 setLike/setCollect 使用同一对象监视器互斥（本方法内为阻塞式网络调用，无挂起点），
     * 避免处理过程中新增的待同步项被覆盖丢失。
     */
    private suspend fun doProcess(context: Context) {
        synchronized(this) {
            val token = UserManager.getInstance().token ?: return
            val pending = load(context)
            if (pending.isEmpty()) return

            val api = ArticleDetailApiServiceProvider.getApiService()
            val iterator = pending.entries.iterator()
            while (iterator.hasNext()) {
                val (aid, state) = iterator.next()

                val like = state.like
                val collect = state.collect
                var likeOk = like == null
                var collectOk = collect == null

                if (!likeOk) {
                    try {
                        val params = if (like == true) {
                            mapOf("id" to aid.toString(), "type" to "like")
                        } else {
                            mapOf("aid" to aid.toString())
                        }
                        val resp = if (like == true) {
                            api.vote(token, params).execute()
                        } else {
                            api.voteDel(token, params).execute()
                        }
                        val code = resp.body()?.code ?: -1
                        // FastAdmin 系列接口成功码不统一（1 或 1001），统一兼容
                        likeOk = code == 1 || code == 1001
                    } catch (e: Exception) {
                        likeOk = false
                    }
                }

                if (!collectOk) {
                    try {
                        val params = if (collect == true) {
                            mapOf("aid" to aid.toString(), "type" to "archives")
                        } else {
                            mapOf("aid" to aid.toString())
                        }
                        val resp = if (collect == true) {
                            api.collectionCreate(token, params).execute()
                        } else {
                            api.collectionDelete(token, params).execute()
                        }
                        val code = resp.body()?.code ?: -1
                        collectOk = code == 1 || code == 1001
                    } catch (e: Exception) {
                        collectOk = false
                    }
                }

                if (likeOk && collectOk) {
                    iterator.remove()
                    save(context, pending)
                } else {
                    // 请求失败（大概率又断网/登录失效），停止本轮同步，剩余队列保留
                    android.util.Log.w(TAG, "sync failed, stop. aid=$aid like=$like collect=$collect")
                    return
                }
            }
        }
    }

    private fun load(context: Context): MutableMap<Int, PendingState> {
        val json = prefs(context).getString(KEY, null) ?: return mutableMapOf()
        return try {
            gson.fromJson(json, object : TypeToken<HashMap<Int, PendingState>>() {}.type)
                ?: mutableMapOf()
        } catch (e: Exception) {
            mutableMapOf()
        }
    }

    private fun save(context: Context, map: MutableMap<Int, PendingState>) {
        prefs(context).edit().putString(KEY, gson.toJson(map)).apply()
    }
}
