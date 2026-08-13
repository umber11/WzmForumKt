package com.ls.librarybase.ui.article

import android.util.Log
import com.ls.librarybase.api.ArticleDetailApiService
import com.ls.librarybase.api.ArticleDetailApiServiceProvider
import com.ls.librarybase.base.BaseApplication
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
import com.ls.librarybase.utils.NetworkUtils
import com.ls.librarybase.utils.OfflineCache
import com.ls.librarybase.utils.PendingSyncManager
import com.ls.librarybase.utils.TimeUtils
import com.ls.librarybase.utils.ViewTracker
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayList
/**
 * 文章详情页 ViewModel：管理文章详情、评论、点赞收藏等状态与离线缓存。
 */
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
                    // 离线缓存：保存最近一次加载的文章详情
                    OfflineCache.saveDetail(BaseApplication.getContext(), articleId, result.data!!)
                    val info = mDetail?.archivesInfo
                    if (info != null) {
                        LikeTracker.setLiked(info.id, info.islike == 1, info.likes)
                        // 详情已经请求过（带 token 时会返回 islike），标记为已探测，
                        // 避免列表 LikeSyncHelper 再次调用 detail 接口导致浏览量被重复 +1
                        LikeTracker.markChecked(info.id)
                        ViewTracker.setViews(info.id, info.views)
                        CollectionTracker.setCollected(info.id, info.iscollection == 1)
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
                // 离线时展示缓存的文章详情，实现"离线可读"
                if (!NetworkUtils.isNetworkAvailable()) {
                    val cached = OfflineCache.loadDetail(BaseApplication.getContext(), articleId)
                    if (cached != null) {
                        mDetail = cached
                        _mArticleDetail.value = cached
                        _mCommentList.value = cached.commentList ?: ArrayList()
                        showToast("网络不可用，当前为离线缓存内容")
                    }
                }
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

        if (!UserManager.getInstance().isLogin()) {
            showToast("请先登录")
            return
        }
        if (info.iscomment == 0) {
            showToast("该文章已关闭评论")
            return
        }
        if (!NetworkUtils.isNetworkAvailable()) {
            showToast("网络连接不可用，请检查网络")
            return
        }

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

    fun deleteComment(commentId: Int) {
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "id" to commentId.toString()
        )

        val api = ArticleDetailApiServiceProvider.getApiService()
        ApiCall.enqueueCommon(api.commentDelete(token, params), object : ApiCall.ApiCallBack<ResBase<Void>> {
            override fun onSuccess(result: ResBase<Void>) {
                // 按 commentId 匹配删除，避免列表在请求期间变化时删错位置
                val list = _mCommentList.value?.toMutableList()
                if (list != null) {
                    val index = list.indexOfFirst { it.id == commentId }
                    if (index >= 0) {
                        list.removeAt(index)
                        _mCommentList.value = list
                    }
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
        if (!UserManager.getInstance().isLogin()) {
            showToast("请先登录")
            return
        }
        if (!NetworkUtils.isNetworkAvailable()) {
            // 离线：乐观更新本地状态 + 记入待同步队列，联网后自动补传
            optimisticToggleLike()
            return
        }
        val wasLiked = info.islike == 1
        if (wasLiked) {
            unlike()
        } else {
            like()
        }
    }

    /**
     * 离线点赞/取消点赞：先改本地 UI（LikeTracker + 详情状态），
     * 并记录最终期望状态到待同步队列。
     */
    private fun optimisticToggleLike() {
        val info = mDetail?.archivesInfo ?: return
        val newLiked = info.islike != 1
        val newCount = if (newLiked) info.likes + 1 else Math.max(0, info.likes - 1)
        LikeTracker.setLiked(info.id, newLiked, newCount)
        updateDetail(info.copy(islike = if (newLiked) 1 else 0, likes = newCount))
        PendingSyncManager.setLike(BaseApplication.getContext(), info.id, newLiked)
        showToast("网络不可用，已离线保存，联网后自动同步")
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
        if (!UserManager.getInstance().isLogin()) {
            showToast("请先登录")
            return
        }
        if (!NetworkUtils.isNetworkAvailable()) {
            // 离线：乐观更新本地状态（含收藏列表）+ 记入待同步队列
            optimisticToggleCollection()
            return
        }
        val wasCollected = info.iscollection == 1
        if (wasCollected) {
            uncollect()
        } else {
            collect()
        }
    }

    /**
     * 离线收藏/取消收藏：先改本地 UI（CollectionTracker + CollectionStorage + 详情状态），
     * 并记录最终期望状态到待同步队列。
     */
    private fun optimisticToggleCollection() {
        val info = mDetail?.archivesInfo ?: return
        val newCollected = info.iscollection != 1
        val newCount = if (newCollected) info.collection + 1 else Math.max(0, info.collection - 1)
        CollectionTracker.setCollected(info.id, newCollected)
        updateDetail(info.copy(iscollection = if (newCollected) 1 else 0, collection = newCount))

        val localBean = ResArticleList.ListBean()
        localBean.id = info.id
        localBean.title = info.title
        localBean.image = info.image
        localBean.views = info.views
        localBean.likes = info.likes
        localBean.comments = info.comments
        localBean.publishtime = TimeUtils.convertTimestampToDate(info.publishtime.toLong())
        if (newCollected) {
            CollectionStorage.put(info.id, localBean)
        } else {
            CollectionStorage.remove(info.id)
        }

        PendingSyncManager.setCollect(BaseApplication.getContext(), info.id, newCollected)
        showToast("网络不可用，已离线保存，联网后自动同步")
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
                    CollectionTracker.setCollected(currentInfo.id, true)
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
                    CollectionTracker.setCollected(currentInfo.id, false)
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
