package com.ls.librarybase.utils

import android.util.Log
import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.manager.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

/**
 * 登录后批量补齐列表项的点赞状态。
 *
 * 列表接口(getActicleList)不返回 islike，只有详情接口(articleDetail)带 token 时会返回。
 * 详情接口每次调用会让文章浏览量 +1，因此这里对每篇文章只探测一次，
 * 结果写入 LikeTracker(并标记 checked)，之后不再重复请求。
 */
object LikeSyncHelper {

    private const val TAG = "LikeSyncHelper"
    private const val CONCURRENCY = 3

    private val mSemaphore = Semaphore(CONCURRENCY)

    suspend fun sync(
        articleIds: List<Int>,
        onItemSynced: (articleId: Int, islike: Int, likes: Int) -> Unit
    ) {
        val token = UserManager.getInstance().token
        if (token.isNullOrEmpty()) return

        val pending = articleIds.distinct().filter { !LikeTracker.isChecked(it) }
        if (pending.isEmpty()) return
        Log.d(TAG, "sync start, pending size=$pending")

        val api = ArticleDetailApiServiceProvider.getApiService()
        withContext(Dispatchers.IO) {
            pending.forEach { id ->
                mSemaphore.withPermit {
                    try {
                        val response = api.articleDetail(token, id.toString()).execute()
                        val body = response.body()
                        val info = body?.data?.archivesInfo
                        if (body?.code == 1 && info != null) {
                            LikeTracker.setLiked(info.id, info.islike == 1, info.likes)
                            LikeTracker.markChecked(info.id)
                            Log.d(TAG, "article=$id islike=${info.islike} likes=${info.likes}")
                            onItemSynced(info.id, info.islike, info.likes)
                        } else {
                            Log.w(TAG, "article=$id detail failed, code=${body?.code}, error=${body?.msg}")
                            LikeTracker.markChecked(id)
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "article=$id detail request error", e)
                    }
                }
            }
        }
    }
}
