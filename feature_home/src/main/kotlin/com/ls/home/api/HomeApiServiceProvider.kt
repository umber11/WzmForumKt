package com.ls.home.api

import com.ls.network.RetrofitProvider

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
