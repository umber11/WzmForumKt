package com.ls.user.ui.login

import android.content.Context
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.user.api.UserApiServiceProvider
import com.ls.user.bean.ReqMobileLogin
import com.ls.user.bean.ReqPasswordLogin
import com.ls.user.bean.ReqSendSmsCode
import com.ls.user.bean.ResMobileLogin
import com.ls.user.bean.ResPasswordLogin

class LoginModel {

    fun sendSmsCode(mobile: String, callback: IRequestCallback<ResBase<Any>>) {
        val smsCode = ReqSendSmsCode(mobile, "mobilelogin")
        val call = UserApiServiceProvider.apiService.sendSmsCode(smsCode)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<Any>> {
            override fun onSuccess(result: ResBase<Any>) {
                callback.onLoadSuccess(result)
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onLoadFailure(errorCode, message)
            }
        })
    }

    fun mobileLogin(mobile: String, code: String, callback: IRequestCallback<ResBase<ResMobileLogin>>) {
        val login = ReqMobileLogin(mobile, code)
        val call = UserApiServiceProvider.apiService.mobileLogin(login)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<ResMobileLogin>> {
            override fun onSuccess(result: ResBase<ResMobileLogin>) {
                callback.onLoadSuccess(result)

                val token = result.data?.token ?: ""
                val userManager = UserManager.getInstance()
                userManager.saveToken(token)

                val preferences = BaseApplication.getContext().getSharedPreferences("agreementStatus", Context.MODE_PRIVATE)
                preferences.edit().putBoolean("agreement_privacy", true).apply()
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onLoadFailure(errorCode, message)
            }
        })
    }

    fun passwordLogin(account: String, password: String, callback: IRequestCallback<ResBase<ResPasswordLogin>>) {
        val login = ReqPasswordLogin(account, password)
        val call = UserApiServiceProvider.apiService.passwordLogin(login)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<ResPasswordLogin>> {
            override fun onSuccess(result: ResBase<ResPasswordLogin>) {
                callback.onLoadSuccess(result)
                val token = result.data?.token ?: ""
                UserManager.getInstance().saveToken(token)
                val prefs = BaseApplication.getContext().getSharedPreferences("agreementStatus", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("agreement_privacy", true).apply()
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onLoadFailure(errorCode, message)
            }
        })
    }

    fun getUserInfo(userId: String, callback: IRequestCallback<ResBase<ResUser>>) {
        val call = UserApiServiceProvider.apiService.getUserInfo(userId, "archives")
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<ResUser>> {
            override fun onSuccess(result: ResBase<ResUser>) {
                val resUser = result.data
                if (resUser != null) {
                    UserManager.getInstance().saveUserInfo(resUser)
                    callback.onLoadSuccess(result)
                } else {
                    callback.onLoadFailure(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, "用户信息获取失败！")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onLoadFailure(errorCode, message)
            }
        })
    }
}
