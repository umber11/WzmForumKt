package com.ls.user.bean
/**
 * 发送短信验证码请求体：mobile（手机号）与 event（事件类型）。
 */
data class ReqSendSmsCode(
    val mobile: String,
    val event: String
)
