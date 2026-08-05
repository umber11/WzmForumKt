package com.ls.librarybase.base

interface IRequestCallback<T> {

    fun onLoadSuccess(data: T)
    fun onLoadFailure(errorCode: Int, message: String)
}
