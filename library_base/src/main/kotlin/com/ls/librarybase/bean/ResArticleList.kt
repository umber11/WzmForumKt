package com.ls.librarybase.bean
/**
 * 文章列表响应模型：包含列表总数、排序字段与文章列表数据。
 */
data class ResArticleList(
    var code: Int = 0,
    var msg: String? = null,
    var count: Int = 0,
    var sort_field: String? = null,
    var sort_order: String? = null,
    var list: List<ListBean>? = null
) {
    data class ListBean(
        var id: Int = 0,
        var special_ids: String? = null,
        var channel_id: Int = 0,
        var title: String? = null,
        var image: String? = null,
        var images: String? = null,
        var views: Int = 0,
        var comments: Int = 0,
        var likes: Int = 0,
        var dislikes: Int = 0,
        var islike: Int = 0,
        var iscollection: Int = 0,
        var publishtime: String? = null
    )
}
