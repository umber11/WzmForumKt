package com.ls.librarybase.bean

data class ResVote(
    var code: Int = 0,
    var msg: String? = null,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var time: Long = 0L,
    var data: Any? = null
)
