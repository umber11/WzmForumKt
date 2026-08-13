package com.ls.home.api

import com.ls.network.RetrofitProvider
/**
 * 首页网络接口单例提供者，懒加载创建 Retrofit 服务。
 */
object HomeApiServiceProvider {

    private var mApiService: HomeApiService? = null

    @JvmStatic
    val apiService: HomeApiService
        get() {
            if (mApiService == null) {
                val retrofit = RetrofitProvider.provide()
                mApiService = retrofit.create(HomeApiService::class.java)
            }
            return mApiService!!
        }
}
