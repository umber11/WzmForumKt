package com.ls.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Retrofit 单例提供者：基于统一 BaseUrl 与 OkHttpClient 构建 Retrofit 实例。
 */
object RetrofitProvider {
    var mRetrofit: Retrofit? = null
    const val BASE_URL = "http://dayhot.fzqq.fun/"

    fun provide(): Retrofit {
        if (mRetrofit == null) {
            mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkhttpClientProvider.provide())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return mRetrofit!!
    }
}
