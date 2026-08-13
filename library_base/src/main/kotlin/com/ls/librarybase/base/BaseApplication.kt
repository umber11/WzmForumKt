package com.ls.librarybase.base

import android.app.Application
import android.content.Context
/**
 * 应用基类：持有全局 Application 实例，提供 getContext() 获取全局上下文。
 */
open class BaseApplication : Application() {

    companion object {
        private var instance: Application? = null

        fun getContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
