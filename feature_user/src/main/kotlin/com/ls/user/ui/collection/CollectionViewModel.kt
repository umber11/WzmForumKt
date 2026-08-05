package com.ls.user.ui.collection

import android.util.Log
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.utils.CollectionStorage
import com.ls.librarybase.utils.CollectionTracker
import com.ls.network.ApiCall
import com.ls.user.bean.ResCollectionList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectionViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "CollectionViewModel"
    }

    private val _mArticles = MutableStateFlow<List<ResArticleList.ListBean>?>(null)
    val mArticles: StateFlow<List<ResArticleList.ListBean>?> = _mArticles.asStateFlow()

    private val mModel = CollectionModel()

    fun loadCollectionList() {
        showLoading(true)
        mModel.loadCollectionList(object : ApiCall.ApiCallBack<ResCollectionList> {
            override fun onSuccess(result: ResCollectionList) {
                showLoading(false)
                val items = result.collectionList?.data
                if (!items.isNullOrEmpty()) {
                    Log.d(TAG, "server list size=${items.size}")
                    val articles = mutableListOf<ResArticleList.ListBean>()
                    for (item in items) {
                        val bean = ResArticleList.ListBean()
                        bean.id = item.aid
                        bean.title = item.title
                        bean.image = item.image
                        bean.publishtime = item.create_date ?: ""
                        bean.iscollection = 1
                        articles.add(bean)
                        CollectionStorage.put(item.aid, bean)
                        CollectionTracker.setCollected(item.aid, true, 0)
                    }
                    _mArticles.value = articles
                } else {
                    Log.d(TAG, "server returned empty, fallback to local")
                    loadFromLocal()
                }
            }

            override fun onError(errorCode: Int, message: String) {
                showLoading(false)
                Log.e(TAG, "onError: errorCode=$errorCode, message=$message")
                loadFromLocal()
            }
        })
    }

    private fun loadFromLocal() {
        val local = CollectionStorage.getAll()
        Log.d(TAG, "loadFromLocal size=${local.size}")
        _mArticles.value = local
        showLoading(false)
    }

    fun refreshLocal() {
        val local = CollectionStorage.getAll()
        val current = _mArticles.value
        if (current == null) {
            _mArticles.value = local
        } else {
            val merged = LinkedHashMap<Int, ResArticleList.ListBean>()
            for (item in current) {
                merged[item.id] = item
            }
            for (item in local) {
                merged[item.id] = item
            }
            _mArticles.value = merged.values.toList()
        }
        Log.d(TAG, "refreshLocal merged size=${_mArticles.value?.size}")
    }
}
