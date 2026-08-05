package com.ls.librarybase.bean

data class ResUser(
    var user: UserInfo? = null,
    var fans: Int = 0,
    var follow: Int = 0,
    var medal: Int = 0
)
