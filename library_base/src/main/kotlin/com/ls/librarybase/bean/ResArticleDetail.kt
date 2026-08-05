package com.ls.librarybase.bean

data class ResArticleDetail(
    var archivesInfo: ArchivesInfoBean? = null,
    var __token__: String? = null,
    var commentList: List<CommentListBean>? = null
) {
    data class ArchivesInfoBean(
        var id: Int = 0,
        var user_id: Int = 0,
        var channel_id: Int = 0,
        var channel_ids: String? = null,
        var model_id: Int = 0,
        var special_ids: String? = null,
        var title: String? = null,
        var flag: String? = null,
        var style: String? = null,
        var image: String? = null,
        var images: String? = null,
        var video_file: Any? = null,
        var seotitle: String? = null,
        var keywords: String? = null,
        var description: String? = null,
        var tags: String? = null,
        var price: String? = null,
        var outlink: String? = null,
        var views: Int = 0,
        var comments: Int = 0,
        var likes: Int = 0,
        var dislikes: Int = 0,
        var collection: Int = 0,
        var diyname: String? = null,
        var isguest: Int = 0,
        var iscomment: Int = 0,
        var createtime: Int = 0,
        var updatetime: Int = 0,
        var publishtime: Int = 0,
        var memo: String? = null,
        var duration: Any? = null,
        var content: String? = null,
        var author: String? = null,
        var islike: Int = 0,
        var iscollection: Int = 0,
        var user: Any? = null,
        var channel: ChannelBean? = null,
        var url: String? = null,
        var fullurl: String? = null,
        var likeratio: String? = null,
        var create_date: String? = null,
        var ispaid: Boolean = false,
        var taglist: List<*>? = null
    ) {
        data class ChannelBean(
            var id: Int = 0,
            var parent_id: Int = 0,
            var name: String? = null,
            var image: String? = null,
            var diyname: String? = null,
            var items: Int = 0,
            var url: String? = null,
            var fullurl: String? = null
        )
    }

    data class CommentListBean(
        var id: Int = 0,
        var user_id: Int = 0,
        var pid: Int = 0,
        var content: String? = null,
        var comments: Int = 0,
        var createtime: Int = 0,
        var user: UserBean? = null,
        var create_date: String? = null
    ) {
        data class UserBean(
            var id: Int = 0,
            var nickname: String? = null,
            var avatar: String? = null,
            var bio: String? = null,
            var email: String? = null,
            var url: String? = null
        )
    }
}
