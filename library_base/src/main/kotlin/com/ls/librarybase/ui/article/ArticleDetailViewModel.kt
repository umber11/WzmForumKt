package com.ls.librarybase.ui.article

import android.util.Log
import com.ls.librarybase.api.ArticleDetailApiService
import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.ResArticleDetail
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.bean.ResCollection
import com.ls.librarybase.bean.ResCommentPostData
import com.ls.librarybase.bean.ResVote
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.utils.ArticleCache
import com.ls.librarybase.utils.CollectionStorage
import com.ls.librarybase.utils.CollectionTracker
import com.ls.librarybase.utils.LikeTracker
import com.ls.librarybase.utils.TimeUtils
import com.ls.librarybase.utils.ViewTracker
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayList

class ArticleDetailViewModel : BaseViewModel() {

    private val _mArticleDetail = MutableStateFlow<ResArticleDetail?>(null)
    val mArticleDetail: StateFlow<ResArticleDetail?> = _mArticleDetail.asStateFlow()
    private val _mCommentList = MutableStateFlow<List<ResArticleDetail.CommentListBean>>(emptyList())
    val mCommentList: StateFlow<List<ResArticleDetail.CommentListBean>> = _mCommentList.asStateFlow()
    private var mModel: ArticleDetailModel? = null
    private var mDetail: ResArticleDetail? = null

    fun loadArticle(articleId: String) {
        mModel = ArticleDetailModel(object : ApiCall.ApiCallBack<ResBase<ResArticleDetail>> {
            override fun onSuccess(result: ResBase<ResArticleDetail>) {
                showLoading(false)
                if (result.data != null) {
                    mDetail = result.data
                    val info = mDetail?.archivesInfo
                    if (info != null) {
                        LikeTracker.setLiked(info.id, info.islike == 1, info.likes)
                        ViewTracker.setViews(info.id, info.views)
                        CollectionTracker.setCollected(info.id, info.iscollection == 1, info.collection)
                        ArticleCache.put(info.id, info.title, info.image)
                    }
                    _mArticleDetail.value = mDetail
                    val commentList = mDetail?.commentList
                    if (commentList != null) {
                        _mCommentList.value = commentList
                    } else {
                        _mCommentList.value = ArrayList()
                    }
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showLoading(false)
            }
        })

        var token = UserManager.getInstance().token
        if (token == null) {
            token = ""
        }
        mModel!!.setToken(token)
        showLoading(true)
        mModel!!.loadArticleDetail(articleId)
    }

    fun postComment(content: String) {
        val info = mDetail?.archivesInfo ?: return

        val articleId = info.id.toString()
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "content" to content,
            "aid" to articleId
        )

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueCommon(api.commentPost(token, params), object : ApiCall.ApiCallBack<ResBase<ResCommentPostData>> {
            override fun onSuccess(result: ResBase<ResCommentPostData>) {
                val d = result.data
                if (d != null && d.comment != null) {
                    val newComment = d.comment ?: return
                    val list = _mCommentList.value?.toMutableList() ?: ArrayList()
                    list.add(0, newComment)
                    _mCommentList.value = list

                    val currentInfo = mDetail?.archivesInfo
                    if (currentInfo != null) {
                        updateDetail(currentInfo.copy(comments = currentInfo.comments + 1))
                    }
                    showToast("评论成功")
                } else {
                    showToast(result.msg ?: "评论失败")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showToast(message)
            }
        })
    }

    fun deleteComment(commentId: Int, position: Int) {
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "id" to commentId.toString()
        )

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueCommon(api.commentDelete(token, params), object : ApiCall.ApiCallBack<ResBase<Void>> {
            override fun onSuccess(result: ResBase<Void>) {
                val list = _mCommentList.value?.toMutableList()
                if (list != null && position >= 0 && position < list.size) {
                    list.removeAt(position)
                    _mCommentList.value = list
                }
                val currentInfo = mDetail?.archivesInfo
                if (currentInfo != null) {
                    updateDetail(currentInfo.copy(comments = Math.max(0, currentInfo.comments - 1)))
                }
                showToast("删除成功")
            }

            override fun onError(errorCode: Int, message: String) {
                showToast(message ?: "删除失败")
            }
        })
    }

