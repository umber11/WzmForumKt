package com.ls.librarybase.bean
/**
 * 收藏操作响应模型：包含 code / msg / time 字段。
 */
data class ResCollection(
    var code: Int = 0,
    var msg: String? = null,
    var time: String? = null
)
