package com.ls.librarybase.bean
/**
 * 投票/点赞响应模型：包含赞与踩数量、时间戳及附加数据。
 */
data class ResVote(
    var code: Int = 0,
    var msg: String? = null,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var time: Long = 0L,
    var data: Any? = null
)
