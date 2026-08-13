package com.ls.user.ui.register

import android.content.Context
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.user.api.UserApiServiceProvider
import com.ls.user.bean.ReqPasswordRegister
import com.ls.user.bean.ResPasswordLogin

/**
 * 注册数据模型
 */
class RegisterModel {

    fun passwordRegister(username: String, password: String, callback: IRequestCallback<ResBase<ResPasswordLogin>>) {
        val register = ReqPasswordRegister(username, password)
        val call = UserApiServiceProvider.apiService.passwordRegister(register)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<ResPasswordLogin>> {
            override fun onSuccess(result: ResBase<ResPasswordLogin>) {
                // 注册成功后端若直接签发 token，则自动登录（与登录流程一致）
                val token = result.data?.token
                if (!token.isNullOrEmpty()) {
                    UserManager.getInstance().saveToken(token)
                    val prefs = BaseApplication.getContext().getSharedPreferences("agreementStatus", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("agreement_privacy", true).apply()
                }
                callback.onLoadSuccess(result)
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
