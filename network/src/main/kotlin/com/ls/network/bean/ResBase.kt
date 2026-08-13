package com.ls.network.bean
/**
 * 网络响应统一基类：封装 code / msg / time / data 通用字段。
 */
data class ResBase<T>(
    var code: Int = 0,
    var msg: String? = null,
    var time: String? = null,
    var data: T? = null
)
