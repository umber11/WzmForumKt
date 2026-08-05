package com.ls.user.ui.register

import com.ls.librarybase.base.IRequestCallback
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.user.api.UserApiServiceProvider
import com.ls.user.bean.ReqPasswordRegister
import com.ls.user.bean.ResPasswordLogin

class RegisterModel {

    fun passwordRegister(username: String, password: String, callback: IRequestCallback<ResBase<ResPasswordLogin>>) {
        val register = ReqPasswordRegister(username, password)
        val call = UserApiServiceProvider.apiService.passwordRegister(register)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<ResPasswordLogin>> {
            override fun onSuccess(result: ResBase<ResPasswordLogin>) {
                callback.onLoadSuccess(result)
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onLoadFailure(errorCode, message)
            }
        })
    }
}
