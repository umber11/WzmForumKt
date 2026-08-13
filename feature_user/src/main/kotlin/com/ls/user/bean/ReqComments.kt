package com.ls.user.bean
/**
 * 获取我的评论列表请求体：page（页码）与 limit（每页条数）。
 */
data class ReqComments(
    val page: String,
    val limit: String
)
