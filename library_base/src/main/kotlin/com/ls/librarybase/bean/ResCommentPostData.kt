package com.ls.librarybase.bean

import com.google.gson.annotations.SerializedName
/**
 * 提交评论的响应数据模型：包含 token 与评论内容。
 */
data class ResCommentPostData(
    @SerializedName("__token__")
    var token: String? = null,
    var comment: ResArticleDetail.CommentListBean? = null
)
