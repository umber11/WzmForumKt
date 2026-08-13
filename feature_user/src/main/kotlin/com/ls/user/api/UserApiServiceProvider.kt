package com.ls.user.api

import com.ls.network.RetrofitProvider
/**
 * 通过 RetrofitProvider 懒加载提供 UserApiService 单例。
 */
object UserApiServiceProvider {
    val apiService: UserApiService by lazy {
        RetrofitProvider.provide().create(UserApiService::class.java)
    }
}
