package com.ls.librarybase.api

import com.ls.network.RetrofitProvider
/**
 * ArticleDetailApiService 单例提供者：通过 Retrofit 创建并缓存接口实例。
 */
object ArticleDetailApiServiceProvider {
    private var mApiService: ArticleDetailApiService? = null

    fun getApiService(): ArticleDetailApiService {
        if (mApiService == null) {
            val retrofit = RetrofitProvider.provide()
            mApiService = retrofit.create(ArticleDetailApiService::class.java)
        }
        return mApiService!!
    }
}
