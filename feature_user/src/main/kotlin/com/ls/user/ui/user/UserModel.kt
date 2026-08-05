package com.ls.user.ui.user

import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.user.api.UserApiServiceProvider

class UserModel {

    fun isLogin(): Boolean {
        return UserManager.getInstance().isLogin()
    }

    fun loadUserInfo(callback: ILoadUserInfoCallback) {
        if (isLogin()) {
            val userInfo = UserManager.getInstance().userInfo
            if (userInfo != null) {
                callback.onLoadSuccess(userInfo)
            } else {
                callback.onLoadFailure(ErrorStatusConfig.ERROR_STATUS_NOT_LOGIN, "未登录")
            }
        } else {
            callback.onLoadFailure(ErrorStatusConfig.ERROR_STATUS_NOT_LOGIN, "未登录")
        }
    }

    fun logout(callback: IRequestCallback<ResBase<Any>>) {
        val token = UserManager.getInstance().token ?: ""
        val call = UserApiServiceProvider.apiService.logout(token)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<Any>> {
            override fun onSuccess(result: ResBase<Any>) {
                UserManager.getInstance().logout()
                callback.onLoadSuccess(result)
            }

            override fun onError(errorCode: Int, message: String) {
                UserManager.getInstance().logout()
                callback.onLoadFailure(errorCode, message)
            }
        })
    }
}
