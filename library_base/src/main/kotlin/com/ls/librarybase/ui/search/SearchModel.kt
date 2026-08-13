package com.ls.librarybase.ui.search

import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.config.ErrorStatusConfig
/**
 * 搜索数据模型：负责通过 API 按关键词与频道发起文章搜索。
 */
class SearchModel {

    private val mApiService = ArticleDetailApiServiceProvider.getApiService()

    fun search(keyword: String?, channelId: String?, page: Int, limit: Int,
               sortField: String, sortOrder: String?,
               callback: ApiCall.ApiCallBack<ResArticleList>) {
        val params = mapOf(
            "keyword" to (keyword ?: ""),
            "channel_id" to (channelId ?: ""),
            "limit" to limit.toString(),
            "page" to page.toString(),
            "sort_field" to (if ("default" == sortField) "publishtime" else sortField),
            "sort_order" to (sortOrder ?: "desc")
        )

        val token = UserManager.getInstance().token ?: ""
        ApiCall.enqueueSimple(mApiService.articleList(token, params), object : ApiCall.ApiCallBack<ResArticleList> {
            override fun onSuccess(result: ResArticleList) {
                if (result.code == 1 || result.code == 1001) {
                    callback.onSuccess(result)
                } else {
                    callback.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, result.msg ?: "未知错误")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onError(errorCode, message)
            }
        })
    }
}
