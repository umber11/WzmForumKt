package com.ls.librarybase.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.ls.librarybase.base.BaseApplication

/**
 * 网络工具类：判断当前网络是否可用。
 * 供关键操作（登录/注册/评论/点赞/收藏等）前置校验以及断网横幅使用。
 */
object NetworkUtils {

    fun isNetworkAvailable(): Boolean {
        val cm = BaseApplication.getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        @Suppress("DEPRECATION")
        return cm.activeNetworkInfo?.isConnected == true
    }
}
