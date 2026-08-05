package com.ls.user.ui.editinfo

import android.net.Uri
import com.ls.librarybase.base.BaseViewModel
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.network.bean.ResBase
import com.ls.user.bean.ResUpload
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class EditUserViewModel : BaseViewModel() {
    private val mModel = EditUserModel()

    private val _mNickName = MutableStateFlow<String?>(null)
    val mNickName: StateFlow<String?> = _mNickName.asStateFlow()
    private val _mBio = MutableStateFlow<String?>(null)
    val mBio: StateFlow<String?> = _mBio.asStateFlow()
    private val _mAvatarUrl = MutableStateFlow<String?>(null)
    val mAvatarUrl: StateFlow<String?> = _mAvatarUrl.asStateFlow()
    private val _mAction = MutableSharedFlow<EditUserAction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val mAction: SharedFlow<EditUserAction> = _mAction.asSharedFlow()
    private var mPendingAvatarUrl: String? = null

    init {
        refresh()
    }

    private fun refresh() {
        if (mModel.isLogin()) {
            val userInfo = mModel.userInfo
            _mNickName.value = userInfo?.nickname
            _mBio.value = userInfo?.bio
            _mAvatarUrl.value = userInfo?.avatar
        } else {
            _mNickName.value = null
            _mBio.value = null
            _mAvatarUrl.value = null
        }
    }

    fun onSaveUserInfo() {
        if (isChange()) {
            showLoading(true)
            val avatarUrl = mPendingAvatarUrl ?: _mAvatarUrl.value
            val nickName = _mNickName.value
            val bio = _mBio.value
            mModel.updateUserInfo(avatarUrl, nickName, bio, object : IRequestCallback<ResBase<Void>> {
                override fun onLoadSuccess(datas: ResBase<Void>) {
                    showToast(datas.msg)
                    showLoading(false)
                    LoginStateBus.post(true)
                    LoginStateBus.postUserInfoRefresh()
                    _mAction.tryEmit(EditUserAction.FINISH)
                }

                override fun onLoadFailure(errorCode: Int, message: String) {
                    showLoading(false)
                    showToast(message)
                }
            })
        }
    }

    fun isChange(): Boolean {
        var change = false
        val userInfo = mModel.userInfo
        if (mPendingAvatarUrl != null) {
            change = true
        }
        val avatarUrl = _mAvatarUrl.value
        if (avatarUrl != null && avatarUrl != userInfo?.avatar) {
            change = true
        }
        val nickName = _mNickName.value
        if (nickName != null && nickName != userInfo?.nickname) {
            change = true
        }
        val bio = _mBio.value
        if (bio != null && bio != userInfo?.bio) {
            change = true
        }
        return change
    }

    fun uploadAvatar(uri: Uri) {
        showLoading(true)
        mModel.updateFile(uri, object : IRequestCallback<ResUpload> {
            override fun onLoadSuccess(datas: ResUpload) {
                showToast("上传成功")
                showLoading(false)
                mPendingAvatarUrl = datas.fullurl
            }

            override fun onLoadFailure(errorCode: Int, message: String) {
                showLoading(false)
                showToast(message)
            }
        })
    }

    fun setNickName(value: String?) {
        _mNickName.value = value
    }

    fun setBio(value: String?) {
        _mBio.value = value
    }

    enum class EditUserAction {
        FINISH
    }
}
