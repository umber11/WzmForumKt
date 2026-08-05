package com.ls.librarybase.base

import android.app.Application
import android.content.Context

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
