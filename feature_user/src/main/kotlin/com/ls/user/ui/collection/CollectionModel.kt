package com.ls.user.ui.collection

import android.util.Log
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import com.ls.user.api.UserApiServiceProvider
import com.ls.user.bean.ResCollectionList

class CollectionModel {

    companion object {
        private const val TAG = "CollectionModel"
    }

    private val mApiService = UserApiServiceProvider.apiService

    fun loadCollectionList(callback: ApiCall.ApiCallBack<ResCollectionList>) {
        var token = UserManager.getInstance().token
        if (token == null) token = ""

        val params = mapOf(
            "type" to "archives",
            "page" to "1",
            "limit" to "10"
        )

        Log.d(TAG, "token: $token")

        ApiCall.enqueueCommon(
            mApiService.collectionList(token, params),
            object : ApiCall.ApiCallBack<ResBase<ResCollectionList>> {
                override fun onSuccess(result: ResBase<ResCollectionList>) {
                    val data = result.data
                    if (data != null) {
                        callback.onSuccess(data)
                    } else {
                        callback.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, "数据为空")
                    }
                }

                override fun onError(errorCode: Int, message: String) {
                    callback.onError(errorCode, message)
                }
            }
        )
    }
}
