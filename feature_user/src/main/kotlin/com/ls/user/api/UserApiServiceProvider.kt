package com.ls.user.api

import com.ls.network.RetrofitProvider

object UserApiServiceProvider {
    val apiService: UserApiService by lazy {
        RetrofitProvider.provide().create(UserApiService::class.java)
    }
}
