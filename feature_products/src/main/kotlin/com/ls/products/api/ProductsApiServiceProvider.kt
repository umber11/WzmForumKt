package com.ls.products.api

import com.ls.network.RetrofitProvider
/**
 * 产品网络接口单例提供者，懒加载创建 Retrofit 服务。
 */
object ProductsApiServiceProvider {
    val apiService: ProductsApiService by lazy {
        RetrofitProvider.provide().create(ProductsApiService::class.java)
    }
}
