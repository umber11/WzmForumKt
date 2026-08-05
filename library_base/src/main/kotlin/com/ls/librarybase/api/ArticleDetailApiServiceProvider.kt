package com.ls.librarybase.api

import com.ls.network.RetrofitProvider

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
