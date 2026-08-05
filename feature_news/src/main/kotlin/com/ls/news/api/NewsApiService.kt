package com.ls.news.api

import com.ls.librarybase.bean.ResArticleList
import com.ls.network.bean.ResBase
import com.ls.news.bean.ResNewsCategory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NewsApiService {

    @POST("addons/cms/api.archives/getArticleChannel")
    fun newsCategory(@Header("token") token: String): Call<ResBase<ResNewsCategory>>

    @POST("addons/cms/api.archives/getActicleList")
    fun articleList(@Header("token") token: String, @Body params: Map<String, String>): Call<ResArticleList>
}
