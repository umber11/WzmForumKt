package com.ls.home.bean
/**
 * 首页文章列表请求参数实体类。
 */
data class ReqArticleList(
    var keyword: String?,
    var channel_id: String?,
    var limit: String?,
    var page: String?,
    var sort_field: String?,
    var sort_order: String?
)
