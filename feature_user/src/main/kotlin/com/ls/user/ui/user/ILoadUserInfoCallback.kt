package com.ls.user.ui.user

import com.ls.librarybase.bean.ResUser

interface ILoadUserInfoCallback {

    fun onLoadSuccess(user: ResUser)

    fun onLoadFailure(errorCode: Int, message: String)
}
