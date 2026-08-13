package com.ls.librarybase.base
/**
 * 请求回调接口：定义数据加载成功与失败两个回调方法。
 */
interface IRequestCallback<T> {

    fun onLoadSuccess(data: T)
    fun onLoadFailure(errorCode: Int, message: String)
}
