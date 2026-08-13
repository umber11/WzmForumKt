package com.ls.user.bean
/**
 * 账号密码登录响应数据：token（登录令牌）与 user_id（用户 id）。
 */
data class ResPasswordLogin(
    var token: String? = null,
    var user_id: Int = 0
)
