package com.ls.wzmforum.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ls.wzmforum.service.NewArticleNotifyService

/**
 * 开机广播接收者：手机开机完成后自动拉起前台服务，恢复新内容监测。
 * 静态注册于 AndroidManifest.xml，接收系统广播 BOOT_COMPLETED。
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            NewArticleNotifyService.start(context)
        }
    }
}
