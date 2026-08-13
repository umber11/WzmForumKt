package com.ls.user.ui.comment

import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.utils.ArticleCache
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.user.bean.ResComments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * 评论列表 ViewModel，管理评论列表状态并支持删除评论。
 */
class CommentViewModel : BaseViewModel() {

    private val _mCommentList = MutableStateFlow<List<ResComments.ListBean>?>(null)
    val mCommentList: StateFlow<List<ResComments.ListBean>?> = _mCommentList.asStateFlow()

    private val mModel = CommentModel()
    private var mCurrentList: MutableList<ResComments.ListBean>? = null

    fun loadCommentList() {
        showLoading(true)
        mModel.loadCommentList(1, 20, object : CommentModel.CommentListCallback {
            override fun onSuccess(result: ResComments) {
                showLoading(false)
                val resultList = result.list
                if (resultList != null) {
                    mCurrentList = resultList.toMutableList()
                    for (item in mCurrentList!!) {
                        val archives = item.archives
                        val needsCache = archives == null
                                || archives.image == null
                                || archives.title == null
                        if (needsCache && item.aid > 0) {
                            val title = ArticleCache.getTitle(item.aid)
                            val image = ArticleCache.getImage(item.aid)
                            if (title != null || image != null) {
                                val archives = ResComments.ListBean.ArchivesBean()
                                archives.id = item.aid
                                archives.title = title
                                archives.image = image
                                item.archives = archives
                            }
                        }
                    }
                    _mCommentList.value = ArrayList(mCurrentList!!)
                } else {
                    _mCommentList.value = ArrayList()
                }
            }

            override fun onArticleDetailLoaded() {
                _mCommentList.value = ArrayList(mCurrentList!!)
            }

            override fun onError(errorCode: Int, message: String) {
                showLoading(false)
            }
        })
    }

    fun deleteComment(commentId: Int) {
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf("id" to commentId.toString())

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueCommon(api.commentDelete(token, params), object : ApiCall.ApiCallBack<ResBase<Void>> {
            override fun onSuccess(result: ResBase<Void>) {
                // 按 commentId 匹配删除，避免列表变化时删错位置
                if (mCurrentList != null) {
                    val index = mCurrentList!!.indexOfFirst { it.id == commentId }
                    if (index >= 0) {
                        mCurrentList!!.removeAt(index)
                        _mCommentList.value = ArrayList(mCurrentList!!)
                    }
                }
                showToast("删除成功")
            }

            override fun onError(errorCode: Int, message: String) {
                showToast(message ?: "删除失败")
            }
        })
    }
}
