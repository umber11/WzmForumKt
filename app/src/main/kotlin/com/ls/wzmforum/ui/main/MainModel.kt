package com.ls.wzmforum.ui.main

import android.content.Context
import com.ls.librarybase.base.BaseApplication
/**
 * 主模块数据层：读写隐私协议同意状态的本地缓存。
 */
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
