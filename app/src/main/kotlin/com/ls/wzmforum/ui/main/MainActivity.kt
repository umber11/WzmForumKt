package com.ls.wzmforum.ui.main

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ls.librarybase.ui.theme.WzmForumTheme
import com.ls.librarybase.utils.NetworkUtils
import com.ls.librarybase.utils.PendingSyncManager
import com.ls.librarybase.utils.StatusBarUtils
import com.ls.wzmforum.bus.NetworkStateBus
import com.ls.wzmforum.component.OfflineBanner
import com.ls.wzmforum.receiver.NetworkChangeReceiver
import com.ls.wzmforum.service.NewArticleNotifyService
import com.ls.wzmforum.ui.navigation.AppNavigation
import com.ls.wzmforum.util.ArticleNotifyPrefs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 主页面Activity
 */
class MainActivity : ComponentActivity() {

    // BroadcastReceiver 演示：动态注册网络变化接收者（API 26+ 系统不再给静态注册的
    // CONNECTIVITY_CHANGE 接收者发送广播，因此这里采用动态注册）
    private val networkChangeReceiver = NetworkChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setImmerseStatusBar(this)

        // 动态注册网络变化接收者（API 26+ 系统不再给静态注册的 CONNECTIVITY_CHANGE
        // 接收者发送广播）。注册于 onCreate/注销于 onDestroy，保证 App 退到后台
        // 时也能捕获网络恢复，触发离线操作补传与横幅状态更新。
        ContextCompat.registerReceiver(
            this,
            networkChangeReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        // Service 演示：启动前台服务，后台轮询首页新内容并提醒
        NewArticleNotifyService.start(this)
        // 用户在 App 内浏览时重置基线，避免轮询结果反复打扰
        ArticleNotifyPrefs.resetBaseline(this)
        // Android 13+ 通知权限：前台服务通知需要用户授权后才会展示
        requestNotificationPermission()

        // 同步一次当前网络状态：App 离线启动时横幅也能正确显示（NetworkStateBus 默认值是 true）
        NetworkStateBus.post(NetworkUtils.isNetworkAvailable())

        // 第三阶段：App 启动且联网时，尝试补传离线期间缓存的点赞/收藏操作
        if (NetworkUtils.isNetworkAvailable()) {
            PendingSyncManager.processQueue(this)
        }

        // 网络恢复时，自动补传离线缓存的点赞/收藏操作
        lifecycleScope.launch {
            NetworkStateBus.networkState.collectLatest { online ->
                if (online) {
                    PendingSyncManager.processQueue(this@MainActivity)
                }
            }
        }

        setContent {
            WzmForumTheme {
                Box {
                    AppNavigation(
                        onExit = { finish() }
                    )
                    // 全局断网横幅：覆盖所有页面
                    OfflineBanner()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* 授权结果无需额外处理 */ }
}
