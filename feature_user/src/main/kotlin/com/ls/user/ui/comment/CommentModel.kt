package com.ls.user.ui.comment

import android.util.Log
import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.bean.ResArticleDetail
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.user.api.UserApiServiceProvider
import com.ls.user.bean.ReqComments
import com.ls.user.bean.ResComments
/**
 * 评论列表数据模型，加载我的评论数据并补全关联文章详情。
 */
class CommentModel {

    companion object {
        private const val TAG = "CommentModel"
    }

    private val mUserApi = UserApiServiceProvider.apiService
    private val mArticleApi = ArticleDetailApiServiceProvider.getApiService()

    interface CommentListCallback {
        fun onSuccess(result: ResComments)
        fun onArticleDetailLoaded()
        fun onError(errorCode: Int, message: String)
    }

    fun loadCommentList(page: Int, limit: Int, callback: CommentListCallback) {
        val rawToken = UserManager.getInstance().token
        val token = rawToken ?: ""

        val req = ReqComments(page.toString(), limit.toString())
        Log.d(TAG, "loadCommentList token=$token")

        ApiCall.enqueueCommon(mUserApi.commentList(token, req), object : ApiCall.ApiCallBack<ResBase<ResComments>> {
            override fun onSuccess(result: ResBase<ResComments>) {
                val data = result.data
                if (data == null) {
                    callback.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, "数据为空")
                    return
                }
                Log.d(TAG, "comments loaded count=${data.list?.size ?: 0}")
                val list = data.list
                if (list != null) {
                    for (item in list) {
                        Log.d(TAG, "comment id=${item.id} aid=${item.aid} archives=${if (item.archives != null) "present" else "null"}")
                    }
                    fetchArticleDetails(token, list, callback)
                }
                callback.onSuccess(data)
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e(TAG, "loadCommentList error: $errorCode $message")
                callback.onError(errorCode, message)
            }
        })
    }

    private fun fetchArticleDetails(token: String, items: List<ResComments.ListBean>, callback: CommentListCallback) {
        for (item in items) {
            val archives = item.archives
            val needsFetch = archives == null
                    || archives.image == null
                    || archives.title == null
            if (needsFetch && item.aid > 0) {
                Log.d(TAG, "fetching article detail for aid=${item.aid}")
                fetchArticleDetail(token, item, callback)
            }
        }
    }

    private fun fetchArticleDetail(token: String, item: ResComments.ListBean, callback: CommentListCallback) {
        val aid = item.aid.toString()
        ApiCall.enqueueCommon(mArticleApi.articleDetail(token, aid), object : ApiCall.ApiCallBack<ResBase<ResArticleDetail>> {
            override fun onSuccess(result: ResBase<ResArticleDetail>) {
                val data = result.data ?: return
                val info = data.archivesInfo ?: return
                Log.d(TAG, "article detail loaded: id=${info.id} title=${info.title} image=${info.image}")
                val archives = ResComments.ListBean.ArchivesBean()
                archives.id = info.id
                archives.title = info.title
                archives.image = info.image
                item.archives = archives
                callback.onArticleDetailLoaded()
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e(TAG, "article detail error for aid=$aid: $errorCode $message")
            }
        })
    }
}
