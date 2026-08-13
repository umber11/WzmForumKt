package com.ls.user.bean
/**
 * 我的评论列表响应数据，包含评论内容及关联文章信息。
 */
data class ResComments(
    var list: List<ListBean>? = null
) {
    data class ListBean(
        var id: Int = 0,
        var user_id: Int = 0,
        var pid: Int = 0,
        var content: String? = null,
        var comments: Int = 0,
        var createtime: Int = 0,
        var user: UserBean? = null,
        var create_date: String? = null,
        var aid: Int = 0,
        var archives: ArchivesBean? = null
    ) {
        data class UserBean(
            var id: Int = 0,
            var nickname: String? = null,
            var avatar: String? = null,
            var url: String? = null
        )

        data class ArchivesBean(
            var id: Int = 0,
            var title: String? = null,
            var image: String? = null
        )
    }
}