    fun toggleLike() {
        val info = mDetail?.archivesInfo ?: return
        val wasLiked = info.islike == 1
        if (wasLiked) {
            unlike()
        } else {
            like()
        }
    }

    private fun updateDetail(newInfo: ResArticleDetail.ArchivesInfoBean) {
        val current = mDetail ?: return
        mDetail = current.copy(archivesInfo = newInfo)
        _mArticleDetail.value = mDetail
    }

    private fun like() {
        val info = mDetail?.archivesInfo ?: return
        val articleId = info.id.toString()
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "id" to articleId,
            "type" to "like"
        )

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueSimple(api.vote(token, params), object : ApiCall.ApiCallBack<ResVote> {
            override fun onSuccess(result: ResVote) {
                if (result != null && result.code == 1001) {
                    val currentInfo = mDetail?.archivesInfo ?: return
                    LikeTracker.setLiked(currentInfo.id, true, result.likes)
                    updateDetail(currentInfo.copy(islike = 1, likes = result.likes))
                } else {
                    showToast(result?.msg ?: "操作失败")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showToast(message)
            }
        })
    }

    private fun unlike() {
        val info = mDetail?.archivesInfo ?: return
        val articleId = info.id.toString()
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "aid" to articleId
        )

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueSimple(api.voteDel(token, params), object : ApiCall.ApiCallBack<ResVote> {
            override fun onSuccess(result: ResVote) {
                if (result != null && result.code == 1) {
                    val currentInfo = mDetail?.archivesInfo ?: return
                    val newLikes = Math.max(0, currentInfo.likes - 1)
                    LikeTracker.setLiked(currentInfo.id, false, newLikes)
                    updateDetail(currentInfo.copy(islike = 0, likes = newLikes))
                } else {
                    showToast(result?.msg ?: "操作失败")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showToast(message)
            }
        })
    }

    fun toggleCollection() {
        val info = mDetail?.archivesInfo ?: return
        val wasCollected = info.iscollection == 1
        if (wasCollected) {
            uncollect()
        } else {
            collect()
        }
    }

    private fun collect() {
        val info = mDetail?.archivesInfo ?: return
        val articleId = info.id.toString()
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "aid" to articleId,
            "type" to "archives"
        )

        Log.d("CollectionCreate", "aid=$articleId token=$token")

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueSimple(api.collectionCreate(token, params), object : ApiCall.ApiCallBack<ResCollection> {
            override fun onSuccess(result: ResCollection) {
                Log.d("CollectionCreate", "onSuccess code=${result?.code} msg=${result?.msg}")
                if (result != null && result.code == 1) {
                    val currentInfo = mDetail?.archivesInfo ?: return
                    val newInfo = currentInfo.copy(
                        iscollection = 1,
                        collection = currentInfo.collection + 1
                    )
                    CollectionTracker.setCollected(currentInfo.id, true, newInfo.collection)
                    val localBean = ResArticleList.ListBean()
                    localBean.id = newInfo.id
                    localBean.title = newInfo.title
                    localBean.image = newInfo.image
                    localBean.views = newInfo.views
                    localBean.likes = newInfo.likes
                    localBean.comments = newInfo.comments
                    localBean.publishtime = TimeUtils.convertTimestampToDate(newInfo.publishtime.toLong())
                    CollectionStorage.put(newInfo.id, localBean)
                    updateDetail(newInfo)
                } else {
                    showToast(result?.msg ?: "操作失败")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e("CollectionCreate", "onError code=$errorCode msg=$message")
                showToast(message)
            }
        })
    }

    private fun uncollect() {
        val info = mDetail?.archivesInfo ?: return
        val articleId = info.id.toString()
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "aid" to articleId
        )

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueSimple(api.collectionDelete(token, params), object : ApiCall.ApiCallBack<ResCollection> {
            override fun onSuccess(result: ResCollection) {
                if (result != null && result.code == 1) {
                    val currentInfo = mDetail?.archivesInfo ?: return
                    val newInfo = currentInfo.copy(
                        iscollection = 0,
                        collection = Math.max(0, currentInfo.collection - 1)
                    )
                    CollectionTracker.setCollected(currentInfo.id, false, newInfo.collection)
                    CollectionStorage.remove(currentInfo.id)
                    updateDetail(newInfo)
                } else {
                    showToast(result?.msg ?: "操作失败")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showToast(message)
            }
        })
    }
}
