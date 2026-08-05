package com.ls.user.bean

data class ReqUpdateUserProfile(
    var avatar: String? = null,
    var nickname: String? = null,
    var bio: String? = null
)
