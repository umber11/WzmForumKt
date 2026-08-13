package com.ls.wzmforum.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ls.librarybase.utils.NetworkUtils
import com.ls.wzmforum.bus.NetworkStateBus

/**
 * 网络状态变化接收者：接收系统 CONNECTIVITY_CHANGE 广播，
 * 通过 NetworkStateBus 通知 Compose UI 提示用户网络断开。
 */
class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NetworkStateBus.post(NetworkUtils.isNetworkAvailable())
    }
}
