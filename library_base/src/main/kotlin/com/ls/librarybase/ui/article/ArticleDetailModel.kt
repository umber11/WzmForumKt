package com.ls.librarybase.ui.article

import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.bean.ResArticleDetail
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
/**
 * 文章详情数据模型：负责通过 API 加载文章详情数据。
 */
class ArticleDetailModel(private val mCallback: ApiCall.ApiCallBack<ResBase<ResArticleDetail>>) {

    private val mApiService = ArticleDetailApiServiceProvider.getApiService()
    private var mToken: String? = null

    fun setToken(token: String) {
        mToken = token
    }

    fun loadArticleDetail(articleId: String) {
        ApiCall.enqueueCommon(mApiService.articleDetail(mToken ?: "", articleId), mCallback)
    }
}
