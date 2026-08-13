package com.ls.librarybase.navigation
/**
 * 导航路由常量：集中定义各页面的路由路径及拼接方法。
 */
object NavRoutes {
    const val MAIN = "main"

    const val ARTICLE_DETAIL = "article_detail/{articleId}"
    const val SEARCH = "search"

    const val LOGIN = "login"
    const val REGISTER = "register"
    const val AGREEMENT = "agreement/{type}"
    const val USER_CENTER = "user_center"
    const val USER_INFO_MENU = "user_info_menu"
    const val EDIT_USER_INFO = "edit_user_info"
    const val COMMENT = "comment"
    const val COLLECTION = "collection"
    const val ABOUT_ME = "about_me"
    const val CAMERA = "camera"

    fun articleDetail(articleId: String): String = "article_detail/$articleId"

    fun agreement(type: Int): String = "agreement/$type"
}
