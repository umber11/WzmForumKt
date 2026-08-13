package com.ls.librarybase.api

import com.ls.librarybase.bean.ResArticleDetail
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.bean.ResCollection
import com.ls.librarybase.bean.ResCommentPostData
import com.ls.librarybase.bean.ResVote
import com.ls.network.bean.ResBase
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
/**
 * 文章详情相关 Retrofit 接口定义：详情、列表、投票、收藏、评论等请求。
 */
interface ArticleDetailApiService {
    @GET("addons/cms/api.archives/detail")
    fun articleDetail(@Header("token") token: String, @Query("id") id: String): Call<ResBase<ResArticleDetail>>

    @POST("addons/cms/api.archives/getActicleList")
    fun articleList(@Header("token") token: String, @Body params: Map<String, String>): Call<ResArticleList>

    @POST("addons/cms/api.archives/vote")
    fun vote(@Header("token") token: String, @Body params: Map<String, String>): Call<ResVote>

    @POST("addons/cms/api.archives/vote_del")
    fun voteDel(@Header("token") token: String, @Body params: Map<String, String>): Call<ResVote>

    @POST("addons/cms/api.collection/create")
    fun collectionCreate(@Header("token") token: String, @Body params: Map<String, String>): Call<ResCollection>

    @POST("addons/cms/api.collection/delete")
    fun collectionDelete(@Header("token") token: String, @Body params: Map<String, String>): Call<ResCollection>

    @POST("addons/cms/api.comment/post")
    fun commentPost(@Header("token") token: String, @Body params: Map<String, String>): Call<ResBase<ResCommentPostData>>

    @POST("addons/cms/api.comment/delete")
    fun commentDelete(@Header("token") token: String, @Body params: Map<String, String>): Call<ResBase<Void>>
}
