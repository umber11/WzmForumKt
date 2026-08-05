package com.ls.librarybase.ui.article

import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.bean.ResArticleDetail
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase

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
