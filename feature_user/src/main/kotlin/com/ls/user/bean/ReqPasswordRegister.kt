package com.ls.user.bean
/**
 * 账号密码注册请求体：username（用户名）与 password（密码）。
 */
data class ReqPasswordRegister(
    val username: String,
    val password: String
)
