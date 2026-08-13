package com.ls.home.ui.home

import android.util.Log
import com.ls.home.api.HomeApiServiceProvider
import com.ls.home.bean.ReqArticleList
import com.ls.home.bean.ResHomeCategory
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
/**
 * 首页数据层：请求频道分类与文章列表，含 token、排序参数组装与结果校验。
 */
class HomeModel {
    private val mApiService = HomeApiServiceProvider.apiService

    fun loadCategories(callback: ApiCall.ApiCallBack<ResBase<ResHomeCategory>>) {
        ApiCall.enqueueCommon(mApiService.homeCategory(), callback)
    }

    fun loadArticles(
        keyword: String, channelId: String, page: Int, limit: Int,
        callback: ApiCall.ApiCallBack<ResArticleList>
    ) {
        loadArticles(keyword, channelId, page, limit, "publishtime", "desc", callback)
    }

    fun loadArticles(
        keyword: String, channelId: String, page: Int, limit: Int,
        sortField: String, sortOrder: String,
        callback: ApiCall.ApiCallBack<ResArticleList>
    ) {
        val actualField = if ("default" == sortField) "publishtime" else sortField
        val req = ReqArticleList(
            keyword,
            channelId,
            limit.toString(),
            page.toString(),
            actualField,
            sortOrder
        )
        Log.d("HomeModel", "channelId param=|$channelId| keyword param=|$keyword| sort_field=|$sortField| sort_order=|$sortOrder|")

        var token = UserManager.getInstance().token
        if (token == null) token = ""
        ApiCall.enqueueSimple(mApiService.articleList(token, req), object : ApiCall.ApiCallBack<ResArticleList> {
            override fun onSuccess(result: ResArticleList) {
                Log.d("HomeModel", "articleList code=${result.code} msg=${result.msg} list=${result.list?.size ?: "null"}")
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
