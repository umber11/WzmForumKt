package com.ls.home.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.ls.home.bean.ResHomeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.librarybase.utils.LikeSyncHelper
import com.ls.librarybase.utils.LikeTracker
import com.ls.librarybase.utils.OfflineCache
import com.ls.librarybase.utils.ViewTracker
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
/**
 * 首页 ViewModel：分类与文章分页加载、离线缓存、点赞状态同步、登录登出刷新。
 */
class HomeViewModel : BaseViewModel() {

    private val _mCategories = MutableStateFlow<List<ResHomeCategory.ChannelBean>?>(null)
    val mCategories: StateFlow<List<ResHomeCategory.ChannelBean>?> = _mCategories.asStateFlow()

    private val _mArticles = MutableStateFlow<List<ResArticleList.ListBean>?>(null)
    val mArticles: StateFlow<List<ResArticleList.ListBean>?> = _mArticles.asStateFlow()

    private val _mSelectedCategoryId = MutableStateFlow("")

    private val mModel = HomeModel()
    private var mPage = 1
    private var mIsLoadMore = false
    /** 请求序号：每次发新请求自增，旧响应到达时与当前序号不一致则直接丢弃，避免乱序覆盖列表 */
    private var mRequestId = 0
    private var mAllArticles: MutableList<ResArticleList.ListBean> = mutableListOf()
    private var mLastLoginState = UserManager.getInstance().isLogin()

    init {
        viewModelScope.launch {
            LoginStateBus.loginState.collect { isLogin ->
                if (isLogin && !mLastLoginState) {
                    refresh()
                } else if (!isLogin && mLastLoginState) {
                    onLogout()
                }
                mLastLoginState = isLogin
            }
        }
    }

    private fun onLogout() {
        Log.d(TAG, "onLogout: clear like tracker and reset islike")
        LikeTracker.clear()
        val current = _mArticles.value ?: return
        _mArticles.value = current.map { item ->
            if (item.islike != 0) item.copy(islike = 0) else item
        }
    }

    fun loadCategories() {
        showLoading(true)
        mModel.loadCategories(object : ApiCall.ApiCallBack<ResBase<ResHomeCategory>> {
            override fun onSuccess(result: ResBase<ResHomeCategory>) {
                showLoading(false)
                val data = result.data
                Log.d(TAG, "loadCategories onSuccess, data=${if (data != null) "notNull channel=${data.channel?.size ?: "null"}" else "null"}")
                val channel = data?.channel
                if (channel != null) {
                    val all = mutableListOf<ResHomeCategory.ChannelBean>()
                    val allItem = ResHomeCategory.ChannelBean()
                    allItem.id = 0
                    allItem.name = "全部"
                    all.add(allItem)
                    all.addAll(channel)
                    Log.d(TAG, "loadCategories total categories count=${all.size}")
                    _mCategories.value = all
                    // 离线缓存：保存最近一次的分类（含"全部"）
                    OfflineCache.put(BaseApplication.getContext(), "category_home", all)
                } else {
                    Log.e(TAG, "loadCategories: data or channel is null")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showLoading(false)
                Log.e(TAG, "loadCategories onError: code=$errorCode msg=$message")
                // 请求失败（断网或服务器异常）时展示缓存的分类，保证 Tab 能正常显示并触发文章加载
                val cached = OfflineCache.get(
                    BaseApplication.getContext(), "category_home",
                    object : TypeToken<List<ResHomeCategory.ChannelBean>>() {})
                if (!cached.isNullOrEmpty()) {
                    _mCategories.value = cached
                }
            }
        })
    }

    fun loadArticles(isLoadMore: Boolean) {
        val requestId = ++mRequestId
        mIsLoadMore = isLoadMore
        if (!isLoadMore) {
            mPage = 1
        }
        showLoading(true)
        var channelId = _mSelectedCategoryId.value
        if ("0" == channelId) {
            channelId = ""
        }
        Log.d(TAG, "loadArticles: isLoadMore=$isLoadMore page=$mPage channelId=$channelId")
        mModel.loadArticles("", channelId ?: "",
            mPage, PAGE_SIZE, object : ApiCall.ApiCallBack<ResArticleList> {
                override fun onSuccess(result: ResArticleList) {
                    // 旧请求响应：已被更新的请求取代，直接丢弃，避免乱序覆盖列表导致"只剩末页几篇"
                    if (requestId != mRequestId) return
                    showLoading(false)
                    mIsLoadMore = false
                    Log.d(TAG, "loadArticles onSuccess: code=${result.code} list=${result.list?.size ?: "null"} count=${result.count}")
                    val list = result.list
                    if (list != null) {
                        for (item in list) {
                            if (item.islike == 1) {
                                LikeTracker.setLiked(item.id, true, item.likes)
                            }
                        }
                        if (isLoadMore) {
                            mAllArticles.addAll(list)
                        } else {
                            mAllArticles = list.toMutableList()
                        }
                        _mArticles.value = ArrayList(mAllArticles)
                        mPage++
                        Log.d(TAG, "loadArticles: mArticles set with ${mAllArticles.size} items, page=$mPage")
                        syncLikedState()
                        // 离线缓存：保存最近一次的文章列表
                        OfflineCache.saveList(BaseApplication.getContext(), "home", channelId, mAllArticles)
                    } else {
                        Log.e(TAG, "loadArticles: result list is null")
                    }
                }

                override fun onError(errorCode: Int, message: String) {
                    if (requestId != mRequestId) return
                    showLoading(false)
                    mIsLoadMore = false
                    Log.e(TAG, "loadArticles onError: code=$errorCode msg=$message")
                    // 请求失败（断网或服务器异常）时展示缓存的文章列表，实现"离线可读"
                    val cached = OfflineCache.loadList(BaseApplication.getContext(), "home", channelId)
                    if (!cached.isNullOrEmpty()) {
                        mAllArticles = cached.toMutableList()
                        _mArticles.value = ArrayList(mAllArticles)
                        syncLikedState()
                    }
                }
            })
    }

    fun onCategorySelected(channelId: String) {
        Log.d(TAG, "onCategorySelected: channelId=$channelId")
        _mSelectedCategoryId.value = channelId
        loadArticles(false)
    }

    fun refresh() {
        loadCategories()
    }

    private fun syncLikedState() {
        val ids = mAllArticles.map { it.id }
        if (ids.isEmpty()) return
        viewModelScope.launch {
            LikeSyncHelper.sync(ids) { id, islike, likes ->
                val current = _mArticles.value ?: return@sync
                _mArticles.value = current.map { item ->
                    if (item.id == id) item.copy(islike = islike, likes = likes) else item
                }
            }
        }
    }

    fun refreshFromTrackers() {
        val current = _mArticles.value ?: return
        val updated = current.map { item ->
            val likeCount = LikeTracker.getLikeCount(item.id)
            val views = ViewTracker.getViews(item.id)
            if (likeCount != null || views != null) {
                item.copy(likes = likeCount ?: item.likes, views = views ?: item.views)
            } else {
                item
            }
        }
        _mArticles.value = updated
    }

    fun isLoadMore() = mIsLoadMore

    companion object {
        private const val TAG = "HomeViewModel"
        private const val PAGE_SIZE = 10
    }
}
