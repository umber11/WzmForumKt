package com.ls.news.api

import com.ls.network.RetrofitProvider
/**
 * 资讯网络接口单例提供者，懒加载创建 Retrofit 服务。
 */
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
