package com.ls.news.ui.news

import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.news.api.NewsApiServiceProvider
import com.ls.news.bean.ResNewsCategory
/**
 * 资讯数据层：请求频道分类与文章列表，含 token、参数组装与结果校验。
 */
class NewsModel {
    private val mApiService = NewsApiServiceProvider.apiService

    fun loadCategories(callback: ApiCall.ApiCallBack<ResBase<ResNewsCategory>>) {
        var token = UserManager.getInstance().token
        if (token == null) token = ""
        ApiCall.enqueueCommon(mApiService.newsCategory(token), callback)
    }

    fun loadArticles(
        keyword: String, channelId: String, page: Int, limit: Int,
        sortField: String, sortOrder: String,
        callback: ApiCall.ApiCallBack<ResArticleList>
    ) {
        val params = HashMap<String, String>()
        params["keyword"] = keyword ?: ""
        params["channel_id"] = channelId
        params["limit"] = limit.toString()
        params["page"] = page.toString()
        params["sort_field"] = sortField
        params["sort_order"] = sortOrder
        var token = UserManager.getInstance().token
        if (token == null) token = ""
        ApiCall.enqueueSimple(mApiService.articleList(token, params), object : ApiCall.ApiCallBack<ResArticleList> {
            override fun onSuccess(result: ResArticleList) {
                if (result.code == 1 || result.code == 1001) {
                    callback.onSuccess(result)
                } else {
                    callback.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, result.msg ?: "错误")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onError(errorCode, message)
            }
        })
    }
}
