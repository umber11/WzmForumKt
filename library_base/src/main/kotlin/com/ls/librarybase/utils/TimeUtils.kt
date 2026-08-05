package com.ls.librarybase.utils

import java.text.SimpleDateFormat
import java.util.Date

object TimeUtils {
    fun convertTimestampToDate(timestamp: Long): String {
        val time = timestamp * 1000
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(time)
        return sdf.format(date)
    }
}
