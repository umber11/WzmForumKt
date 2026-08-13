package com.ls.user.ui.user

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.bean.UserInfo
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.network.bean.ResBase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * 用户中心 ViewModel，管理用户信息状态、退出登录及页面导航动作。
 */
class UserViewModel : BaseViewModel() {

    private val model = UserModel()

    private val _avatar = MutableStateFlow("")
    val avatar: StateFlow<String> = _avatar.asStateFlow()
    private val _nickName = MutableStateFlow("")
    val nickName: StateFlow<String> = _nickName.asStateFlow()
    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio.asStateFlow()
    private val _action = MutableSharedFlow<UserCenterAction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val action: SharedFlow<UserCenterAction> = _action.asSharedFlow()

    init {
        viewModelScope.launch {
            LoginStateBus.loginState.collect { isLogin ->
                loadUserInfo(isLogin)
            }
        }
        viewModelScope.launch {
            LoginStateBus.userInfoRefresh.collect {
                loadUserInfo(true)
            }
        }
    }

    fun loadUserInfo(login: Boolean) {
        if (login) {
            showLoading(true)
            model.loadUserInfo(object : ILoadUserInfoCallback {
                override fun onLoadSuccess(user: ResUser) {
                    showLoading(false)
                    updateUserInfo(user)
                }

                override fun onLoadFailure(errorCode: Int, message: String) {
                    showLoading(false)
                    notLoginUpdateUserInfo()
                }
            })
        } else {
            notLoginUpdateUserInfo()
        }
    }

    private fun notLoginUpdateUserInfo() {
        val user = ResUser()
        user.user = UserInfo()
        updateUserInfo(user)
    }

    private fun updateUserInfo(user: ResUser) {
        val u = user.user
        val avatar = u?.avatar
        if (avatar != null && avatar.isNotEmpty()) {
            _avatar.value = avatar
        } else {
            _avatar.value = ""
        }

        val nickname = u?.nickname
        if (nickname != null && nickname.isNotEmpty()) {
            _nickName.value = nickname
        } else {
            _nickName.value = "请先登录"
        }

        val bio = u?.bio
        if (bio != null && bio.isNotEmpty()) {
            _bio.value = bio
        } else {
            _bio.value = "请编辑资料完善个人信息"
        }
    }

    fun onEditUserInfoClick() {
        val login = model.isLogin()
        _action.tryEmit(if (login) UserCenterAction.NAVIGATION_TO_EDIT_INFO else UserCenterAction.NAVIGATE_TO_LOGIN)
    }

    fun onCommentClick() {
        _action.tryEmit(UserCenterAction.NAVIGATION_TO_COMMENT)
    }

    fun onCollectionClick() {
        _action.tryEmit(UserCenterAction.NAVIGATION_TO_COLLECTION)
    }

    fun onUserInfoMenuClick() {
        _action.tryEmit(UserCenterAction.NAVIGATE_TO_USER_INFO_MENU)
    }

    fun onSettingsClick() {
        _action.tryEmit(UserCenterAction.NAVIGATE_TO_ABOUT_ME)
    }

    fun onLogoutClick() {
        _action.tryEmit(UserCenterAction.SHOW_LOGOUT_DIALOG)
    }

    fun logout() {
        showLoading(true)
        model.logout(object : IRequestCallback<ResBase<Any>> {
            override fun onLoadSuccess(datas: ResBase<Any>) {
                LoginStateBus.post(false)
                showToast(datas.msg)
                showLoading(false)
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                UserManager.getInstance().logout()
                LoginStateBus.post(false)
                showToast(message)
                showLoading(false)
            }
        })
    }

    enum class UserCenterAction {
        SHOW_LOGOUT_DIALOG,
        NAVIGATION_TO_EDIT_INFO,
        NAVIGATION_TO_COMMENT,
        NAVIGATION_TO_COLLECTION,
        NAVIGATE_TO_LOGIN,
        NAVIGATE_TO_USER_INFO_MENU,
        NAVIGATE_TO_ABOUT_ME
    }
}
