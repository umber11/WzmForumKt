package com.ls.user.bean
/**
 * 更新用户资料请求体：avatar（头像）、nickname（昵称）、bio（简介）。
 */
data class ReqUpdateUserProfile(
    var avatar: String? = null,
    var nickname: String? = null,
    var bio: String? = null
)
