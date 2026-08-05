package com.ls.user.api

import com.ls.librarybase.bean.ResUser
import com.ls.network.bean.ResBase
import com.ls.user.bean.ReqComments
import com.ls.user.bean.ReqMobileLogin
import com.ls.user.bean.ReqPasswordLogin
import com.ls.user.bean.ReqPasswordRegister
import com.ls.user.bean.ReqSendSmsCode
import com.ls.user.bean.ReqUpdateUserProfile
import com.ls.user.bean.ResCollectionList
import com.ls.user.bean.ResComments
import com.ls.user.bean.ResMobileLogin
import com.ls.user.bean.ResPasswordLogin
import com.ls.user.bean.ResUpload
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApiService {
    @POST("addons/cms/api.login/mobilelogin")
    fun mobileLogin(@Body login: ReqMobileLogin): Call<ResBase<ResMobileLogin>>

    @POST("addons/cms/api.sms/send")
    fun sendSmsCode(@Body code: ReqSendSmsCode): Call<ResBase<Any>>

    @POST("addons/cms/api.login/register")
    fun passwordRegister(@Body register: ReqPasswordRegister): Call<ResBase<ResPasswordLogin>>

    @POST("addons/cms/api.login/login")
    fun passwordLogin(@Body login: ReqPasswordLogin): Call<ResBase<ResPasswordLogin>>

    @GET("addons/cms/api.user/userInfo")
    fun getUserInfo(@Query("user_id") userId: String?, @Query("type") type: String?): Call<ResBase<ResUser>>

    @POST("addons/cms/api.user/profile")
    fun updateUserProfile(@Header("token") token: String, @Body profile: ReqUpdateUserProfile): Call<ResBase<Void>>

    @POST("addons/cms/api.user/logout")
    fun logout(@Header("token") token: String): Call<ResBase<Any>>

    @Multipart
    @POST("api/common/upload")
    fun uploadFile(@Header("token") token: String, @Part file: MultipartBody.Part): Call<ResBase<ResUpload>>

    @POST("addons/cms/api.collection/index")
    fun collectionList(@Header("token") token: String, @Body params: Map<String, String>): Call<ResBase<ResCollectionList>>

    @POST("addons/cms/api.comment/getMyComment")
    fun commentList(@Header("token") token: String, @Body comments: ReqComments): Call<ResBase<ResComments>>
}
