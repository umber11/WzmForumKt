package com.ls.librarybase.bean

import com.google.gson.annotations.SerializedName

data class ResCommentPostData(
    @SerializedName("__token__")
    var token: String? = null,
    var comment: ResArticleDetail.CommentListBean? = null
)
