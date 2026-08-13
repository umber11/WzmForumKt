package com.ls.librarybase.navigation

import com.ls.librarybase.manager.UserManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * 登录状态总线：以 Flow 对外发布登录状态与用户信息刷新事件。
 */
object LoginStateBus {

    private val _loginState = MutableStateFlow(UserManager.getInstance().isLogin())
    val loginState: StateFlow<Boolean> = _loginState.asStateFlow()

    private val _userInfoRefresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val userInfoRefresh: SharedFlow<Unit> = _userInfoRefresh.asSharedFlow()

    fun post(isLogin: Boolean) {
        _loginState.value = isLogin
    }

    fun postUserInfoRefresh() {
        _userInfoRefresh.tryEmit(Unit)
    }
}
