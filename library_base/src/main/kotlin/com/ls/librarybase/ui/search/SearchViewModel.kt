package com.ls.librarybase.ui.search

import androidx.lifecycle.ViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.utils.LikeTracker
import com.ls.network.ApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayList

/**
 * 搜索页ViewModel
 */
class SearchViewModel : ViewModel() {

    private val mSearchModel = SearchModel()
    private val mArticleList = ArrayList<ResArticleList.ListBean>()
    private var mPage = 1
    private var mTotalCount = 0
    private var mIsLoadMore = false
    private var mHasMoreFlag = true
    private var mSortField = "default"
    private var mSortOrder = "desc"
    private var mRequestId = 0

    private val _mKeyword = MutableStateFlow("")
    val mKeyword: StateFlow<String> = _mKeyword.asStateFlow()
    private val _mArticles = MutableStateFlow<List<ResArticleList.ListBean>>(emptyList())
    val mArticles: StateFlow<List<ResArticleList.ListBean>> = _mArticles.asStateFlow()
    private val _mHasMore = MutableStateFlow(true)
    val mHasMore: StateFlow<Boolean> = _mHasMore.asStateFlow()
    private val _mToast = MutableStateFlow<String?>(null)
    val mToast: StateFlow<String?> = _mToast.asStateFlow()

    fun performSearch(keyword: String) {
        _mKeyword.value = keyword
        val kw = keyword.trim()
        if (kw.isEmpty()) {
            _mToast.value = "请输入关键词"
            return
        }
        mPage = 1
        mHasMoreFlag = true
        mArticleList.clear()
        _mArticles.value = emptyList()
        loadArticles(isLoadMore = false)
    }

    fun setSort(field: String, order: String) {
        if (mSortField == field && mSortOrder == order) return
        mSortField = field
        mSortOrder = order
        val kw = _mKeyword.value.trim()
        if (kw.isNotEmpty()) {
            mPage = 1
            mHasMoreFlag = true
            mArticleList.clear()
            _mArticles.value = emptyList()
            loadArticles(isLoadMore = false)
        }
    }

    fun loadMore() {
        if (!mHasMoreFlag || mIsLoadMore) return
        loadArticles(isLoadMore = true)
    }

    private fun loadArticles(isLoadMore: Boolean) {
        val kw = _mKeyword.value.trim()
        if (kw.isEmpty()) return

        val requestId = ++mRequestId
        mIsLoadMore = isLoadMore

        mSearchModel.search(kw, "", mPage, PAGE_SIZE, mSortField, mSortOrder,
            object : ApiCall.ApiCallBack<ResArticleList> {
                override fun onSuccess(result: ResArticleList) {
                    if (requestId != mRequestId) return
                    mIsLoadMore = false
                    val list = result.list
                    if (list != null) {
                        mTotalCount = result.count
                        for (item in list) {
                            if (item.islike == 1) {
                                LikeTracker.setLiked(item.id, true, item.likes)
                            }
                        }
                        mArticleList.addAll(list)
                        _mArticles.value = ArrayList(mArticleList)
                        mHasMoreFlag = mArticleList.size < mTotalCount
                        _mHasMore.value = mHasMoreFlag
                        mPage++
                    }
                }

                override fun onError(errorCode: Int, message: String) {
                    if (requestId != mRequestId) return
                    mIsLoadMore = false
                    _mToast.value = message
                }
            })
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
