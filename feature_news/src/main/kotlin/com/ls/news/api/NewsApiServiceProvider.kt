package com.ls.news.api

import com.ls.network.RetrofitProvider

object NewsApiServiceProvider {

    private var mApiService: NewsApiService? = null

    @JvmStatic
    val apiService: NewsApiService
        get() {
            if (mApiService == null) {
                val retrofit = RetrofitProvider.provide()
                mApiService = retrofit.create(NewsApiService::class.java)
            }
            return mApiService!!
        }
}
