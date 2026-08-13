package com.ls.products.ui.products

import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.products.api.ProductsApiService
import com.ls.products.api.ProductsApiServiceProvider
import com.ls.products.bean.ResProductsCategory
/**
 * 产品数据层：请求频道分类与内容列表，含参数组装与结果校验。
 */
class ProductsModel {
    private val mApiService: ProductsApiService = ProductsApiServiceProvider.apiService

    fun loadCategories(callback: ApiCall.ApiCallBack<ResBase<ResProductsCategory>>) {
        val token = UserManager.getInstance().token ?: ""
        ApiCall.enqueueCommon(mApiService.productsCategory(token), callback)
    }

    fun loadContent(
        keyword: String?,
        channelId: String?,
        page: Int,
        limit: Int,
        sortField: String,
        sortOrder: String,
        callback: ApiCall.ApiCallBack<ResArticleList>
    ) {
        val params = mutableMapOf<String, String>()
        params["keyword"] = keyword ?: ""
        params["channel_id"] = channelId ?: ""
        params["limit"] = limit.toString()
        params["page"] = page.toString()
        params["sort_field"] = sortField
        params["sort_order"] = sortOrder
        ApiCall.enqueueSimple(mApiService.contentList(params), object : ApiCall.ApiCallBack<ResArticleList> {
            override fun onSuccess(result: ResArticleList) {
                if (result.code == 1 || result.code == 1001) {
                    callback.onSuccess(result)
                } else {
                    callback.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, result.msg ?: "错误")
                }
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onError(errorCode, message)
            }
        })
    }
}
