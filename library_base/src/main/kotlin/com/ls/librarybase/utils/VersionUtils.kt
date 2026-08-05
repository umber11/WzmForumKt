package com.ls.librarybase.utils

import android.content.pm.PackageManager
import com.ls.librarybase.base.BaseApplication

object VersionUtils {
    fun getVersionName(): String {
        return try {
            val context = BaseApplication.getContext()
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    fun getVersionCode(): Int {
        val context = BaseApplication.getContext()
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }
}
