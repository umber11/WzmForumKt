package com.ls.user.bean
/**
 * 手机号验证码登录请求体：mobile（手机号）与 captcha（短信验证码）。
 */
data class ReqMobileLogin(
    val mobile: String,
    val captcha: String
)
