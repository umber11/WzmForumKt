package com.ls.librarybase.bean
/**
 * 用户基本信息模型：包含 id、昵称、简介、头像、用户名等字段。
 */
data class UserInfo(
    var id: String? = null,
    var nickname: String? = null,
    var bio: String? = null,
    var avatar: String? = null,
    var status: String? = null,
    var username: String? = null
)
