package com.ls.librarybase.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
/**
 * 状态栏工具类：提供沉浸式状态栏设置（透明背景 + 浅色图标）。
 */
object StatusBarUtils {
    fun setImmerseStatusBar(activity: Activity) {
        activity.window.statusBarColor = Color.TRANSPARENT
        val decorView = activity.window.decorView
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = flags
    }
}
