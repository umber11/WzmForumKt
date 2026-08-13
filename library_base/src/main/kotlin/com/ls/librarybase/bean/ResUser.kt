package com.ls.librarybase.bean
/**
 * 用户信息响应模型：包含用户详情、粉丝数、关注数与勋章数。
 */
data class ResUser(
    var user: UserInfo? = null,
    var fans: Int = 0,
    var follow: Int = 0,
    var medal: Int = 0
)
