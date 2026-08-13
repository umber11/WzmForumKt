package com.ls.products.ui.products

import com.google.gson.reflect.TypeToken
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.utils.OfflineCache
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.products.bean.ResProductsCategory
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * 产品 ViewModel：分类与内容分页加载、排序筛选、离线缓存。
 */
class ProductsViewModel : BaseViewModel() {
    private val _mCategories = MutableStateFlow<List<ResProductsCategory.ListBean>?>(null)
    val mCategories: StateFlow<List<ResProductsCategory.ListBean>?> = _mCategories.asStateFlow()

    private val _mContent = MutableStateFlow<List<ResArticleList.ListBean>?>(null)
    val mContent: StateFlow<List<ResArticleList.ListBean>?> = _mContent.asStateFlow()

    private val _mSelectedCategoryId = MutableStateFlow("")

    private val mModel = ProductsModel()
    private var mPage = 1
    private var mIsLoadMore = false
    /** 请求序号：旧响应到达时与当前序号不一致则直接丢弃，避免乱序覆盖列表 */
    private var mRequestId = 0
    private val mAllContent = mutableListOf<ResArticleList.ListBean>()
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
                // 离线缓存：保存最近一次的分类（含"全部"）
                OfflineCache.put(BaseApplication.getContext(), "category_products", all)
            }

            override fun onError(errorCode: Int, message: String) {
                // 请求失败（断网或服务器异常）时展示缓存的分类，保证 Tab 能正常显示并触发内容加载
                val cached = OfflineCache.get(
                    BaseApplication.getContext(), "category_products",
                    object : TypeToken<List<ResProductsCategory.ListBean>>() {})
                if (!cached.isNullOrEmpty()) {
                    _mCategories.value = cached
                }
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
        val requestId = ++mRequestId
        mModel.loadContent("", channelId, mPage, pageSize, "publishtime", "desc",
            object : ApiCall.ApiCallBack<ResArticleList> {
                override fun onSuccess(result: ResArticleList) {
                    // 旧请求响应：已被更新的请求取代，直接丢弃
                    if (requestId != mRequestId) return
                    showLoading(false)
                    mIsLoadMore = false
                    val list = result.list
                    if (list != null) {
                        val filteredList = list
                        if (!isLoadMore) {
                            mAllContent.clear()
                        }
                        mAllContent.addAll(filteredList)
                        sortContent()
                        mPage++
                        // 离线缓存：保存最近一次的内容列表
                        OfflineCache.saveList(BaseApplication.getContext(), "products", channelId, mAllContent)
                    }
                }

                override fun onError(errorCode: Int, message: String) {
                    if (requestId != mRequestId) return
                    showLoading(false)
                    mIsLoadMore = false
                    // 请求失败（断网或服务器异常）时展示缓存的内容列表，实现"离线可读"
                    val cached = OfflineCache.loadList(BaseApplication.getContext(), "products", channelId)
                    if (!cached.isNullOrEmpty()) {
                        mAllContent.clear()
                        mAllContent.addAll(cached)
                        sortContent()
                    }
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
