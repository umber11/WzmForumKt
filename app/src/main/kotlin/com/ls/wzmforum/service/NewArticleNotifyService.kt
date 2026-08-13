package com.ls.wzmforum.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.ls.home.api.HomeApiServiceProvider
import com.ls.home.bean.ReqArticleList
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.wzmforum.R
import com.ls.wzmforum.ui.main.MainActivity
import com.ls.wzmforum.util.ArticleNotifyPrefs

/**
 * 新内容提醒前台服务。
 *
 * 业务场景：论坛/资讯 App 在后台运行时，周期性轮询首页文章列表，
 * 一旦发现比"上次已看到"更新的文章（id 更大），就通过通知提醒用户"有新的内容"。
 * 用户在 App 内浏览时基线会被重置，不会反复打扰。
 *
 * 要点：
 * 1. 前台服务 + 常驻通知，适合需要持续在后台运行的定时任务；
 * 2. START_STICKY：进程被系统回收后会尝试自动重建；
 * 3. 开机广播(BootReceiver)会拉起本服务，恢复监测。
 */
class NewArticleNotifyService : Service() {

    companion object {
        private const val CHANNEL_ID = "new_content_channel"
        private const val NOTIFICATION_ID = 3001
        private const val ALERT_NOTIFICATION_ID = 3002
        /** 轮询间隔（演示用 60 秒；正式产品建议加大到 10~30 分钟，或改用 WorkManager） */
        private const val POLL_INTERVAL_MILLIS = 60_000L
        /** 监测的频道：空字符串表示全部/首页（与 App 内 "0"→"" 的转换保持一致） */
        private const val CHANNEL_ID_PARAM = ""
        private const val PAGE_SIZE = "10"

        fun start(context: Context) {
            val intent = Intent(context, NewArticleNotifyService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())

    private val mPollTask = object : Runnable {
        override fun run() {
            pollNewArticles()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startAsForeground()
        mHandler.post(mPollTask)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        mHandler.removeCallbacks(mPollTask)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun pollNewArticles() {
        val token = UserManager.getInstance().token ?: ""
        val req = ReqArticleList(
            keyword = "",
            channel_id = CHANNEL_ID_PARAM,
            limit = PAGE_SIZE,
            page = "1",
            sort_field = "publishtime",
            sort_order = "desc"
        )
        ApiCall.enqueueSimple(
            HomeApiServiceProvider.apiService.articleList(token, req),
            object : ApiCall.ApiCallBack<ResArticleList> {
                override fun onSuccess(result: ResArticleList) {
                    if (result.code == 1 || result.code == 1001) {
                        handleResult(result)
                    }
                    mHandler.postDelayed(mPollTask, POLL_INTERVAL_MILLIS)
                }

                override fun onError(errorCode: Int, message: String) {
                    mHandler.postDelayed(mPollTask, POLL_INTERVAL_MILLIS)
                }
            }
        )
    }

    private fun handleResult(result: ResArticleList) {
        val list = result.list ?: return
        if (list.isEmpty()) return
        val maxId = list.maxOfOrNull { it.id } ?: return
        val lastSeen = ArticleNotifyPrefs.lastSeenId(this)

        when {
            lastSeen <= 0 -> {
                // 首次运行（或用户刚打开 App）：只建立基线，不提醒
                ArticleNotifyPrefs.updateLastSeen(this, maxId)
            }
            maxId > lastSeen -> {
                val newItems = list.filter { it.id > lastSeen }
                ArticleNotifyPrefs.updateLastSeen(this, maxId)
                showNewContentAlert(newItems.size, newItems.firstOrNull()?.title)
            }
        }
    }

    private fun showNewContentAlert(newCount: Int, latestTitle: String?) {
        val latest = if (latestTitle.isNullOrEmpty()) "" else "，最新：《$latestTitle》"
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle("有 $newCount 篇新内容")
            .setContentText("首页更新了 $newCount 篇内容$latest")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
            ?.notify(ALERT_NOTIFICATION_ID, notification)
    }

    private fun startAsForeground() {
        val notification = buildForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle("内容更新监测中")
            .setContentText("有新内容时第一时间提醒你")
            .setContentIntent(pi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "内容更新", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "首页有新内容时提醒"
            (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                ?.createNotificationChannel(channel)
        }
    }
}
