package com.ls.products.api

import com.ls.librarybase.bean.ResArticleList
import com.ls.network.bean.ResBase
import com.ls.products.bean.ResProductsCategory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProductsApiService {
    @POST("addons/cms/api.archives/getDeviceChannel")
    fun productsCategory(@Header("token") token: String): Call<ResBase<ResProductsCategory>>

    @POST("addons/cms/api.archives/getActicleList")
    fun contentList(@Body params: Map<String, String>): Call<ResArticleList>
}
