package com.ls.wzmforum.ui.main

import android.content.Context
import com.ls.librarybase.base.BaseApplication

class MainModel {
    fun savePrivacyAgreementStatus() {
        val preferences = BaseApplication.getContext().getSharedPreferences("agreementStatus", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("agreement_privacy", true).apply()
    }

    fun getPrivacyAgreementStatus(): Boolean {
        val preferences = BaseApplication.getContext().getSharedPreferences("agreementStatus", Context.MODE_PRIVATE)
        return preferences.getBoolean("agreement_privacy", false)
    }
}
