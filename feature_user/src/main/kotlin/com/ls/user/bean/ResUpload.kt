package com.ls.user.bean
/**
 * 文件上传响应数据：url（相对路径）与 fullurl（完整访问路径）。
 */
data class ResUpload(
    var url: String? = null,
    var fullurl: String? = null
)
