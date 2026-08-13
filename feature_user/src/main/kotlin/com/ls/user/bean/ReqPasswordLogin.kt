package com.ls.user.bean
/**
 * 账号密码登录请求体：account（账号）与 password（密码）。
 */
data class ReqPasswordLogin(
    val account: String,
    val password: String
)
