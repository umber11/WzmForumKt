package com.ls.wzmforum.ui.main

import com.ls.librarybase.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    private val mModel = MainModel()

    fun savePrivacyAgreementStatus() {
        mModel.savePrivacyAgreementStatus()
    }

    fun getPrivacyAgreementStatus(): Boolean = mModel.getPrivacyAgreementStatus()
}
