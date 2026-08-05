package com.ls.network

import android.util.Log
import com.ls.network.bean.ResBase
import com.ls.network.config.ErrorStatusConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiCall {
    private const val TAG = "ApiCall"

    interface ApiCallBack<T> {
        fun onSuccess(result: T)
        fun onError(errorCode: Int, message: String)
    }

    fun <T> enqueueSimple(call: Call<T>, callBack: ApiCallBack<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callBack.onSuccess(response.body()!!)
                } else {
                    callBack.onError(ErrorStatusConfig.ERROR_STATUS_NETWORK_FAIL, "网络异常")
                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callBack.onError(ErrorStatusConfig.ERROR_STATUS_NETWORK_FAIL, "网络异常")
            }
        })
    }

    fun <T> enqueueCommon(call: Call<ResBase<T>>, callBack: ApiCallBack<ResBase<T>>) {
        call.enqueue(object : Callback<ResBase<T>> {
            override fun onResponse(call: Call<ResBase<T>>, response: Response<ResBase<T>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.code == 1) {
                        Log.i(TAG, "onResponse: 数据请求成功")
                        callBack.onSuccess(body)
                    } else if (body != null) {
                        callBack.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, body.msg ?: "服务器异常")
                    } else {
                        callBack.onError(ErrorStatusConfig.ERROR_STATUS_SERVER_ERROR, "服务器异常")
                    }
                } else {
                    callBack.onError(ErrorStatusConfig.ERROR_STATUS_NETWORK_FAIL, "网络异常")
                }
            }

            override fun onFailure(call: Call<ResBase<T>>, throwable: Throwable) {
                callBack.onError(ErrorStatusConfig.ERROR_STATUS_NETWORK_FAIL, "网络异常")
            }
        })
    }
}
