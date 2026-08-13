package com.ls.wzmforum.ui.main

import com.ls.librarybase.base.BaseViewModel
/**
 * 主模块 ViewModel：代理隐私协议状态的读写。
 */
class MainViewModel : BaseViewModel() {
    private val mModel = MainModel()

    fun savePrivacyAgreementStatus() {
        mModel.savePrivacyAgreementStatus()
    }

    fun getPrivacyAgreementStatus(): Boolean = mModel.getPrivacyAgreementStatus()
}
