package com.ls.user.ui.login

import android.os.CountDownTimer
import android.util.Log
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.network.bean.ResBase
import com.ls.user.bean.ResMobileLogin
import com.ls.user.bean.ResPasswordLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
        const val MODE_PHONE_CODE = 0
    }

    private val mModel = LoginModel()

    private val _userMobile = MutableStateFlow("")
    val userMobile: StateFlow<String> = _userMobile.asStateFlow()
    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()
    private val _getVerticalCodeText = MutableStateFlow("获取验证码")
    val getVerticalCodeText: StateFlow<String> = _getVerticalCodeText.asStateFlow()
    private val _isEnableSendCode = MutableStateFlow(true)
    val isEnableSendCode: StateFlow<Boolean> = _isEnableSendCode.asStateFlow()
    private var mDownTimer: CountDownTimer? = null

    private val _account = MutableStateFlow("")
    val account: StateFlow<String> = _account.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginMode = MutableStateFlow(MODE_PHONE_CODE)
    val loginMode: StateFlow<Int> = _loginMode.asStateFlow()
    private val _isEnableLogin = MutableStateFlow(false)
    val isEnableLogin: StateFlow<Boolean> = _isEnableLogin.asStateFlow()
    private val _checkAgreement = MutableStateFlow(false)
    val checkAgreement: StateFlow<Boolean> = _checkAgreement.asStateFlow()
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun setUserMobile(value: String) {
        _userMobile.value = value
    }

    fun setCode(value: String) {
        _code.value = value
    }

    fun setAccount(value: String) {
        _account.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setCheckAgreement(value: Boolean) {
        _checkAgreement.value = value
    }

    fun updateEnableLoginBtnStatus() {
        val mode = _loginMode.value
        val isEnable: Boolean = if (mode == MODE_PHONE_CODE) {
            val mobile = userMobile.value
            val code = code.value
            mobile.length == 11 && code.length == 4
        } else {
            val account = account.value
            val pwd = password.value
            account.length > 0 && pwd.length > 0
        }
        _isEnableLogin.value = isEnable
    }

    fun sendCode() {
        val mobile = userMobile.value
        if (mobile.length != 11) {
            Log.i(TAG, "sendCode: 手机号不符合规则！")
            showToast("请输入正确的手机号码！")
            return
        }

        mDownTimer?.cancel()

        _isEnableSendCode.value = false

        mDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                _getVerticalCodeText.value = "${seconds}s"
            }

            override fun onFinish() {
                _getVerticalCodeText.value = "获取验证码"
                _isEnableSendCode.value = true
            }
        }.start()

        Log.i(TAG, "sendCode: ")
        showLoading(true)
        mModel.sendSmsCode(mobile, object : IRequestCallback<ResBase<Any>> {
            override fun onLoadSuccess(datas: ResBase<Any>) {
                showToast(datas.msg)
                showLoading(false)
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                showToast(message)
                showLoading(false)
            }
        })
    }

    fun setLoginMode(mode: Int) {
        _loginMode.value = mode
        _account.value = ""
        _password.value = ""
        updateEnableLoginBtnStatus()
    }

    fun login() {
        val checkAgreement = checkAgreement.value
        if (checkAgreement != true) {
            showToast("请先同意用户协议与隐私政策")
            Log.i(TAG, "请先同意用户协议与隐私政策")
            return
        }
        showLoading(true)

        val mode = _loginMode.value

        if (mode == MODE_PHONE_CODE) {
            val mobile = userMobile.value
            val code = code.value
            mModel.mobileLogin(mobile, code, object : IRequestCallback<ResBase<ResMobileLogin>> {
                override fun onLoadSuccess(datas: ResBase<ResMobileLogin>) {
                    Log.i(TAG, "onLoadFinish token：" + datas.data)
                    showLoading(false)
                    showToast(datas.msg)

                    val id = datas.data?.id ?: 0
                    getUserInfo(id)
                }

                override fun onLoadFailure(errorCode: Int, message: String) {
                    showToast(message)
                    showLoading(false)
                }
            })
        } else {
            val account = account.value
            val pwd = password.value
            mModel.passwordLogin(account, pwd, object : IRequestCallback<ResBase<ResPasswordLogin>> {
                override fun onLoadSuccess(datas: ResBase<ResPasswordLogin>) {
                    showLoading(false)
                    showToast(datas.msg)
                    getUserInfo(datas.data?.user_id ?: 0)
                }

                override fun onLoadFailure(errorCode: Int, message: String) {
                    showToast(message)
                    showLoading(false)
                }
            })
        }
    }

    private fun getUserInfo(id: Int) {
        showLoading(true)
        mModel.getUserInfo(id.toString(), object : IRequestCallback<ResBase<ResUser>> {
            override fun onLoadSuccess(datas: ResBase<ResUser>) {
                showLoading(false)
                _loginSuccess.value = true
                LoginStateBus.post(true)
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                showLoading(false)
                showToast(message)
            }
        })
    }
}
