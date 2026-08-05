package com.ls.home.bean

data class ResHomeCategory(
    var channel_id: Int = 0,
    var channel: List<ChannelBean>? = null
) {
    data class ChannelBean(
        var id: Int = 0,
        var type: String? = null,
        var model_id: Int = 0,
        var parent_id: Int = 0,
        var name: String? = null,
        var image: String? = null
    )
}
