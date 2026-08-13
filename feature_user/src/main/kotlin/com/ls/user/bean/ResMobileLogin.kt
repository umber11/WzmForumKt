package com.ls.user.bean
/**
 * 手机号登录响应数据：token（登录令牌）与 id（用户 id）。
 */
data class ResMobileLogin(
    var token: String? = null,
    var id: Int = 0
)
