package com.ls.wzmforum.bus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 网络连接状态总线：由 NetworkChangeReceiver 写入，UI 层通过 StateFlow 订阅。
 */
object NetworkStateBus {

    private val _networkState = MutableStateFlow(true)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    fun post(online: Boolean) {
        _networkState.value = online
    }
}
