package com.ls.user.ui.aboutme

import com.ls.librarybase.utils.VersionUtils

class AboutMeModel {
    val versionName: String
        get() = VersionUtils.getVersionName()

    val versionCode: Int
        get() = VersionUtils.getVersionCode()
}
