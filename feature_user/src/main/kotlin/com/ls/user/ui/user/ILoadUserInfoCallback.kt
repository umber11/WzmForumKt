package com.ls.user.ui.user

import com.ls.librarybase.bean.ResUser
/**
 * 用户信息加载回调接口，定义加载成功与失败的回调方法。
 */
interface ILoadUserInfoCallback {

    fun onLoadSuccess(user: ResUser)

    fun onLoadFailure(errorCode: Int, message: String)
}
