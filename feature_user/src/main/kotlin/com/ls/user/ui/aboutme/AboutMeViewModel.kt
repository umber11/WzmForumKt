package com.ls.user.ui.aboutme

import com.ls.librarybase.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * 关于我们页 ViewModel，组装并暴露版本信息文本。
 */
class AboutMeViewModel : BaseViewModel() {
    private val _mVersionLabel = MutableStateFlow("")
    val mVersionLabel: StateFlow<String> = _mVersionLabel.asStateFlow()
    private val mModel = AboutMeModel()

    init {
        val versionCode = mModel.versionCode
        val versionName = mModel.versionName
        val label = "版本信息：v$versionName-$versionCode"
        _mVersionLabel.value = label
    }
}
