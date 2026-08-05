package com.ls.home.api

import com.ls.home.bean.ReqArticleList
import com.ls.home.bean.ResHomeCategory
import com.ls.librarybase.bean.ResArticleList
import com.ls.network.bean.ResBase
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HomeApiService {

    @POST("addons/cms/api.archives/get_channel")
    fun homeCategory(): Call<ResBase<ResHomeCategory>>

    @POST("addons/cms/api.archives/getActicleList")
    fun articleList(@Header("token") token: String, @Body articleList: ReqArticleList): Call<ResArticleList>
}
