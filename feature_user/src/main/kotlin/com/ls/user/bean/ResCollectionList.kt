package com.ls.user.bean

/**
 * 我的收藏列表（对应 addons/cms/api.collection/index 响应 data 结构）
 */
data class ResCollectionList(
    var collectionList: CollectionListBean? = null,
    var model: Any? = null
) {
    data class CollectionListBean(
        var total: Int = 0,
        var per_page: Int = 0,
        var current_page: Int = 0,
        var last_page: Int = 0,
        var data: List<ItemBean>? = null
    )

    data class ItemBean(
        var id: Int = 0,
        var type: String? = null,
        var aid: Int = 0,
        var user_id: Int = 0,
        var title: String? = null,
        var image: String? = null,
        var url: String? = null,
        var createtime: Int = 0,
        var updatetime: Int = 0,
        var tag: String? = null,
        var create_date: String? = null
    )
}
