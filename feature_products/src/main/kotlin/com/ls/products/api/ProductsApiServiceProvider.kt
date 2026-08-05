package com.ls.products.api

import com.ls.network.RetrofitProvider

object ProductsApiServiceProvider {
    val apiService: ProductsApiService by lazy {
        RetrofitProvider.provide().create(ProductsApiService::class.java)
    }
}
