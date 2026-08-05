package com.ls.news.ui.news

import androidx.lifecycle.viewModelScope
import android.util.Log
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.librarybase.utils.LikeSyncHelper
import com.ls.librarybase.utils.LikeTracker
import com.ls.librarybase.utils.ViewTracker
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.news.bean.ResNewsCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel : BaseViewModel() {
    private val _mCategories = MutableStateFlow<List<ResNewsCategory.ListBean>?>(null)
    val mCategories: StateFlow<List<ResNewsCategory.ListBean>?> = _mCategories.asStateFlow()

    private val _mArticles = MutableStateFlow<List<ResArticleList.ListBean>?>(null)
    val mArticles: StateFlow<List<ResArticleList.ListBean>?> = _mArticles.asStateFlow()

    private val _mSelectedCategoryId = MutableStateFlow("")

    private val mModel = NewsModel()
    private var mPage = 1
    private var mIsLoadMore = false
    private var mAllArticles: MutableList<ResArticleList.ListBean> = mutableListOf()
    private val mCategoryIdSet = mutableSetOf<Int>()
    private var mCurrentSort = SortType.DEFAULT
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
        mModel.loadCategories(object : ApiCall.ApiCallBack<ResBase<ResNewsCategory>> {
            override fun onSuccess(result: ResBase<ResNewsCategory>) {
                val data = result.data ?: return
                val list = data.list ?: return
                val all = mutableListOf<ResNewsCategory.ListBean>()
                val allItem = ResNewsCategory.ListBean()
                allItem.id = 0
                allItem.name = "全部"
                all.add(allItem)
                all.addAll(list)
                _mCategories.value = all
                mCategoryIdSet.clear()
                for (category in list) {
                    mCategoryIdSet.add(category.id)
                }
            }

            override fun onError(errorCode: Int, message: String) {
            }
        })
    }

    fun loadArticles(isLoadMore: Boolean) {
        mIsLoadMore = isLoadMore
        if (!isLoadMore) {
            mPage = 1
        }
        showLoading(true)

        var channelId = _mSelectedCategoryId.value
        val isAll = "0" == channelId
        if (isAll) {
            channelId = ""
        }

        val pageSize = if (isAll) ALL_PAGE_SIZE else PAGE_SIZE
        mModel.loadArticles("", channelId ?: "", mPage, pageSize, "publishtime", "desc",
            object : ApiCall.ApiCallBack<ResArticleList> {
                override fun onSuccess(result: ResArticleList) {
                    showLoading(false)
                    mIsLoadMore = false
                    val articleList = result.list ?: return
                    var list = articleList
                    if (isAll && mCategoryIdSet.isNotEmpty()) {
                        val filtered = mutableListOf<ResArticleList.ListBean>()
                        for (article in list) {
                            if (mCategoryIdSet.contains(article.channel_id)) {
                                filtered.add(article)
                            }
                        }
                        list = filtered
                    }
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
                    sortArticles()
                    mPage++
                    syncLikedState()
                }

                override fun onError(errorCode: Int, message: String) {
                    showLoading(false)
                    mIsLoadMore = false
                }
            })
    }

    fun onCategorySelected(channelId: String) {
        _mSelectedCategoryId.value = channelId
        loadArticles(false)
    }

    fun refresh() {
        loadCategories()
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

    fun isLoadMore() = mIsLoadMore

    fun applySort(sortType: SortType) {
        mCurrentSort = sortType
        sortArticles()
    }

    private fun sortArticles() {
        val sorted = ArrayList(mAllArticles ?: mutableListOf<ResArticleList.ListBean>())
        when (mCurrentSort) {
            SortType.VIEWS -> {
                sorted.sortWith { a, b -> Integer.compare(b.views, a.views) }
            }
            SortType.PUBLISHTIME -> {
                sorted.sortWith { a, b ->
                    val aTime = a.publishtime ?: ""
                    val bTime = b.publishtime ?: ""
                    bTime.compareTo(aTime)
                }
            }
            SortType.DEFAULT -> {}
        }
        _mArticles.value = sorted
    }

    companion object {
        private const val TAG = "NewsViewModel"
        private const val PAGE_SIZE = 10
        private const val ALL_PAGE_SIZE = 50
    }
}
