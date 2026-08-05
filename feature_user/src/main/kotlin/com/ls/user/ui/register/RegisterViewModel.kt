package com.ls.user.ui.register

import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.base.IRequestCallback
import com.ls.network.bean.ResBase
import com.ls.user.bean.ResPasswordLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
                showToast("注册成功，请登录")
                _registerSuccess.value = true
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                showLoading(false)
                showToast(message)
            }
        })
    }
}
