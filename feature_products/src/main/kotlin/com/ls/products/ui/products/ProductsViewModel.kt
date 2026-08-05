package com.ls.products.ui.products

import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.products.bean.ResProductsCategory
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductsViewModel : BaseViewModel() {
    private val _mCategories = MutableStateFlow<List<ResProductsCategory.ListBean>?>(null)
    val mCategories: StateFlow<List<ResProductsCategory.ListBean>?> = _mCategories.asStateFlow()

    private val _mContent = MutableStateFlow<List<ResArticleList.ListBean>?>(null)
    val mContent: StateFlow<List<ResArticleList.ListBean>?> = _mContent.asStateFlow()

    private val _mSelectedCategoryId = MutableStateFlow("")

    private val mModel = ProductsModel()
    private var mPage = 1
    private var mIsLoadMore = false
    private val mAllContent = mutableListOf<ResArticleList.ListBean>()
    private val mCategoryIdSet = mutableSetOf<Int>()
    private var mCurrentSort = SortType.DEFAULT

    companion object {
        private const val PAGE_SIZE = 10
        private const val ALL_PAGE_SIZE = 50
    }

    fun loadCategories() {
        mModel.loadCategories(object : ApiCall.ApiCallBack<ResBase<ResProductsCategory>> {
            override fun onSuccess(result: ResBase<ResProductsCategory>) {
                val data = result.data ?: return
                val list = data.list ?: return
                val all = mutableListOf<ResProductsCategory.ListBean>()
                val allItem = ResProductsCategory.ListBean()
                allItem.id = 0
                allItem.name = "\u5168\u90e8"
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

    fun loadContent(isLoadMore: Boolean) {
        mIsLoadMore = isLoadMore
        if (!isLoadMore) {
            mAllContent.clear()
            mPage = 1
        }
        showLoading(true)

        var channelId = _mSelectedCategoryId.value
        val isAll = "0" == channelId
        if (isAll) {
            channelId = ""
        }

        val pageSize = if (isAll) ALL_PAGE_SIZE else PAGE_SIZE
        mModel.loadContent("", channelId, mPage, pageSize, "publishtime", "desc",
            object : ApiCall.ApiCallBack<ResArticleList> {
                override fun onSuccess(result: ResArticleList) {
                    showLoading(false)
                    mIsLoadMore = false
                    val list = result.list
                    if (list != null) {
                        var filteredList = list
                        if (isAll && mCategoryIdSet.isNotEmpty()) {
                            val filtered = mutableListOf<ResArticleList.ListBean>()
                            for (article in filteredList) {
                                if (mCategoryIdSet.contains(article.channel_id)) {
                                    filtered.add(article)
                                }
                            }
                            filteredList = filtered
                        }
                        if (!isLoadMore) {
                            mAllContent.clear()
                        }
                        mAllContent.addAll(filteredList)
                        sortContent()
                        mPage++
                    }
                }

                override fun onError(errorCode: Int, message: String) {
                    showLoading(false)
                    mIsLoadMore = false
                }
            })
    }

    fun onCategorySelected(channelId: String) {
        _mSelectedCategoryId.value = channelId
        loadContent(false)
    }

    fun refresh() {
        loadCategories()
    }

    fun isLoadMore(): Boolean = mIsLoadMore

    fun applySort(sortType: SortType) {
        mCurrentSort = sortType
        sortContent()
    }

    private fun sortContent() {
        val sorted = ArrayList(mAllContent)
        when (mCurrentSort) {
            SortType.VIEWS ->
                Collections.sort(sorted) { a, b -> Integer.compare(b.views, a.views) }
            SortType.PUBLISHTIME ->
                Collections.sort(sorted) { a, b -> (b.publishtime ?: "").compareTo(a.publishtime ?: "") }
            SortType.DEFAULT -> {}
        }
        _mContent.value = sorted
    }
}
