package com.ls.news.bean

data class ResNewsCategory(
    var count: Int = 0,
    var list: List<ListBean>? = null
) {
    data class ListBean(
        var id: Int = 0,
        var type: String? = null,
        var model_id: Int = 0,
        var parent_id: Int = 0,
        var name: String? = null,
        var image: String? = null,
        var description: String? = null,
        var weigh: Int = 0
    )
}
