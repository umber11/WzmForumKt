package com.ls.network.bean

data class ResBase<T>(
    var code: Int = 0,
    var msg: String? = null,
    var time: String? = null,
    var data: T? = null
)
