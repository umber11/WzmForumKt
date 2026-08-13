package com.ls.user.ui.aboutme

import com.ls.librarybase.utils.VersionUtils
/**
 * 关于我们页数据模型，提供应用版本名与版本号。
 */
class AboutMeModel {
    val versionName: String
        get() = VersionUtils.getVersionName()

    val versionCode: Int
        get() = VersionUtils.getVersionCode()
}
