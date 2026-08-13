package com.ls.librarybase.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    /**
     * 秒级时间戳转日期字符串。
     * @param timestamp 秒级时间戳（如服务端返回的 publishtime）。
     * @param isMillis 若传入的是毫秒级时间戳则置 true。
     */
    fun convertTimestampToDate(timestamp: Long, isMillis: Boolean = false): String {
        val time = if (isMillis) timestamp else timestamp * 1000
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(time))
    }
}
