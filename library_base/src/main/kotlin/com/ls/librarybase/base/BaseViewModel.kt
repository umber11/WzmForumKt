package com.ls.librarybase.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel : ViewModel() {

    private val _toastText = MutableSharedFlow<String>()
    val toastText: SharedFlow<String> = _toastText.asSharedFlow()

    private val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading.asStateFlow()

    fun showToast(text: String?) {
        if (text.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            _toastText.emit(text)
        }
    }

    fun showLoading(b: Boolean) {
        _showLoading.value = b
    }
}
