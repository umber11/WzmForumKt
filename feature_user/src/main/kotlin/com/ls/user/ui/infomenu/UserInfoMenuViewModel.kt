package com.ls.user.ui.infomenu

import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.UserInfo
import com.ls.librarybase.manager.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * 用户信息菜单 ViewModel，提供头像、昵称、简介及关注/粉丝/勋章等用户数据。
 */
class UserInfoMenuViewModel : BaseViewModel() {
    private val _avatarUrl = MutableStateFlow("")
    val avatarUrl: StateFlow<String> = _avatarUrl.asStateFlow()
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()
    private val _nickName = MutableStateFlow("")
    val nickName: StateFlow<String> = _nickName.asStateFlow()
    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio.asStateFlow()
    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()
    private val _follow = MutableStateFlow("0")
    val follow: StateFlow<String> = _follow.asStateFlow()
    private val _fans = MutableStateFlow("0")
    val fans: StateFlow<String> = _fans.asStateFlow()
    private val _medal = MutableStateFlow("0")
    val medal: StateFlow<String> = _medal.asStateFlow()

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        val resUser = UserManager.getInstance().userInfo
        if (resUser != null) {
            val info = resUser.user
            if (info != null) {
                _avatarUrl.value = info.avatar ?: ""
                _userName.value = info.username ?: ""
                _nickName.value = info.nickname ?: ""
                _bio.value = info.bio ?: ""
                _status.value = info.status ?: ""
            }
            _follow.value = resUser.follow.toString()
            _fans.value = resUser.fans.toString()
            _medal.value = resUser.medal.toString()
        }
    }
}
