package com.ls.user.ui.register

import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.librarybase.utils.NetworkUtils
import com.ls.network.bean.ResBase
import com.ls.user.bean.ResPasswordLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 注册 ViewModel，管理注册输入状态并执行账号密码注册流程。
 */
class RegisterViewModel : BaseViewModel() {


    private val mModel = RegisterModel()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    private val _confirmPwd = MutableStateFlow("")
    val confirmPwd: StateFlow<String> = _confirmPwd.asStateFlow()
    private val _checkAgreement = MutableStateFlow(false)
    val checkAgreement: StateFlow<Boolean> = _checkAgreement.asStateFlow()
    private val _isEnableRegister = MutableStateFlow(false)
    val isEnableRegister: StateFlow<Boolean> = _isEnableRegister.asStateFlow()
    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun setUsername(value: String) {
        _username.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setConfirmPwd(value: String) {
        _confirmPwd.value = value
    }

    fun setCheckAgreement(value: Boolean) {
        _checkAgreement.value = value
    }

    fun updateRegisterBtnStatus() {
        val username = username.value
        val password = password.value
        val confirmPwd = confirmPwd.value
        val isEnable = username.length > 0
                && password.length > 0
                && password == confirmPwd
        _isEnableRegister.value = isEnable
    }

    fun register() {
        val checkAgreement = checkAgreement.value
        if (checkAgreement != true) {
            showToast("请先同意用户协议与隐私政策")
            return
        }
        if (!NetworkUtils.isNetworkAvailable()) {
            showToast("网络连接不可用，请检查网络")
            return
        }

        val username = username.value
        val password = password.value

        if (username.isEmpty()) {
            showToast("请输入用户名")
            return
        }
        if (password.isEmpty()) {
            showToast("请输入密码")
            return
        }

        showLoading(true)
        mModel.passwordRegister(username, password, object : IRequestCallback<ResBase<ResPasswordLogin>> {
            override fun onLoadSuccess(datas: ResBase<ResPasswordLogin>) {
                showLoading(false)
                val data = datas.data
                if (data == null || data.user_id <= 0 || data.token.isNullOrEmpty()) {
                    // 后端未直接签发 token：保持原逻辑，提示去登录
                    showToast("注册成功，请登录")
                    _registerSuccess.value = true
                    return
                }
                showToast("注册成功，已自动登录")
                getUserInfo(data.user_id)
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                showLoading(false)
                showToast(message)
            }
        })
    }

    private fun getUserInfo(id: Int) {
        showLoading(true)
        mModel.getUserInfo(id.toString(), object : IRequestCallback<ResBase<ResUser>> {
            override fun onLoadSuccess(datas: ResBase<ResUser>) {
                showLoading(false)
                _registerSuccess.value = true
                LoginStateBus.post(true)
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                showLoading(false)
                // 用户信息获取失败：回滚已保存的 token，避免半登录状态
                UserManager.getInstance().logout()
                showToast(message)
            }
        })
    }
}
